package f.z.subtitle

import java.util.UUID

data class ChatItem(
    var from: String = "",
    var to: String = "",
    var content: String = "",
    var isPic: Boolean = false,
    var picBase64: String? = null,
    var id: String? = ""
) {
    init {
        this.id = UUID.randomUUID().toString()
    }
}