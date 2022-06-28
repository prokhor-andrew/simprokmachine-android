package com.simprok.simprokmachine.android

import com.simprok.simprokmachine.android.implementation.dropFirst
import com.simprok.simprokmachine.api.BiMapper

sealed class BufferStrategy<Input>(
    internal val reducer: BiMapper<List<Input>, Input, List<Input>>
) {

    class TakeFirst<Input> : BufferStrategy<Input>({ current, new ->
        current.ifEmpty {
            listOf(new)
        }
    })

    class TakeLast<Input> : BufferStrategy<Input>({ _, new -> listOf(new) })

    class SkipAll<Input> : BufferStrategy<Input>({ _, _ -> emptyList() })

    class CollectDropFirst<Input>(limit: Int) : BufferStrategy<Input>({ current, new ->
        if (current.size < limit) {
            current.plus(new)
        } else {
            current.dropFirst().plus(new)
        }
    })

    class CollectDropLast<Input>(limit: Int) : BufferStrategy<Input>({ current, new ->
        if (current.size < limit) {
            current.plus(new)
        } else {
            current.dropLast(1).plus(new)
        }
    })

    class CollectNoDrop<Input>(limit: Int) : BufferStrategy<Input>({ current, new ->
        if (current.size < limit) {
            current.plus(new)
        } else {
            current
        }
    })

    class Custom<Input>(
        reducer: BiMapper<List<Input>, Input, List<Input>>
    ) : BufferStrategy<Input>(reducer)
}