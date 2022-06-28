package com.simprok.simprokmachine.android.implementation

internal sealed interface ComponentData<Input, Output> {

    sealed interface ToMachines<Input, Output>: ComponentData<Input, Output> {

        data class ParentInput<Input, Output>(
            val input: Input
        ): ToMachines<Input, Output>

        class LifecycleInput<Input, Output>(
            val cycle: ComponentLifecycle
        ): ToMachines<Input, Output>
    }

    data class FromMachines<Input, Output>(val output: Output): ComponentData<Input, Output>
}