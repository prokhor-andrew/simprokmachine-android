package com.simprok.simprokmachine.android

import androidx.lifecycle.LifecycleOwner
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.Machine

interface BasicLifecycleOwnerMachineType<Input, Output> :
    ParentLifecycleOwnerMachine<Input, Output> {

    val strategy: BufferStrategy<Input> get() = BufferStrategy.TakeLast()

    fun supply(owner: LifecycleOwner): Machine<Input, Output>?

    fun process(owner: LifecycleOwner, input: Input?, callback: Handler<Output>)

    override val child: LifecycleOwnerMachine<Input, Output>
        get() = BasicLifecycleOwnerMachineObject(strategy, ::supply)
}