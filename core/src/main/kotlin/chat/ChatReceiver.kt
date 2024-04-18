package chat

import kotlinx.coroutines.flow.Flow
import message.Message

interface ChatReceiver<out M : Message> {
    val messagesFlow: Flow<M>
    val chats: Set<Chat>
}