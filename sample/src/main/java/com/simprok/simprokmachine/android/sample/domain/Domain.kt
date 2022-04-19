package com.simprok.simprokmachine.android.sample.domain

import android.content.SharedPreferences
import com.simprok.simprokmachine.android.sample.AppEvent
import com.simprok.simprokmachine.android.sample.domain.calculator.Calculator
import com.simprok.simprokmachine.android.sample.domain.sreader.StorageReader
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.api.connectable.BasicConnection
import com.simprok.simprokmachine.api.connectable.ConnectableMachine
import com.simprok.simprokmachine.api.connectable.ConnectionType
import com.simprok.simprokmachine.machines.Machine
import com.simprok.simprokmachine.machines.ParentMachine

class Domain(private val prefs: SharedPreferences) : ParentMachine<AppEvent, AppEvent> {

    override val child: Machine<AppEvent, AppEvent>
        get() {
            fun getCalculator(initial: Int): Machine<DomainInput, DomainOutput> {
                return Calculator(initial).outward {
                    Ward.set<DomainOutput>(DomainOutput.FromCalculator(it))
                }.inward {
                    when (it) {
                        is DomainInput.FromParent -> Ward.set(Unit)
                        is DomainInput.FromReader -> Ward.set()
                    }
                }
            }

            val reader: Machine<DomainInput, DomainOutput> = StorageReader(prefs).outward {
                Ward.set<DomainOutput>(DomainOutput.FromReader(it))
            }.inward {
                Ward.set()
            }

            val connectable: Machine<DomainInput, DomainOutput> =
                ConnectableMachine(BasicConnection.create(reader)) { state, input ->
                    when (input) {
                        is DomainInput.FromParent -> ConnectionType.Inward()
                        is DomainInput.FromReader -> ConnectionType.Reduce(
                            BasicConnection.create(getCalculator(input.value))
                        )
                    }
                }.redirect {
                    when (it) {
                        is DomainOutput.FromReader -> Direction.Back(DomainInput.FromReader(it.value))
                        is DomainOutput.FromCalculator -> Direction.Prop()
                    }
                }

            return connectable.outward {
                when (it) {
                    is DomainOutput.FromReader -> Ward.set()
                    is DomainOutput.FromCalculator -> Ward.set<AppEvent>(AppEvent.DidChangeState(it.value))
                }
            }.inward {
                when (it) {
                    is AppEvent.DidChangeState -> Ward.set()
                    is AppEvent.WillChangeState -> Ward.set(DomainInput.FromParent)
                }
            }
        }
}