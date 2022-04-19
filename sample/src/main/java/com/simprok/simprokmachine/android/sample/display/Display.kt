package com.simprok.simprokmachine.android.sample.display

import android.content.SharedPreferences
import com.simprok.simprokmachine.android.sample.AppEvent
import com.simprok.simprokmachine.android.sample.display.logger.Logger
import com.simprok.simprokmachine.android.sample.display.swriter.StorageWriter
import com.simprok.simprokmachine.api.Ward
import com.simprok.simprokmachine.api.inward
import com.simprok.simprokmachine.api.merge
import com.simprok.simprokmachine.api.outward
import com.simprok.simprokmachine.machines.Machine
import com.simprok.simprokmachine.machines.ParentMachine

class Display(private val prefs: SharedPreferences) : ParentMachine<AppEvent, AppEvent> {

    override val child: Machine<AppEvent, AppEvent>
        get() = merge(
            Logger().outward { Ward.set<AppEvent>() }.inward {
                when (it) {
                    is AppEvent.WillChangeState -> Ward.set()
                    is AppEvent.DidChangeState -> Ward.set(it.value.toString())
                }
            },
            StorageWriter(prefs).outward { Ward.set<AppEvent>() }.inward {
                when (it) {
                    is AppEvent.WillChangeState -> Ward.set()
                    is AppEvent.DidChangeState -> Ward.set(it.value)
                }
            }
        )
}