package com.simprok.simprokmachine.android.implementation

internal sealed interface PipeData<Input, Output> {

    data class ToPipe<Input, Output>(val output: Output) : PipeData<Input, Output>

    data class ToMachine<Input, Output>(val input: Input) : PipeData<Input, Output>
}
