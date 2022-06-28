package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.ComponentData
import com.simprok.simprokmachine.android.implementation.ComponentLifecycle
import com.simprok.simprokmachine.android.implementation.machine
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine

fun <Output, Input, R> LifecycleOwnerMachine<R, Output>.inward(
    function: Mapper<Input, Ward<R>>
): LifecycleOwnerMachine<Input, Output> = ConstructableLifecycleOwnerMachine(
    machine().inward {
        when (it) {
            is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                when (it.cycle) {
                    is ComponentLifecycle.OwnerData -> {
                        Ward.set(ComponentData.ToMachines.LifecycleInput(it.cycle))
                    }
                    is ComponentLifecycle.ActivityData,
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

fun <Input, Output, R> LifecycleOwnerMachine<Input, Output>.outward(
    function: Mapper<Output, Ward<R>>
): LifecycleOwnerMachine<Input, R> = ConstructableLifecycleOwnerMachine(
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

fun <Input, Output> LifecycleOwnerMachine<Input, Output>.redirect(
    function: Mapper<Output, Direction<Input>>
): LifecycleOwnerMachine<Input, Output> = ConstructableLifecycleOwnerMachine(
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
    vararg machines: LifecycleOwnerMachine<Input, Output>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeArray(
    machines: Array<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeSet(
    machines: Set<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeList(
    machines: List<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = LifecycleOwnerMachine.mergeList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine.Companion.merge(
    vararg machines: LifecycleOwnerMachine<Input, Output>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine.Companion.mergeArray(
    machines: Array<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine.Companion.mergeSet(
    machines: Set<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine.Companion.mergeList(
    machines: List<LifecycleOwnerMachine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = ConstructableLifecycleOwnerMachine(
    Machine.mergeList(machines.toList().map { it.machine() })
)

fun <Input, Output> LifecycleOwnerMachine<Input, Output>.mergeWith(
    vararg machines: Machine<Input, Output>
): LifecycleOwnerMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine<Input, Output>.mergeWithArray(
    machines: Array<Machine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine<Input, Output>.mergeWithSet(
    machines: Set<Machine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> LifecycleOwnerMachine<Input, Output>.mergeWithList(
    machines: List<Machine<Input, Output>>
): LifecycleOwnerMachine<Input, Output> {
    val mapped: List<Machine<ComponentData.ToMachines<Input, Output>, Output>> =
        machines.toList().map { machine ->
            machine.inward {
                when (it) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> Ward.set()
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> Ward.set(it.input)
                }
            }
        }
    return ConstructableLifecycleOwnerMachine(
        Machine.mergeList(mapped.plus(machine()))
    )
}