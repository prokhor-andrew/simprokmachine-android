package com.simprok.simprokmachine.android.sample.domain.sreader

import android.content.SharedPreferences
import com.simprok.simprokmachine.android.sample.storageKey
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class StorageReader(private val prefs: SharedPreferences) : ChildMachine<Unit, Int> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    override fun process(input: Unit?, callback: Handler<Int>) {
        callback(prefs.getInt(storageKey, 0))
    }
}