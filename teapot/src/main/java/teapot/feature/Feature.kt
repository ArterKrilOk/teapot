package teapot.feature

import teapot.effect.Effect
import kotlinx.coroutines.flow.StateFlow
import teapot.message.Message

interface Feature<S, in M : Message, in E : Effect> {
    /**
     * Name of this feature
     */
    val featureName: String

    /**
     * Feature state flow
     */
    val state: StateFlow<S>

    /**
     * Current feature state. Same as calling state.value
     */
    val currentState: S
        get() = state.value

    /**
     * Sends message to feature. Message will be added to message queue and then reduced to new state
     */
    fun dispatch(message: M)

    /**
     * Cancels feature coroutine scope. You can not use feature after calling this. Dangerous API
     */
    fun clear()
}