package f.z.RokidScan

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import f.z.RokidScan.databinding.ActivityScannerBinding
import java.util.Arrays

class ScannerActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ScanQRActivity"
    }


    private lateinit var binding: ActivityScannerBinding

    private var barcodeView: DecoratedBarcodeView? = null
    private var isFlashOn = false

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        isFlashOn = false
        if (!hasFlash()) {
            binding.btnFlash.visibility = View.GONE
        }
        binding.btnFlash.setOnClickListener { switchFlashlight() }

        barcodeView = binding.barcodeScanner
        val formats: Collection<BarcodeFormat> =
            Arrays.asList(BarcodeFormat.CODE_39) // Set barcode type
        barcodeView!!.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
        barcodeView!!.initializeFromIntent(getIntent())
        barcodeView!!.decodeContinuous(callback)
    }

    private val callback: BarcodeCallback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            Log.e(TAG, result.text) // QR/Barcode result
            val data = Intent()
            data.putExtra("code", result.text)
            setResult(1, data)
        }

        override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return getApplicationContext().getPackageManager()
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun switchFlashlight() {
        if (isFlashOn) {
            isFlashOn = false
            barcodeView!!.setTorchOff()
        } else {
            isFlashOn = true
            barcodeView!!.setTorchOn()
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView!!.pause()
    }
}