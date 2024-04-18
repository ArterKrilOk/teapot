package teapot.android.viewmodel

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import teapot.effect.Effect
import teapot.feature.Feature
import teapot.message.Message
import teapot.provider.FeatureProvider

/**
 * Creates [ViewModelProvider.Factory] that will create same [ViewModelFeature] as [Feature] inside [FeatureProvider]
 */
val <S, M : Message, E : Effect> FeatureProvider<S, M, E>.AsViewModelFactory: ViewModelProvider.Factory
    get() = viewModelFactory {
        initializer {
            ViewModelFeature(createFeature())
        }
    }

/**
 * Provides [Feature], [ViewModelFeature] if more specific, using [viewModels].
 */
fun <S, M : Message, E : Effect> Fragment.features(
    provider: FeatureProvider<S, M, E>,
): Lazy<Feature<S, M, E>> = viewModels { provider.AsViewModelFactory }

/**
 * Provides [Feature], [ViewModelFeature] if more specific, using [activityViewModels].
 */
fun <S, M : Message, E : Effect> Fragment.activityFeatures(
    provider: FeatureProvider<S, M, E>,
): Lazy<Feature<S, M, E>> = activityViewModels { provider.AsViewModelFactory }
