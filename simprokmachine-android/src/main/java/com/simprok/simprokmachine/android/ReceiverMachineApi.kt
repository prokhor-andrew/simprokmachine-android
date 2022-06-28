package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.ComponentData
import com.simprok.simprokmachine.android.implementation.ComponentLifecycle
import com.simprok.simprokmachine.android.implementation.machine
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine


fun <Output, Input, R> ReceiverMachine<R, Output>.inward(
    function: Mapper<Input, Ward<R>>
): ReceiverMachine<Input, Output> = ConstructableReceiverMachine(
    machine().inward {
        when (it) {
            is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                when (it.cycle) {
                    is ComponentLifecycle.ReceivedData -> {
                        Ward.set(ComponentData.ToMachines.LifecycleInput(it.cycle))
                    }
                    is ComponentLifecycle.ActivityData,
                    is ComponentLifecycle.OwnerData -> Ward.set()
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

fun <Input, Output, R> ReceiverMachine<Input, Output>.outward(
    function: Mapper<Output, Ward<R>>
): ReceiverMachine<Input, R> = ConstructableReceiverMachine(
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

fun <Input, Output> ReceiverMachine<Input, Output>.redirect(
    function: Mapper<Output, Direction<Input>>
): ReceiverMachine<Input, Output> = ConstructableReceiverMachine(
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
    vararg machines: ReceiverMachine<Input, Output>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeArray(
    machines: Array<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeSet(
    machines: Set<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeList(
    machines: List<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = ReceiverMachine.mergeList(machines.toList())

fun <Input, Output> ReceiverMachine.Companion.merge(
    vararg machines: ReceiverMachine<Input, Output>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ReceiverMachine.Companion.mergeArray(
    machines: Array<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ReceiverMachine.Companion.mergeSet(
    machines: Set<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ReceiverMachine.Companion.mergeList(
    machines: List<ReceiverMachine<Input, Output>>
): ReceiverMachine<Input, Output> = ConstructableReceiverMachine(
    Machine.mergeList(machines.toList().map { it.machine() })
)

fun <Input, Output> ReceiverMachine<Input, Output>.mergeWith(
    vararg machines: Machine<Input, Output>
): ReceiverMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ReceiverMachine<Input, Output>.mergeWithArray(
    machines: Array<Machine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ReceiverMachine<Input, Output>.mergeWithSet(
    machines: Set<Machine<Input, Output>>
): ReceiverMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ReceiverMachine<Input, Output>.mergeWithList(
    machines: List<Machine<Input, Output>>
): ReceiverMachine<Input, Output> {
    val mapped: List<Machine<ComponentData.ToMachines<Input, Output>, Output>> =
        machines.toList().map { machine ->
            machine.inward {
                when (it) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> Ward.set()
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> Ward.set(it.input)
                }
            }
        }
    return ConstructableReceiverMachine(
        Machine.mergeList(mapped.plus(machine()))
    )
}