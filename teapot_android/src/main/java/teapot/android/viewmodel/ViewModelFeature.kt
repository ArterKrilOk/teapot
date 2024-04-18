package teapot.android.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import teapot.effect.Effect
import teapot.feature.Feature
import teapot.message.Message

/**
 * Wraps [Feature] in [ViewModel]
 */
class ViewModelFeature<S, M : Message, E : Effect>(
    private val feature: Feature<S, M, E>
) : ViewModel(), Feature<S, M, E> {
    /**
     * Name of this feature
     */
    override val featureName: String
        get() = feature.featureName

    /**
     * Feature state flow
     */
    override val state: StateFlow<S>
        get() = feature.state

    /**
     * Doesn't do anything. [ViewModelFeature] clears in [onCleared] method.
     */
    override fun clear() = Unit

    /**
     * Sends message to feature. Message will be added to message queue and then reduced to new state
     */
    override fun dispatch(message: M) = feature.dispatch(message)


    override fun onCleared() {
        feature.clear()
        super.onCleared()
    }
}