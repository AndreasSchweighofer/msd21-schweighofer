package at.interactivecuriosity.imagedownload

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.interactivecuriosity.imagedownload.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"

    private lateinit var imageDownloadReceiver: BroadcastReceiver

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }



        imageDownloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                // Aktualisierung der ImageView wenn heruntergeladen
                val file = File(getExternalFilesDir(null), fileName)
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)

                // Benachrichtigung wenn heruntergeladem‚
                Toast.makeText(this@MainActivity, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
            }
        }

        val filter = IntentFilter("IMAGE_DOWNLOADED")
        registerReceiver(imageDownloadReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(imageDownloadReceiver)
    }

    private fun downloadImage(urlString: String, fileName: String) {

        // Erstelle Intent
        val downloadIntent = Intent(this, ImageDownloadService::class.java)
        downloadIntent.putExtra("imageUrl", urlString)
        downloadIntent.putExtra("fileName", fileName)

        startService(downloadIntent)

        // Benachrichtigung
        Toast.makeText(this@MainActivity, "Herunterladen...", Toast.LENGTH_SHORT).show()
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
