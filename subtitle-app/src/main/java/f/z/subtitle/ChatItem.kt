package f.z.subtitle

import java.util.UUID

data class ChatItem(val from: String, val to: String, val content: String) {
    val id = UUID.randomUUID().toString()
}
