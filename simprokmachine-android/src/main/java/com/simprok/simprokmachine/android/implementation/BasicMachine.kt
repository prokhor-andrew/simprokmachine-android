package com.simprok.simprokmachine.android.implementation

import com.simprok.simprokmachine.api.BiHandler
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher

internal class BasicMachine<Input, Output>(
    override val dispatcher: CoroutineDispatcher,
    private val processor: BiHandler<Input?, Handler<Output>>
) : ChildMachine<Input, Output> {

    override fun process(input: Input?, callback: Handler<Output>) {
        processor(input, callback)
    }
}