package f.z.subtitle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import f.z.subtitle.databinding.ActivityMainBinding
import f.z.subtitle.databinding.ListItemChatBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatItems = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mockButton.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                addMockChatItem()
            }, 1000)
        }

        chatAdapter = ChatAdapter()
        binding.listView.adapter = chatAdapter
    }

    private fun addMockChatItem() {
        val from = "User"
        val to = "Bot"
        val content = "Hello, this is a mock chat message."
        val chatItem = ChatItem(from, to, content)
        chatItems.add(chatItem)
        chatAdapter.notifyDataSetChanged()
    }

    private inner class ChatAdapter : BaseAdapter() {
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
                // Inflate the chat item layout using ViewBinding
                ListItemChatBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
            } else {
                // Reuse the existing binding
                ListItemChatBinding.bind(convertView)
            }

            // Bind data to the ViewBinding
            binding.fromTextView.text = chatItem.from
            binding.toTextView.text = chatItem.to
            binding.contentTextView.text = chatItem.content

            return binding.root
        }
    }
}
