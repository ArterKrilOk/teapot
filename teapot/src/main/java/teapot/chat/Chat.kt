package teapot.chat

import kotlinx.coroutines.flow.Flow
import teapot.message.Message

interface Chat {
    val messageFlow: Flow<Message>
    suspend fun send(message: Message)
}