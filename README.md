# simprokmachine-android

## Introduction

This library is the extension of [simprokmachine-kotlin](https://github.com/simprok-dev/simprokmachine-kotlin). It provides a utility class to connect android's ```Activity``` to [RootMachine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/RootMachine) object via ```ViewModel```. 

## Usage

### Activity

After installation you get two extension methods in your activity.

```Kotlin
fun <State, Event> AppCompatActivity.start(
    assemble: Mapper<WidgetMachine<State, Event>, Assembly>
)
```

and 

```Kotlin
fun <State, Event> AppCompatActivity.startWithRenderer(
    renderer: BiHandler<State?, Handler<Event>>,
    assemble: Mapper<WidgetMachine<State, Event>, Assembly>
)
```

The [start()](https://github.com/simprok-dev/simprokandroid/wiki/AppCompatActivityExt#start-without-renderer) method builds your applicaton's machine hierarchy while [startWithRenderer()](https://github.com/simprok-dev/simprokandroid/wiki/AppCompatActivityExt#start-with-renderer) also adds your ```Activity``` as a [ChildMachine](https://github.com/simprok-dev/simprokmachine-kotlin/wiki/ChildMachine) in this hierarchy enabling you to render UI according to the coming input.

This is done via special [Assembly](https://github.com/simprok-dev/simprokandroid/wiki/Assembly) object and its inhertitor - ```MachineAssembly```.

```Kotlin
start<ActivityInput, ActivityOutput> { activityMachine -> 
    MachineAssembly.create(
        appMachine, // merge other machines with the activityMachine to receive appMachine
        appInitialInput,
        appMapperFunction
    )
}
```

### Fragment

Inside your fragments you can call [filterMapInput()](https://github.com/simprok-dev/simprokandroid/wiki/FragmentExt#filtermapinput), [filterMapOutput()](https://github.com/simprok-dev/simprokandroid/wiki/FragmentExt#filtermapoutput) or [render()](https://github.com/simprok-dev/simprokandroid/wiki/FragmentExt#render) to filter input flow, filter output flow or subscribe to the flow to render fragment's UI.

```Kotlin
fun <ParentInput, ParentOutput, ChildOutput> Fragment.filterMapOutput(
    func: Mapper<ChildOutput, FilterMap<ParentOutput>>
): RenderingObject<ParentInput, ChildOutput>
```

```Kotlin
fun <ParentInput, ParentOutput, ChildInput> Fragment.filterMapInput(
    func: Mapper<ParentInput?, FilterMap<ChildInput?>>
): RenderingObject<ChildInput, ParentOutput>
```

```Kotlin
fun <Input, Output> Fragment.render(func: BiHandler<Input?, Handler<Output>>)
```


[RenderingObject](https://github.com/simprok-dev/simprokandroid/wiki/RenderingObject) is class with builder pattern inside if more filtering operators application needed.

## Example

Check out the [sample](https://github.com/simprok-dev/simprokmachine-android/tree/main/sample) for more information about API and how to use it.


## Installation

Add this in your project's gradle file:

```groovy
implementation 'com.github.simprok-dev:simprokmachine-android:1.1.4'
```

and this in your settings.gradle file:

```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

## What to check next

Check out [simprokcore-android](https://github.com/simprok-dev/simprokcore-android) - an architectural framework that connects [simprokcore-kotlin](https://github.com/simprok-dev/simprokcore-kotlin) the same way this library does.
