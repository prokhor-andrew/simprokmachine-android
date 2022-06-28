package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.ComponentData
import com.simprok.simprokmachine.android.implementation.machine
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine


fun <Output, Input, R> ComponentMachine<R, Output>.inward(
    function: Mapper<Input, Ward<R>>
): ComponentMachine<Input, Output> = ConstructableComponentMachine(
    machine().inward {
        when (it) {
            is ComponentData.ToMachines.LifecycleInput<Input, Output> -> {
                Ward.set(ComponentData.ToMachines.LifecycleInput(it.cycle))
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

fun <Input, Output, R> ComponentMachine<Input, Output>.outward(
    function: Mapper<Output, Ward<R>>
): ComponentMachine<Input, R> = ConstructableComponentMachine(
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

fun <Input, Output> ComponentMachine<Input, Output>.redirect(
    function: Mapper<Output, Direction<Input>>
): ComponentMachine<Input, Output> = ConstructableComponentMachine(
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
    vararg machines: ComponentMachine<Input, Output>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeArray(
    machines: Array<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeSet(
    machines: Set<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> mergeList(
    machines: List<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = ComponentMachine.mergeList(machines.toList())

fun <Input, Output> ComponentMachine.Companion.merge(
    vararg machines: ComponentMachine<Input, Output>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ComponentMachine.Companion.mergeArray(
    machines: Array<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ComponentMachine.Companion.mergeSet(
    machines: Set<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = mergeList(machines.toList())

fun <Input, Output> ComponentMachine.Companion.mergeList(
    machines: List<ComponentMachine<Input, Output>>
): ComponentMachine<Input, Output> = ConstructableComponentMachine(
    Machine.mergeList(machines.toList().map { it.machine() })
)

fun <Input, Output> ComponentMachine<Input, Output>.mergeWith(
    vararg machines: Machine<Input, Output>
): ComponentMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ComponentMachine<Input, Output>.mergeWithArray(
    machines: Array<Machine<Input, Output>>
): ComponentMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ComponentMachine<Input, Output>.mergeWithSet(
    machines: Set<Machine<Input, Output>>
): ComponentMachine<Input, Output> = mergeWithList(machines.toList())

fun <Input, Output> ComponentMachine<Input, Output>.mergeWithList(
    machines: List<Machine<Input, Output>>
): ComponentMachine<Input, Output> {
    val mapped: List<Machine<ComponentData.ToMachines<Input, Output>, Output>> =
        machines.toList().map { machine ->
            machine.inward {
                when (it) {
                    is ComponentData.ToMachines.LifecycleInput<Input, Output> -> Ward.set()
                    is ComponentData.ToMachines.ParentInput<Input, Output> -> Ward.set(it.input)
                }
            }
        }
    return ConstructableComponentMachine(
        Machine.mergeList(mapped.plus(machine()))
    )
}