package com.simprok.simprokmachine.android

import android.app.Activity
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.Machine

interface BasicActivityMachineType<Input, Output> : ParentActivityMachine<Input, Output> {

    val strategy: BufferStrategy<Input> get() = BufferStrategy.TakeLast()

    fun supply(activity: Activity): Machine<Input, Output>?

    fun process(activity: Activity, input: Input?, callback: Handler<Output>)

    override val child: ActivityMachine<Input, Output>
        get() = BasicActivityMachineObject(strategy, ::supply)
}