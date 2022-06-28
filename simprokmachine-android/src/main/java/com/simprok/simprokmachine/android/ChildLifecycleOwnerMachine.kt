package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.api.Handler
import kotlinx.coroutines.CoroutineDispatcher


interface ChildLifecycleOwnerMachine<Input, Output> : LifecycleOwnerMachine<Input, Output> {

    val dispatcher: CoroutineDispatcher

    fun process(data: LifecycleOwnerMachine.Data<Input>?, callback: Handler<Output>)
}