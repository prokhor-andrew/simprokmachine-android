package com.simprok.simprokmachine.android.implementation

import com.simprok.simprokmachine.android.*
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine
import kotlinx.coroutines.Dispatchers.IO

internal fun <Input, Output> ComponentMachine<Input, Output>.machine(

): Machine<ComponentData.ToMachines<Input, Output>, Output> {
    when (this) {
        is ParentActivityMachine<Input, Output> -> return child.machine()
        is ParentLifecycleOwnerMachine<Input, Output> -> return child.machine()
        is ParentReceiverMachine<Input, Output> -> return child.machine()
        is ChildActivityMachine<Input, Output> -> {
            return BasicMachine(dispatcher) { input, callback ->
                when (input) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                        when (input.cycle) {
                            is ComponentLifecycle.ActivityData.Created -> {
                                process(
                                    ActivityMachine.Lifecycle.Created(input.cycle.activity),
                                    callback
                                )
                            }
                            is ComponentLifecycle.ActivityData.Destroyed -> {
                                process(
                                    ActivityMachine.Lifecycle.Destroyed(input.cycle.activity),
                                    callback
                                )
                            }
                            is ComponentLifecycle.OwnerData,
                            is ComponentLifecycle.ReceivedData -> {
                                // do nothing
                            }
                        }
                    }
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> {
                        process(ActivityMachine.ParentInput(input.input), callback)
                    }
                    null -> process(null, callback)
                }
            }
        }
        is ChildLifecycleOwnerMachine<Input, Output> -> {
            return BasicMachine(dispatcher) { input, callback ->
                when (input) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                        when (input.cycle) {
                            is ComponentLifecycle.OwnerData.Created -> {
                                process(
                                    LifecycleOwnerMachine.Lifecycle.Created(input.cycle.owner),
                                    callback
                                )
                            }
                            is ComponentLifecycle.OwnerData.Destroyed -> {
                                process(
                                    LifecycleOwnerMachine.Lifecycle.Destroyed(input.cycle.owner),
                                    callback
                                )
                            }
                            is ComponentLifecycle.ActivityData,
                            is ComponentLifecycle.ReceivedData -> {
                                // do nothing
                            }
                        }
                    }
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> {
                        process(LifecycleOwnerMachine.ParentInput(input.input), callback)
                    }
                    null -> process(null, callback)
                }
            }
        }
        is ChildReceiverMachine<Input, Output> -> {
            return BasicMachine<ComponentData.ToMachines<Input, Output>, ReceiverData>(IO) { input, callback ->
                when (input) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                        when (input.cycle) {
                            is ComponentLifecycle.ActivityData,
                            is ComponentLifecycle.OwnerData -> {
                                // do nothing
                            }
                            is ComponentLifecycle.ReceivedData -> callback(input.cycle.data)
                        }
                    }
                    is ComponentData.ToMachines.ParentInput<Input, Output>,
                    null -> {
                        // do nothing
                    }
                }
            }.outward { outward(it) }
        }
        is ConstructableActivityMachine<Input, Output> -> return machine
        is ConstructableLifecycleOwnerMachine<Input, Output> -> return machine
        is ConstructableComponentMachine<Input, Output> -> return machine
        is ConstructableReceiverMachine -> return machine
    }
}


internal fun <Input, Output> AndroidRootMachine<Input, Output>.root(
    helper: HelperMachine
): Machine<Input, Output> {
    return Machine.merge<ComponentData<Input, Output>, ComponentData<Input, Output>>(
        helper.outward {
            Ward.set<ComponentData<Input, Output>>(
                ComponentData.ToMachines.LifecycleInput(it)
            )
        }.inward { Ward.set() },
        component.machine().outward {
            Ward.set<ComponentData<Input, Output>>(
                ComponentData.FromMachines(it)
            )
        }.inward {
            when (it) {
                is ComponentData.FromMachines<Input, Output> -> Ward.set()
                is ComponentData.ToMachines<Input, Output> -> Ward.set(it)
            }
        }
    ).redirect {
        when (it) {
            is ComponentData.FromMachines<Input, Output> -> Direction.Prop()
            is ComponentData.ToMachines<Input, Output> -> Direction.Back(it)
        }
    }.outward {
        when (it) {
            is ComponentData.FromMachines<Input, Output> -> Ward.set(it.output)
            is ComponentData.ToMachines<Input, Output> -> Ward.set()
        }
    }.inward { Ward.set() }
}