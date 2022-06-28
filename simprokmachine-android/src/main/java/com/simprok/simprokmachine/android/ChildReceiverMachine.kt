package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.api.Ward

interface ChildReceiverMachine<Input, Output> : ReceiverMachine<Input, Output> {

    fun outward(output: ReceiverData): Ward<Output>
}