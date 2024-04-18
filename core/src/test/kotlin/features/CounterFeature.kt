package features

import chat.Chat
import chat.ChatReceiver
import effect.Effect
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import message.Message
import provider.featureProvider
import reducer.simpleReducer
import java.util.concurrent.Executors

object CounterFeature {
    data class State(
        val value: Int,
    )

    sealed interface Msg : Message {
        object Increment : Msg
        object Clear : Msg
        data class SendMessageToMessagesFeature(val text: String) : Msg
    }

    sealed interface Eff : Effect

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    fun provider(chat: Chat) = featureProvider<State, Msg, Eff>(
        featureName = "counter-feature",
        initialState = State(value = 0),
        chatReceivers = setOf(customFeatureMessageChatReceiver(chat)),
        featureCoroutineDispatcher = customDispatcher,
        reducer = simpleReducer { state, message ->
            when (message) {
                is Msg.Clear -> initialState
                is Msg.Increment -> state.copy(value = state.value + 1)
                is Msg.SendMessageToMessagesFeature -> state.also {
                    sendMessage(MessagesFeature.Msg.SendMessage(message.text))
                }
            }.also {
                println("${System.currentTimeMillis()} CounterFeature Reduce $message $state -> $it")
            }
        },
    )

    private inline fun <reified M : Message> customFeatureMessageChatReceiver(
        vararg chat: Chat,
    ): ChatReceiver<M> = object : ChatReceiver<M> {
        override val messagesFlow: Flow<M> =
            chat.map { it.messageFlow }.merge().distinctUntilChanged()
                .filterIsInstance<M>().onEach {
                    println("${System.currentTimeMillis()} CounterFeature Receiver $it")
                }

        override val chats: Set<Chat> = chat.toSet()
    }
}