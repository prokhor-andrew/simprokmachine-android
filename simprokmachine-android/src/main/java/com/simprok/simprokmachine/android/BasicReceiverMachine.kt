package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.api.Mapper
import com.simprok.simprokmachine.api.Ward

class BasicReceiverMachine<Input, Output>(
    private val function: Mapper<ReceiverData, Ward<Output>>
) : ChildReceiverMachine<Input, Output> {

    override fun outward(output: ReceiverData): Ward<Output> = function(output)
}