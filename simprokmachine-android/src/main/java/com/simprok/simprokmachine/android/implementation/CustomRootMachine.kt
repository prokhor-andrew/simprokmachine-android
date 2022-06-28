package com.simprok.simprokmachine.android.implementation

import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine

internal class CustomRootMachine<Input, Output>(
    private val machine: Machine<Input, Output>,
    buffer: List<Input>,
    callback: Handler<Output>
) : RootMachine<PipeData<Input, Output>, PipeData<Input, Output>> {

    private val pipe: PipeMachine<Input, Output> = PipeMachine(buffer, callback)

    override val child: Machine<PipeData<Input, Output>, PipeData<Input, Output>>
        get() {
            val pipe: Machine<PipeData<Input, Output>, PipeData<Input, Output>> =
                pipe.inward {
                    when (it) {
                        is PipeData.ToMachine<Input, Output> -> Ward.set()
                        is PipeData.ToPipe<Input, Output> -> Ward.set(it)
                    }
                }

            val casted: Machine<PipeData<Input, Output>, PipeData<Input, Output>> =
                machine.outward<Input, Output, PipeData<Input, Output>> {
                    Ward.set(PipeData.ToPipe(it))
                }.inward {
                    when (it) {
                        is PipeData.ToMachine<Input, Output> -> Ward.set(it.input)
                        is PipeData.ToPipe<Input, Output> -> Ward.set()
                    }
                }

            return merge(casted, pipe).redirect { Direction.Back(it) }
        }

    fun send(input: Input) = pipe.send(input)
}