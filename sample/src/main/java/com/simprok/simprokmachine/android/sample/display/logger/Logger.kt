package com.simprok.simprokmachine.android.sample.display.logger

import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Logger : ChildMachine<String, Nothing> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    override fun process(input: String?, callback: Handler<Nothing>) {
        println(input ?: "loading")
    }
}