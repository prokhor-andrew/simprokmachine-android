package com.simprok.simprokmachine.android

import androidx.lifecycle.LifecycleOwner


sealed interface LifecycleOwnerMachine<Input, Output> : ComponentMachine<Input, Output> {

    sealed interface Data<Input>

    sealed class Lifecycle<Input>(val owner: LifecycleOwner) : Data<Input> {

        class Created<Input>(owner: LifecycleOwner) : Lifecycle<Input>(owner)

        class Destroyed<Input>(owner: LifecycleOwner) : Lifecycle<Input>(owner)
    }

    data class ParentInput<Input>(val input: Input) : Data<Input>

    companion object
}