package com.simprok.simprokmachine.android.sample.display.swriter

import android.content.SharedPreferences
import com.simprok.simprokmachine.android.sample.storageKey
import com.simprok.simprokmachine.api.Handler
import com.simprok.simprokmachine.machines.ChildMachine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class StorageWriter(private val prefs: SharedPreferences) : ChildMachine<Int, Nothing> {

    override val dispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    override suspend fun process(input: Int?, callback: Handler<Nothing>) {
        if (input != null) {
            prefs.edit()
                .putInt(storageKey, input)
                .apply()
        }
    }
}