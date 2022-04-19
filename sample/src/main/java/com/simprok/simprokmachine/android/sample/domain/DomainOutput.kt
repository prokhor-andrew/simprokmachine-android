package com.simprok.simprokmachine.android.sample.domain

sealed interface DomainOutput {

    class FromReader(val value: Int) : DomainOutput

    class FromCalculator(val value: Int) : DomainOutput
}