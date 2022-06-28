package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.ComponentData
import com.simprok.simprokmachine.machines.Machine

internal class ConstructableReceiverMachine<Input, Output>(
    val machine: Machine<ComponentData.ToMachines<Input, Output>, Output>
) : ReceiverMachine<Input, Output>