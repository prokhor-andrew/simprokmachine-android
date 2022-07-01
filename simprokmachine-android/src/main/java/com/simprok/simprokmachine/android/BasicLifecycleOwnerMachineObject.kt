package com.simprok.simprokmachine.android

import androidx.lifecycle.LifecycleOwner
import com.simprok.simprokmachine.android.implementation.CustomRootMachine
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.api.Mapper
import com.simprok.simprokmachine.api.start
import com.simprok.simprokmachine.machines.Machine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class BasicLifecycleOwnerMachineObject<Input, Output>(
    private val strategy: BufferStrategy<Input> = BufferStrategy.TakeLast(),
    private val supplier: Mapper<LifecycleOwner, Machine<Input, Output>?>,
) : ChildLifecycleOwnerMachine<Input, Output> {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val buffer: MutableList<Input> = mutableListOf()

    private var job: Job? = null
    private var owner: LifecycleOwner? = null
    private var machine: CustomRootMachine<Input, Output>? = null

    override val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override fun process(data: LifecycleOwnerMachine.Data<Input>?, callback: Handler<Output>) {
        if (data == null) {
            return
        }
        when (data) {
            is LifecycleOwnerMachine.Lifecycle.Created<Input> -> {
                if (machine == null) {
                    val result = supplier(data.owner)
                    if (result != null) {
                        owner = data.owner
                        machine = CustomRootMachine(result, buffer.toList(), callback)
                        job = machine?.start(scope)
                        buffer.clear()
                    }
                }
            }
            is LifecycleOwnerMachine.Lifecycle.Destroyed<Input> -> {
                if (owner === data.owner) {
                    job?.cancel()
                    job = null
                    owner = null
                    machine = null
                }
            }
            is LifecycleOwnerMachine.ParentInput<Input> -> {
                if (machine != null) {
                    machine?.send(data.input)
                } else {
                    buffer(data.input)
                }
            }
        }
    }

    private fun buffer(input: Input) {
        val copy = buffer.toList()
        buffer.clear()
        buffer.addAll(strategy.reducer(copy, input))
    }
}