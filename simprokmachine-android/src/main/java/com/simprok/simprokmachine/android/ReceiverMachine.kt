package com.simprok.simprokmachine.android

sealed interface ReceiverMachine<Input, Output>: ComponentMachine<Input, Output> {

    companion object
}