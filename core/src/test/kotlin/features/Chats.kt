package features

import chat.SharedFlowChat
import message.Message

object Chats {
    fun getGlobalChat() = object : SharedFlowChat() {
        override suspend fun send(message: Message) {
            println("${System.currentTimeMillis()} GlobalChat $message")
            super.send(message)
        }
    }

}