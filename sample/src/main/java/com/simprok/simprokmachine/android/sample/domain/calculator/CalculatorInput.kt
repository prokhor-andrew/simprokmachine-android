package com.simprok.simprokmachine.android.sample.domain.calculator

sealed interface CalculatorInput {

    data class Initialize(val value: Int) : CalculatorInput

    object Increment : CalculatorInput
}