package f.z.RokidScan

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.MANAGE_MEDIA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.integration.android.IntentIntegrator
import f.z.RokidScan.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission();

        binding.scan.setOnClickListener {
            startQRCodeScanning()
        }
        binding.play.setOnClickListener {
//            requestPermission()

//            gotoWebView("https://www.baidu.com")


//            ActivityCompat.requestPermissions(
//                this, arrayOf<String>(
//                    READ_EXTERNAL_STORAGE
//                ), 1
//            )
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
            val scannedData = result.contents
//            binding.result.text = scannedData

            parseQRCodeResult(scannedData)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun parseQRCodeResult(scannedData: String) {
        if (scannedData.equals("1")) {
            val path =
                Environment.getExternalStorageDirectory().getPath() + "/Movies/sample_movie.mp4"
            playVideo(path)
        } else {
            gotoWebView("https://www.baidu.com/s?wd=" + scannedData)
        }
    }

    private fun playVideo(path: String) {
        val intent = Intent(this, VideoActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }

    private fun gotoWebView(webViewUrl: String) {
        val intent = Intent(this, WebActivity::class.java)
        intent.putExtra("url", webViewUrl)
        startActivity(intent)
    }

    private fun requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
//                startActivityForResult(intent, 2296)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent, 2296)
//            }
//        } else {
        //below android 11
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(
                READ_EXTERNAL_STORAGE
            ), 1
        )
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] === PackageManager.PERMISSION_GRANTED) {

        }
    }

}