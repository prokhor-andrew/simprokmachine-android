package com.simprok.simprokmachine.android.implementation

import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO

internal class PipeMachine<Input, Output>(
    private val buffer: List<Input>,
    private val escaping: Handler<Output>,
) : ChildMachine<PipeData<Input, Output>, PipeData<Input, Output>> {

    private var callback: Handler<PipeData<Input, Output>>? = null

    fun send(input: Input) {
        callback?.invoke(PipeData.ToMachine(input))
    }

    override val dispatcher: CoroutineDispatcher
        get() = IO

    override fun process(
        input: PipeData<Input, Output>?,
        callback: Handler<PipeData<Input, Output>>
    ) {
        this.callback = callback

        if (input != null) {
            when (input) {
                is PipeData.ToMachine<Input, Output> -> {}
                is PipeData.ToPipe<Input, Output> -> escaping(input.output)
            }
        } else {
            buffer.forEach { send(it) }
        }
    }
}