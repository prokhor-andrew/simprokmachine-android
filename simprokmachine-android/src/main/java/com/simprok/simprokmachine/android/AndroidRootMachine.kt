package com.simprok.simprokmachine.android

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import com.simprok.simprokmachine.android.AndroidRootMachine.Companion.attach
import com.simprok.simprokmachine.android.AndroidRootMachine.Companion.detach
import com.simprok.simprokmachine.android.AndroidRootMachine.Companion.send
import com.simprok.simprokmachine.android.AndroidRootMachine.Companion.subscribe
import com.simprok.simprokmachine.android.implementation.HelperMachine
import com.simprok.simprokmachine.android.implementation.root
import com.simprok.simprokmachine.api.RootMachine
import com.simprok.simprokmachine.machines.Machine

interface AndroidRootMachine<Input, Output> : RootMachine<Input, Output> {

    val component: ComponentMachine<Input, Output>

    override val child: Machine<Input, Output>
        get() = root(helper)

    companion object {

        private val helper = HelperMachine()

        fun Companion.attach(activity: Activity) {
            helper.attach(activity)
        }

        fun Companion.detach(activity: Activity) {
            helper.detach(activity)
        }

        fun Companion.subscribe(owner: LifecycleOwner) {
            helper.subscribe(owner)
        }

        fun Companion.send(data: ReceiverData) {
            helper.send(data)
        }

        fun Companion.send(context: Context?, intent: Intent?) {
            send(ReceiverData(context, intent))
        }
    }
}

fun Activity.attach() {
    AndroidRootMachine.attach(this)
}

fun Activity.detach() {
    AndroidRootMachine.detach(this)
}

fun LifecycleOwner.subscribe() {
    AndroidRootMachine.subscribe(this)
}

fun send(context: Context?, intent: Intent?) {
    AndroidRootMachine.send(context, intent)
}

fun send(data: ReceiverData) {
    AndroidRootMachine.send(data)
}