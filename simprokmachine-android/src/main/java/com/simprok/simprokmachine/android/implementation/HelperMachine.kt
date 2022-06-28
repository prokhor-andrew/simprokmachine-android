package com.simprok.simprokmachine.android.implementation

import android.app.Activity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.simprok.simprokmachine.android.ReceiverData
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.Main

internal class HelperMachine : ChildMachine<Unit, ComponentLifecycle> {

    private val observer = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            attach(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            detach(owner)
        }
    }

    private var callback: Handler<ComponentLifecycle>? = null
    private val receivers: MutableSet<ReceiverData> = mutableSetOf()
    private val activities: MutableSet<Activity> = mutableSetOf()
    private val owners: MutableSet<LifecycleOwner> = mutableSetOf()

    private fun attach(owner: LifecycleOwner) {
        val callback = callback
        if (callback != null) {
            callback(ComponentLifecycle.OwnerData.Created(owner))
        } else {
            owners.add(owner)
        }
    }

    private fun detach(owner: LifecycleOwner) {
        val callback = callback
        if (callback != null) {
            callback(ComponentLifecycle.OwnerData.Destroyed(owner))
        } else {
            owners.remove(owner)
        }
    }

    fun attach(activity: Activity) {
        val callback = callback
        if (callback != null) {
            callback(ComponentLifecycle.ActivityData.Created(activity))
        } else {
            activities.add(activity)
        }
    }

    fun detach(activity: Activity) {
        val callback = callback
        if (callback != null) {
            callback(ComponentLifecycle.ActivityData.Destroyed(activity))
        } else {
            activities.remove(activity)
        }
    }

    fun subscribe(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(observer)
    }

    fun send(data: ReceiverData) {
        val callback = callback
        if (callback != null) {
            callback(ComponentLifecycle.ReceivedData(data))
        } else {
            receivers.add(data)
        }
    }

    override val dispatcher: CoroutineDispatcher
        get() = Main

    override fun process(input: Unit?, callback: Handler<ComponentLifecycle>) {
        this.callback = callback
        if (input == null) {
            activities.forEach {
                callback(ComponentLifecycle.ActivityData.Created(it))
            }

            owners.forEach {
                callback(ComponentLifecycle.OwnerData.Created(it))
            }

            receivers.forEach {
                callback(ComponentLifecycle.ReceivedData(it))
            }

            activities.clear()
            owners.clear()
            receivers.clear()
        }
    }
}