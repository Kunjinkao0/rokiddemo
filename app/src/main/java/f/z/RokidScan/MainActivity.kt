package f.z.RokidScan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import f.z.RokidScan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scan.setOnClickListener {
            startQRCodeScanning()
        }
        binding.play.setOnClickListener {
//            playVideo()
            gotoWebView("https://www.baidu.com")
        }
    }

    private fun startQRCodeScanning() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR Code")
        integrator.setOrientationLocked(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
//            val scannedData = result.contents
//            binding.result.text = scannedData

            parseQRCodeResult()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun parseQRCodeResult() {
    }

    private fun playVideo() {
        var videoPath = "file:///sdcard/Movies/3D_Avatar.mp4"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(videoPath), "video/mp4")
        startActivity(intent)
    }

    private fun gotoWebView(webViewUrl: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("url", webViewUrl)
        startActivity(intent)
    }
}