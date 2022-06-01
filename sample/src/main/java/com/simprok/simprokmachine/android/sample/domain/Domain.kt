package com.simprok.simprokmachine.android.sample.domain

import android.content.SharedPreferences
import com.simprok.simprokmachine.android.sample.AppEvent
import com.simprok.simprokmachine.android.sample.domain.calculator.Calculator
import com.simprok.simprokmachine.android.sample.domain.calculator.CalculatorInput
import com.simprok.simprokmachine.android.sample.domain.sreader.StorageReader
import com.simprok.simprokmachine.api.*
import com.simprok.simprokmachine.machines.Machine
import com.simprok.simprokmachine.machines.ParentMachine

class Domain(private val prefs: SharedPreferences) : ParentMachine<AppEvent, AppEvent> {

    override val child: Machine<AppEvent, AppEvent>
        get() {

            val reader: Machine<DomainInput, DomainOutput> = StorageReader(prefs).outward {
                Ward.set<DomainOutput>(DomainOutput.FromReader(it))
            }.inward {
                Ward.set()
            }

            val calculator: Machine<DomainInput, DomainOutput> = Calculator().outward {
                Ward.set<DomainOutput>(DomainOutput.FromCalculator(it))
            }.inward {
                when (it) {
                    is DomainInput.FromParent -> Ward.set(CalculatorInput.Increment)
                    is DomainInput.FromReader -> Ward.set(CalculatorInput.Initialize(it.value))
                }
            }

            val connectable: Machine<DomainInput, DomainOutput> = Machine.merge(
                reader,
                calculator
            ).redirect {
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