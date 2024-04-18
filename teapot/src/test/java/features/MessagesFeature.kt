package features

import teapot.chat.Chat
import teapot.chat.ChatReceiver
import teapot.effect.Effect
import teapot.effect.simpleSuspendEffectHandler
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import teapot.message.Message
import teapot.provider.featureProvider
import teapot.reducer.simpleReducer
import java.util.concurrent.Executors

object MessagesFeature {
    data class State(
        val isLoading: Boolean = false,
        val messages: List<String> = emptyList(),
    )

    sealed interface Msg : Message {
        data class SendMessage(val message: String) : Msg
        data class AddMessage(val message: String) : Msg

        data class SendChatMessage(val msg: Message) : Msg
        object Clear : Msg
    }

    sealed interface Eff : Effect {
        data class SendMessageEffect(val message: String) : Eff
    }

    private fun createHandler() = simpleSuspendEffectHandler<Eff, Msg> {
        println("${System.currentTimeMillis()} MessagesFeature Effect $it")
        when (it) {
            is Eff.SendMessageEffect -> {
                delay(20)
                Msg.AddMessage(it.message)
            }
        }
    }

    private val customDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()


    fun provider(chat: Chat) = featureProvider(
        featureName = "messages-feature",
        initialState = State(),
        chatReceivers = setOf(
            customFeatureMessageChatReceiver(chat),
        ),
        featureCoroutineDispatcher = customDispatcher,
        effectHandlers = setOf(createHandler()),
        reducer = simpleReducer { state, message ->
            when (message) {
                is Msg.Clear -> initialState

                is Msg.SendMessage -> state.copy(isLoading = true).also {
                    launchEffect(Eff.SendMessageEffect(message.message))
                }

                is Msg.AddMessage -> state.copy(
                    isLoading = false,
                    messages = state.messages + message.message,
                )

                is Msg.SendChatMessage -> state.also {
                    sendMessage(message.msg)
                }
            }.also {
                println("${System.currentTimeMillis()} MessagesFeature Reduce $message $state -> $it")
            }
        },
    )

    private inline fun <reified M : Message> customFeatureMessageChatReceiver(
        vararg chat: Chat,
    ): ChatReceiver<M> = object : ChatReceiver<M> {
        override val messagesFlow: Flow<M> =
            chat.map { it.messageFlow }.merge().distinctUntilChanged()
                .filterIsInstance<M>().onEach {
                    println("${System.currentTimeMillis()} MessagesFeature Receiver $it")
                }

        override val chats: Set<Chat> = chat.toSet()
    }
}