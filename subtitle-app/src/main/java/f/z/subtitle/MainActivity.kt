package f.z.subtitle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import f.z.subtitle.databinding.ActivityMainBinding
import f.z.subtitle.databinding.ListItemChatBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatItems = mutableListOf<ChatItem>()

    private var webSocket: WebSocket? = null
    private var socketConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mockButton.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                addChatItem(ChatItem("User", "Bot", "Hello, this is a mock chat message."))
            }, 1000)
        }

        chatAdapter = ChatAdapter()
        binding.listView.adapter = chatAdapter

//        connectWebSocket()
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket?.cancel()
    }

    private fun addChatItem(item: ChatItem) {
        chatItems.add(item)
        chatAdapter.notifyDataSetChanged()
        binding.listView.post { binding.listView.smoothScrollToPosition(chatAdapter.count - 1) }
    }

    private fun connectWebSocket() {
        val client = OkHttpClient()

        val request = Request.Builder().url("wss://example.com/your-websocket-url").build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                socketConnected = true
                Toast.makeText(this@MainActivity, "Connect Success", Toast.LENGTH_LONG).show();
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // Handle incoming messages here
                runOnUiThread {
                    addChatItem(ChatItem("from", "to", text))
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                socketConnected = false
            }

            override fun onFailure(
                webSocket: WebSocket, t: Throwable, response: okhttp3.Response?
            ) {
                super.onFailure(webSocket, t, response)
                socketConnected = false
                Toast.makeText(this@MainActivity, "Connect Failure", Toast.LENGTH_LONG).show();
            }
        }

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private inner class ChatAdapter : BaseAdapter() {
        private val handler = Handler(Looper.getMainLooper())
        private val showedItems = HashSet<String>()

        override fun getCount(): Int {
            return chatItems.size
        }

        override fun getItem(position: Int): Any {
            return chatItems[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val chatItem = getItem(position) as ChatItem
            val binding = if (convertView == null) {
                ListItemChatBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            } else {
                ListItemChatBinding.bind(convertView)
            }

            binding.fromTextView.text = chatItem.from
            binding.toTextView.text = chatItem.to
            binding.contentTextView.text = "" // empty previous first

            if (showedItems.contains(chatItem.id)) {
                binding.contentTextView.text = chatItem.content
            } else {
                animateText(chatItem.content, binding.contentTextView)
                showedItems.add(chatItem.id)
            }

            return binding.root
        }

        private fun animateText(text: String, textView: TextView) {
            var index = 0
            val delayMillis = 20L

            val runnable = object : Runnable {
                override fun run() {
                    if (index <= text.length) {
                        val partialText = text.substring(0, index)
                        textView.text = partialText
                        index++
                        handler.postDelayed(this, delayMillis)
                    }
                }
            }

            handler.post(runnable)
        }
    }
}
