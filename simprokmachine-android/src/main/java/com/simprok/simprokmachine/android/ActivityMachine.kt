package com.simprok.simprokmachine.android

import android.app.Activity


sealed interface ActivityMachine<Input, Output> : ComponentMachine<Input, Output> {

    sealed interface Data<Input>

    sealed class Lifecycle<Input>(val activity: Activity) : Data<Input> {

        class Created<Input>(activity: Activity) : Lifecycle<Input>(activity)

        class Destroyed<Input>(activity: Activity) : Lifecycle<Input>(activity)
    }

    data class ParentInput<Input>(val input: Input) : Data<Input>

    companion object
}