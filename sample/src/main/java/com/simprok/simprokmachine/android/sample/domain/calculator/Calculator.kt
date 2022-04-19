package com.simprok.simprokmachine.android.sample.domain.calculator

import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Calculator(private var state: Int) : ChildMachine<Unit, Int> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    override suspend fun process(input: Unit?, callback: Handler<Int>) {
        if (input != null) {
            state += 1
        }
        callback(state)
    }
}