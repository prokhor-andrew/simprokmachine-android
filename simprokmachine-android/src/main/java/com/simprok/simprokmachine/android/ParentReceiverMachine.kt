package com.simprok.simprokmachine.android

interface ParentReceiverMachine<Input, Output> : ReceiverMachine<Input, Output> {

    val child: ReceiverMachine<Input, Output>
}