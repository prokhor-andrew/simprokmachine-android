//
//  MachineAssembly.kt
//  simprokmachine-android
//
//  Created by Andrey Prokhorenko on 12.03.2022.
//  Copyright (c) 2022 simprok. All rights reserved.

package com.simprok.simprokmachine.android

import com.simprok.simprokandroid.Assembly
import com.simprok.simprokandroid.WidgetMachine
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.api.RootMachine
import com.simprok.simprokmachine.api.start
import com.simprok.simprokmachine.machines.Machine
import kotlinx.coroutines.CoroutineScope

class MachineAssembly private constructor(
    private val starter: Handler<CoroutineScope>
) : Assembly {

    override fun start(scope: CoroutineScope) {
        starter(scope)
    }

    companion object {

        fun <Input, Output> create(
            machine: WidgetMachine<Input, Output>
        ): MachineAssembly = MachineAssembly { scope ->
            object : RootMachine<Input, Output> {
                override val child: Machine<Input, Output> get() = machine.machine

                override val scope: CoroutineScope get() = scope
            }.start {}
        }
    }
}

fun <Input, Output> Assembly.Companion.create(
    machine: WidgetMachine<Input, Output>,
): MachineAssembly = MachineAssembly.create(machine)