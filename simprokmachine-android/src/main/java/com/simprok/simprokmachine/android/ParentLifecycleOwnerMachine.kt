package com.simprok.simprokmachine.android

interface ParentLifecycleOwnerMachine<Input, Output> : LifecycleOwnerMachine<Input, Output> {

    val child: LifecycleOwnerMachine<Input, Output>
}