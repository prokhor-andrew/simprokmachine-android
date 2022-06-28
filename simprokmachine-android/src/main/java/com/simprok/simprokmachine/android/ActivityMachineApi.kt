package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.ComponentData
import com.simprok.simprokmachine.android.implementation.ComponentLifecycle
import com.simprok.simprokmachine.android.implementation.machine
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine

fun <Output, Input, R> ActivityMachine<R, Output>.inward(
    function: Mapper<Input, Ward<R>>
): ActivityMachine<Input, Output> = ConstructableActivityMachine(
    machine().inward {
        when (it) {
            is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                when (it.cycle) {
                    is ComponentLifecycle.ActivityData -> {
                        Ward.set(ComponentData.ToMachines.LifecycleInput(it.cycle))
                    }
                    is ComponentLifecycle.OwnerData,
                    is ComponentLifecycle.ReceivedData -> Ward.set()
                }
            }
            is ComponentData.ToMachines.ParentInput<Input, Output> -> {
                Ward.set(
                    function(it.input).values.map { input ->
                        ComponentData.ToMachines.ParentInput(input)
                    }
                )
            }
        }
    }
)

fun <Input, Output, R> ActivityMachine<Input, Output>.outward(
    function: Mapper<Output, Ward<R>>
): ActivityMachine<Input, R> = ConstructableActivityMachine(
    machine().outward(function).inward {
        when (it) {
            is ComponentData.ToMachines.ParentInput<Input, R> -> {
                Ward.set(ComponentData.ToMachines.ParentInput(it.input))
            }
            is ComponentData.ToMachines.LifecycleInput<Input, R> -> {
                Ward.set(ComponentData.ToMachines.LifecycleInput(it.cycle))
            }
        }
    }
)

fun <Input, Output> ActivityMachine<Input, Output>.redirect(
    function: Mapper<Output, Direction<Input>>
): ActivityMachine<Input, Output> = ConstructableActivityMachine(
    machine().redirect {
        when (val result = function(it)) {
            is Direction.Back<Input> -> {
                Direction.Back(
                    result.values.map { input -> ComponentData.ToMachines.ParentInput(input) }
                )
            }
            is Direction.Prop<Input> -> Direction.Prop()
        }
    }
)

fun <Input, Output> merge(
    vararg machines: ActivityMachine<Input, Output>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeArray(
    machines: Array<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeSet(
    machines: Set<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeList(
    machines: List<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = ActivityMachine.mergeList(machines.toList())

fun <Input, Output> ActivityMachine.Companion.merge(
    vararg machines: ActivityMachine<Input, Output>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ActivityMachine.Companion.mergeArray(
    machines: Array<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ActivityMachine.Companion.mergeSet(
    machines: Set<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ActivityMachine.Companion.mergeList(
    machines: List<ActivityMachine<Input, Output>>
): ActivityMachine<Input, Output> = ConstructableActivityMachine(
    Machine.mergeList(machines.toList().map { it.machine() })
)

fun <Input, Output> ActivityMachine<Input, Output>.mergeWith(
    vararg machines: Machine<Input, Output>
): ActivityMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ActivityMachine<Input, Output>.mergeWithArray(
    machines: Array<Machine<Input, Output>>
): ActivityMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ActivityMachine<Input, Output>.mergeWithSet(
    machines: Set<Machine<Input, Output>>
): ActivityMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ActivityMachine<Input, Output>.mergeWithList(
    machines: List<Machine<Input, Output>>
): ActivityMachine<Input, Output> {
    val mapped: List<Machine<ComponentData.ToMachines<Input, Output>, Output>> =
        machines.toList().map { machine ->
            machine.inward {
                when (it) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> Ward.set()
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> Ward.set(it.input)
                }
            }
        }
    return ConstructableActivityMachine(
        Machine.mergeList(mapped.plus(machine()))
    )
}