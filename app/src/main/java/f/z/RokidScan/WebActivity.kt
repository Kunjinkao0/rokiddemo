package f.z.RokidScan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import f.z.RokidScan.databinding.ActivityMainBinding
import f.z.RokidScan.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webv.webViewClient = WebViewClient()
        // Retrieve the URL from the extra
        val url = intent.getStringExtra("url")
        if (url != null) {
            binding.webv.loadUrl(url)
        }
    }
}