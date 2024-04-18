package chat

import kotlinx.coroutines.flow.Flow
import message.Message

interface Chat {
    val messageFlow: Flow<Message>
    suspend fun send(message: Message)
}