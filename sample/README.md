# [simprokmachine](https://github.com/simprok-dev/simprokmachine-android) sample

## Introduction

This sample is created to showcase the main features of the framework. 


It is hard to demonstrate the functionality of ```simprokmachine``` without an example as the concept behind it affects the way you design, plan and code your application.


The sample is divided into 15 easy steps demonstrating the flow of the app development and API usage.


## Disclaimer

This sample's main idea is to showcase classes, operators and how to use them. It is more about "how to do" instead of "what to do". 


Neither it guarantees that everything here could or should be used in a real life project nor it forces you into using any certain ideas. 


To see our recommended architecture check our [simprokcore framework](https://github.com/simprok-dev/simprokcore-kotlin) as well as [simprokcore-android](https://github.com/simprok-dev/simprokcore-android) for android.



## Step 0 - Describe application's behavior

Let's assume we want to create a counter app that shows a number on the screen and logcat each time it is incremented. 


When we reopen the app we want to see the same number. So the state must be saved in a persistent storage. 


## Step 1 - Describe application's logic containers

- ```MainFragment``` - rendering UI.
    - Input: String
    - Output: Unit
- ```Logger``` - printing the number.
    - Input: String
    - Output: Nothing
- ```StorageReader``` - reading from ```SharedPreferences```.
    - Input: Unit
    - Output: Int
- ```StorageWriter``` - writing to ```SharedPreferences```.
    - Input: Int
    - Output: Nothing
- ```Calculator``` - incrementing the number.
    - Input: Unit
    - Output: Int


![Components](https://github.com/simprok-dev/simprokmachine-android/blob/main/sample/images/components1.drawio.png)


## Step 2 - Describe data flows

Build a complete tree of all machines to visualize the connections.

![A tree](https://github.com/simprok-dev/simprokmachine-android/blob/main/sample/images/components2.drawio.png)


Two instances that we haven't talked about are:
- ```Display``` 
    - Input: AppEvent
    - Output: AppEvent
- ```Domain```
    - Input: AppEvent
    - Output: AppEvent


They are used as intermediate layers. 


```AppEvent``` is a custom type for communication between ```Domain``` and ```Display```.


## Step 3 - Code data types

We only need ```AppEvent``` as the rest is supported by Kotlin.

```Kotlin
sealed interface AppEvent {
    object WillChangeState : AppEvent

    data class DidChangeState(val value: Int) : AppEvent
}
```

## Step 4 - Code Logger


```Kotlin
class Logger : ChildMachine<String, Nothing> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    override suspend fun process(input: String?, callback: Handler<Nothing>) {
        println(input ?: "loading")
    }
}
```

[ChildMachine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/ChildMachine) - is a container for your logic. It accepts input and handles it. When needed - emits output.

## Step 5 - Code MainFragment

Create ```MainFragment```.

```Kotlin
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.activity_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.textView)
        val button = view.findViewById<Button>(R.id.button)

        render<String, Unit> { input, callback ->
            textView.text = input ?: "loading"
            button.setOnClickListener { callback(Unit) }
        }
    }
}
```

[render()](https://github.com/simprok-dev/simprokandroid/wiki/FragmentExt#render) - is a method that connects input flow of the app to your fragment and emits output if needed. 

In this case ```String``` is your input data and ```Unit``` is your output.


## Step 6 - Code StorageWriter

Repeat the same steps for ```StorageWriter```.

```Kotlin
class StorageWriter(private val prefs: SharedPreferences) : ChildMachine<Int, Nothing> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    override suspend fun process(input: Int?, callback: Handler<Nothing>) {
        if (input != null) {
            prefs.edit()
                .putInt(storageKey, input)
                .apply()
        }
    }
}
```

## Step 7 - Code Display 
    
Code ```Display``` class to connect ```Logger``` and ```StorageWriter``` together.
    
```Kotlin
class Display(private val prefs: SharedPreferences) : ParentMachine<AppEvent, AppEvent> {

    override val child: Machine<AppEvent, AppEvent>
        get() = merge(
            Logger().outward { Ward.set<AppEvent>() }.inward {
                when (it) {
                    is AppEvent.WillChangeState -> Ward.set()
                    is AppEvent.DidChangeState -> Ward.set(it.value.toString())
                }
            },
            StorageWriter(prefs).outward { Ward.set<AppEvent>() }.inward {
                when (it) {
                    is AppEvent.WillChangeState -> Ward.set()
                    is AppEvent.DidChangeState -> Ward.set(it.value)
                }
            }
        )
}
```

- [ParentMachine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/ParentMachine) - is an intermediate layer for your data flow. It passes input from the parent to the child and vice versa for the output.
- [inward()](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine#inward-operator) - maps parent input type into child input type or ignores it.
- [outward()](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine#outward-operator) - maps child output type into parent output type or ignores it.
- [Machine.merge()](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine#merge-with-varargs) - merges two or more machines into one.
    

    
## Step 8 - Code StorageReader and Calculator.

```Kotlin
class StorageReader(private val prefs: SharedPreferences) : ChildMachine<Unit, Int> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    override suspend fun process(input: Unit?, callback: Handler<Int>) {
        callback(prefs.getInt(storageKey, 0))
    }
}
```


```Kotlin
class Calculator(private var state: Int) : ChildMachine<Unit, Int> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    override suspend fun process(input: Unit?, callback: Handler<Int>) {
        if (input != null) {
            state += 1
        }
        callback(state)
    }
}
```

## Step 9 - Code Domain

Code a ```Domain``` class to connect ```StorageReader``` and ```Calculator```.


```Kotlin
class Domain(private val prefs: SharedPreferences) : ParentMachine<AppEvent, AppEvent> {

    override val child: Machine<AppEvent, AppEvent>
        get() {
            fun getCalculator(initial: Int): Machine<DomainInput, DomainOutput> {
                return Calculator(initial).outward {
                    Ward.set<DomainOutput>(DomainOutput.FromCalculator(it))
                }.inward {
                    when (it) {
                        is DomainInput.FromParent -> Ward.set(Unit)
                        is DomainInput.FromReader -> Ward.set()
                    }
                }
            }

            val reader: Machine<DomainInput, DomainOutput> = StorageReader(prefs).outward {
                Ward.set<DomainOutput>(DomainOutput.FromReader(it))
            }.inward {
                Ward.set()
            }

            val connectable: Machine<DomainInput, DomainOutput> =
                ConnectableMachine(BasicConnection.create(reader)) { state, input ->
                    when (input) {
                        is DomainInput.FromParent -> ConnectionType.Inward()
                        is DomainInput.FromReader -> ConnectionType.Reduce(
                            BasicConnection.create(getCalculator(input.value))
                        )
                    }
                }.redirect {
                    when (it) {
                        is DomainOutput.FromReader -> Direction.Back(DomainInput.FromReader(it.value))
                        is DomainOutput.FromCalculator -> Direction.Prop()
                    }
                }

            return connectable.outward {
                when (it) {
                    is DomainOutput.FromReader -> Ward.set()
                    is DomainOutput.FromCalculator -> Ward.set<AppEvent>(AppEvent.DidChangeState(it.value))
                }
            }.inward {
                when (it) {
                    is AppEvent.DidChangeState -> Ward.set()
                    is AppEvent.WillChangeState -> Ward.set(DomainInput.FromParent)
                }
            }
        }
}
```

Here we use two helper instances: ```DomainInput``` and ```DomainOutput```.

```Kotlin
sealed interface DomainInput {

    class FromReader(val value: Int) : DomainInput

    object FromParent : DomainInput
}
```

```Kotlin
sealed interface DomainOutput {

    class FromReader(val value: Int) : DomainOutput

    class FromCalculator(val value: Int) : DomainOutput
}
```

[redirect()](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine#redirect-operator) - depending on the output either passes it further to the root or sends an array of input data back to the child.
[ConnectableMachine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/ConnectableMachine) - dynamically creates and connects a set of machines.

## Step 10 - Code MainActivity


Create ```MainActivity``` and add ```Fragment``` in ```onCreate()```.

```Kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_layout)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment(), "tag")
                .commit()
        }
    }
}

```

## Step 11 - "Machines, assemble!"

Connect your activity and fragment.

```Kotlin

...
        
if (savedInstanceState == null) {
    ...

    start<String, Unit> { machine ->
        val prefs = application.getSharedPreferences("storage", MODE_PRIVATE)
        MachineAssembly.create(
            machine.outward<AppEvent, Unit, String> {
                Ward.set(AppEvent.WillChangeState)
            }.inward<AppEvent, String, AppEvent> {
                when (it) {
                    is AppEvent.DidChangeState -> Ward.set(it.value.toString())
                    is AppEvent.WillChangeState -> Ward.set()
                }
            }
        )
    }
}


```

```MachineAssembly``` - an object that takes your application's root machine and uses it in ```start()``` method.

[start()](https://github.com/simprok-dev/simprokandroid/wiki/AppCompatActivityExt#start-without-renderer) - starts the flow of your application.

[WidgetMachine](https://github.com/simprok-dev/simprokandroid/wiki/WidgetMachine) - special wrapper around normal [Machine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine) object that is used to connect ```Fragment```s or ```Activity``` to the flow, handling lifecycle issues via ```ViewModel``` under the hood.


## Step 12 - Connect Display and Domain

To connect [WidgetMachine](https://github.com/simprok-dev/simprokandroid/wiki/WidgetMachine) to normal [Machine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/Machine)'s use its [merge()](https://github.com/simprok-dev/simprokandroid/wiki/WidgetMachine#merge-set).


```Kotlin
start<String, Unit> { machine ->
    val prefs = application.getSharedPreferences("storage", MODE_PRIVATE)
    MachineAssembly.create(
        machine.outward<AppEvent, Unit, String> {
            ...
        }.inward<AppEvent, String, AppEvent> {
            ...
        }.mergeWith(
            Display(prefs),
            Domain(prefs)
        ).redirect { Direction.Back(it) }
    )
}
```

## Step 13 - Enjoy yourself for a couple of minutes

Run the app and see how things are working.


![result](https://github.com/simprok-dev/simprokmachine-android/blob/main/sample/images/results.gif)


## To sum up

- ```ChildMachine``` is a container for logic that handles input in a serial queue and may produce an output.
- ```ParentMachine``` is a proxy/intermediate class used for comfortable logic separation and as a place to apply operators.
- ```RootMachine``` is a top-level machine that starts and stops the flow.
- ```inward()``` is an operator to map the parent's input type into the child's input type or ignore it.
- ```outward()``` is an operator to map the child's output type into the parent's output type or ignore it.
- ```redirect()``` is an operator to either pass the output further to the root or map it into an array of inputs and send back to the child.
- ```merge()``` is an operator to merge two or more machines of the same input and output types.
- ```ConnectableMachine``` is a machine that is used to dynamically create and connect other machines.
- ```WidgetMachine``` is a special wrapper around normal machine that is used to connect ```Fragment```s or ```Activity``` to the flow, handling lifecycle issues via ```ViewModel``` under the hood.
- ```AppCompatActivity.start()``` - starts the flow of your application.
- ```Fragment.render()``` - is a method that connects input flow of the app to your fragment and emits output if needed. 


Refer to [wiki](https://github.com/simprok-dev/simprokmachine-kotlin/wiki) for more information.
