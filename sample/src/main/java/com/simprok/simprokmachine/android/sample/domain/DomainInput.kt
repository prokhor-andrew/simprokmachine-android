package com.simprok.simprokmachine.android.sample.domain

sealed interface DomainInput {

    class FromReader(val value: Int) : DomainInput

    object FromParent : DomainInput
}