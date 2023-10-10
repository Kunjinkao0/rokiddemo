package f.z.RokidScan

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
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

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
                Log.d("MainActivity", "Cancelled scan")
                Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Log.d(
                    "MainActivity",
                    "Cancelled scan due to missing camera permission"
                )
                Toast.makeText(
                    this@MainActivity,
                    "Cancelled due to missing camera permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Log.d("MainActivity", "Scanned")
            Toast.makeText(
                this@MainActivity,
                "Scanned: " + result.contents,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun startQRCodeScanning() {
//        val integrator = IntentIntegrator(this)
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
//        integrator.setPrompt("Scan a QR Code")
//        integrator.setOrientationLocked(true)
//        integrator.initiateScan()

//        scanForResult.launch(Intent(this, ScannerActivity::class.java))
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