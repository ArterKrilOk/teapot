package teapot.chat

import kotlinx.coroutines.flow.Flow
import teapot.message.Message

interface ChatReceiver<out M : Message> {
    val messagesFlow: Flow<M>
    val chats: Set<Chat>
}