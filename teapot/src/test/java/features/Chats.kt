package features

import teapot.chat.SharedFlowChat
import teapot.message.Message

object Chats {
    fun getGlobalChat() = object : SharedFlowChat() {
        override suspend fun send(message: Message) {
            println("${System.currentTimeMillis()} GlobalChat $message")
            super.send(message)
        }
    }

}