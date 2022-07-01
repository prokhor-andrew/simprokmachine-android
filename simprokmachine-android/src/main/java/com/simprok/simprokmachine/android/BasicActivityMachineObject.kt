package com.simprok.simprokmachine.android

import android.app.Activity
import com.simprok.simprokmachine.android.implementation.CustomRootMachine
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.api.Mapper
import com.simprok.simprokmachine.api.start
import com.simprok.simprokmachine.machines.Machine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job

class BasicActivityMachineObject<Input, Output>(
    private val strategy: BufferStrategy<Input> = BufferStrategy.TakeLast(),
    private val supplier: Mapper<Activity, Machine<Input, Output>?>,
) : ChildActivityMachine<Input, Output> {

    private val scope = CoroutineScope(IO)
    private val buffer: MutableList<Input> = mutableListOf()

    private var job: Job? = null
    private var activity: Activity? = null
    private var machine: CustomRootMachine<Input, Output>? = null

    override val dispatcher: CoroutineDispatcher = IO

    override fun process(data: ActivityMachine.Data<Input>?, callback: Handler<Output>) {
        if (data == null) {
            return
        }
        when (data) {
            is ActivityMachine.Lifecycle.Created<Input> -> {
                if (machine == null) {
                    val result = supplier(data.activity)
                    if (result != null) {
                        activity = data.activity
                        machine = CustomRootMachine(result, buffer.toList(), callback)
                        job = machine?.start(scope)
                        buffer.clear()
                    }
                }
            }
            is ActivityMachine.Lifecycle.Destroyed<Input> -> {
                if (activity === data.activity) {
                    job?.cancel()
                    job = null
                    activity = null
                    machine = null
                }
            }
            is ActivityMachine.ParentInput<Input> -> {
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