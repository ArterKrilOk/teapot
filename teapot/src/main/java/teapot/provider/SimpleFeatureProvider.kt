package teapot.provider

import teapot.chat.ChatReceiver
import teapot.effect.Effect
import teapot.effect.EffectHandler
import kotlinx.coroutines.CoroutineDispatcher
import teapot.message.Message
import teapot.reducer.Reducer

fun <S, M : Message, E : Effect> lazyFeatureProvider(
    featureName: String,
    initialState: S,
    reducer: Reducer<S, M, E>.(state: S, message: M) -> S,
    effectHandlers: Set<EffectHandler<E, M>>? = null,
    effectHandlersFactory: (() -> Set<EffectHandler<E, M>>)? = null,
    chatReceivers: Set<ChatReceiver<M>>? = null,
    featureCoroutineDispatcher: CoroutineDispatcher? = null,
    messagesBufferCapacity: Int? = null,
) = lazy {
    featureProvider(
        featureName,
        initialState,
        reducer,
        effectHandlers,
        effectHandlersFactory,
        chatReceivers,
        featureCoroutineDispatcher,
        messagesBufferCapacity
    )
}

fun <S, M : Message, E : Effect> featureProvider(
    featureName: String,
    initialState: S,
    reducer: Reducer<S, M, E>.(state: S, message: M) -> S,
    effectHandlers: Set<EffectHandler<E, M>>? = null,
    effectHandlersFactory: (() -> Set<EffectHandler<E, M>>)? = null,
    chatReceivers: Set<ChatReceiver<M>>? = null,
    featureCoroutineDispatcher: CoroutineDispatcher? = null,
    messagesBufferCapacity: Int? = null,
): FeatureProvider<S, M, E> = object : FeatureProvider<S, M, E>(featureName) {
    override val initialState: S
        get() = initialState
    override val reducer: Reducer<S, M, E>.(state: S, message: M) -> S
        get() = reducer

    override val effectHandlers: () -> Set<EffectHandler<E, M>>
        get() = effectHandlersFactory ?: effectHandlers?.let { { it } } ?: super.effectHandlers

    override val chatReceivers: () -> Set<ChatReceiver<M>>
        get() = chatReceivers?.let { { it } } ?: super.chatReceivers

    override val featureCoroutineDispatcher: CoroutineDispatcher
        get() = featureCoroutineDispatcher ?: super.featureCoroutineDispatcher

    override val messagesBufferCapacity: Int
        get() = messagesBufferCapacity ?: super.messagesBufferCapacity
}