package com.simprok.simprokmachine.android.sample

sealed interface AppEvent {
    object WillChangeState : AppEvent

    data class DidChangeState(val value: Int) : AppEvent
}