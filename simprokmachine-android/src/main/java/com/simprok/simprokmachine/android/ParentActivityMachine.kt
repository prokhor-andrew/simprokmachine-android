package com.simprok.simprokmachine.android

interface ParentActivityMachine<Input, Output> : ActivityMachine<Input, Output> {

    val child: ActivityMachine<Input, Output>
}
