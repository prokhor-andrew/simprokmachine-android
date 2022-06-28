package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.api.Handler
import kotlinx.coroutines.CoroutineDispatcher

interface ChildActivityMachine<Input, Output> : ActivityMachine<Input, Output> {

    val dispatcher: CoroutineDispatcher

    fun process(data: ActivityMachine.Data<Input>?, callback: Handler<Output>)
}