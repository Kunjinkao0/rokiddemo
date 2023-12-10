package f.z.subtitle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import f.z.subtitle.databinding.ActivityMainBinding
import f.z.subtitle.databinding.ListItemChatBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter

    private val chatItems = mutableListOf<ChatItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mockButton.setOnClickListener {
            getChatList()
        }

        chatAdapter = ChatAdapter()
        binding.listView.adapter = chatAdapter
        binding.listView.setOnItemClickListener { adapterView: AdapterView<*>, v: View, index: Int, l: Long ->
            val item = chatItems[index]
            if (item.isPic) {
                showPic(item)
            } else {
                hidePic()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun getChatList() {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = RequestBody.create(mediaType, "{\"key\":\"value\"}")

//        val url = "http://192.168.0.250:5000/get_conversation"
        val url = "http://192.168.31.237:3000/data"
        val request = Request.Builder().url(url).get().build()


        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle the response
                if (response.isSuccessful) {
                    try {
                        val responseData = response.body!!.string()
                        processResponse(responseData)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun processResponse(jsonString: String) {
        chatItems.clear()
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val jai = jsonObject.get("ai")
            val juser = jsonObject.getString("user")

            val userItem = ChatItem()
            userItem.from = "User";
            userItem.content = juser

            val aiItem = ChatItem()
            aiItem.from = "AI";
            when (jai) {
                is String -> {
                    aiItem.content = jai
                }

                is JSONArray -> {
                    when (jai.getString(0)) {
                        "take photo" -> {
                            aiItem.isPic = true
                            aiItem.picBase64 = jsonObject.getString("photo_base64")
                            aiItem.content = "[click to see picture]"
                        }

                        "control robot" -> {
                            aiItem.content = "[controling robot...]"
                        }
                    }
                }
            }

            chatItems.add(userItem)
            chatItems.add(aiItem)
        }

        runOnUiThread {
            chatAdapter.notifyDataSetChanged()
            binding.listView.post { binding.listView.smoothScrollToPosition(chatAdapter.count - 1) }

            if (chatItems.size == 0) return@runOnUiThread

            val lastItem = chatItems.findLast { it.from == "AI" }!!;
            if (lastItem.isPic) {
                showPic(lastItem)
            } else {
                hidePic()
            }
        }
    }

    private fun showPic(item: ChatItem) {
        if (binding.image.visibility == View.VISIBLE) return

        binding.image.apply {
            setImageBitmap(convertBase64Image(item.picBase64!!))  // Set the bitmap to ImageView
            visibility = View.VISIBLE  // Make the view visible
            alpha = 0f               // Start from fully transparent
            scaleX = 0.8f            // Start a bit smaller
            scaleY = 0.8f

            animate().alpha(1f)          // Animate to fully opaque
                .scaleX(1f)         // Animate to normal size
                .scaleY(1f).setDuration(200)  // Duration of the animation
                .setListener(null)  // Add a listener if you need to do something when the animation ends
        }
    }

    private fun hidePic() {
        if (binding.image.visibility != View.VISIBLE) return

        binding.image.apply {
            setImageDrawable(null) // recycle
            animate().alpha(0f)          // Fade out (alpha goes to 0)
                .scaleX(0.8f)       // Scale down slightly
                .scaleY(0.8f).setDuration(200)  // Duration of the animation
                .withEndAction {
                    visibility = View.GONE // Hide the view after animation ends
                }.start()
        }
    }

    private fun convertBase64Image(base64String: String): Bitmap? {
        return try {
            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            decodedImage
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
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

//            binding.fromTextView.text = chatItem.from
//            binding.toTextView.text = chatItem.to
//            binding.contentTextView.text = "" // empty previous first

//            if (showedItems.contains(chatItem.id)) {
//                binding.contentTextView.text = chatItem.content
//            } else {
////                animateText(chatItem.content, binding.contentTextView)
//                showedItems.add(chatItem.id)
//            }
            binding.fromTextView.text = chatItem.from + ":"
            binding.contentTextView.text = chatItem.content

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
