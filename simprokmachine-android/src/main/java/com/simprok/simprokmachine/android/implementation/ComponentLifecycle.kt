package com.simprok.simprokmachine.android.implementation

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.simprok.simprokmachine.android.ReceiverData

internal sealed interface ComponentLifecycle {

    sealed class OwnerData(val owner: LifecycleOwner) : ComponentLifecycle {
        class Created(owner: LifecycleOwner) : OwnerData(owner)

        class Destroyed(owner: LifecycleOwner) : OwnerData(owner)
    }

    sealed class ActivityData(val activity: Activity) : ComponentLifecycle {
        class Created(activity: Activity) : ActivityData(activity)

        class Destroyed(activity: Activity) : ActivityData(activity)
    }

    data class ReceivedData(val data: ReceiverData) : ComponentLifecycle
}