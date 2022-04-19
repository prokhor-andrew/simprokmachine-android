package com.simprok.simprokmachine.android.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simprok.simprokandroid.*
import com.simprok.simprokmachine.android.MachineAssembly
import com.simprok.simprokmachine.android.sample.display.Display
import com.simprok.simprokmachine.android.sample.display.ui.MainFragment
import com.simprok.simprokmachine.android.sample.domain.Domain
import com.simprok.simprokmachine.api.Direction
import com.simprok.simprokmachine.api.Ward

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.empty_layout)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, MainFragment(), "tag")
                .commit()

            start<String, Unit> { machine ->
                val prefs = application.getSharedPreferences("storage", MODE_PRIVATE)
                MachineAssembly.create(
                    machine.outward<AppEvent, Unit, String> {
                        Ward.set(AppEvent.WillChangeState)
                    }.inward<AppEvent, String, AppEvent> {
                        when (it) {
                            is AppEvent.DidChangeState -> Ward.set(it.value.toString())
                            is AppEvent.WillChangeState -> Ward.set()
                        }
                    }.mergeWith(
                        Display(prefs),
                        Domain(prefs)
                    ).redirect { Direction.Back(it) }
                )
            }
        }
    }
}