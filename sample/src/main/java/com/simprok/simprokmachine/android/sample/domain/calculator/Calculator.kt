package com.simprok.simprokmachine.android.sample.domain.calculator

import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Calculator : ChildMachine<CalculatorInput, Int> {

    private var state: Int? = null

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    override fun process(input: CalculatorInput?, callback: Handler<Int>) {
        if (input != null) {
            when (input) {
                is CalculatorInput.Increment -> {
                    val unwrapped = state
                    if (unwrapped != null) {
                        state = unwrapped + 1
                    }
                }
                is CalculatorInput.Initialize -> {
                    state = input.value
                }
            }
            val unwrapped = state
            if (unwrapped != null) {
                callback(unwrapped)
            }
        }
    }
}