package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class ImageDownloadService : IntentService("ImageDownloadService") {
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val imageUrl = intent.getStringExtra("imageUrl")
            val fileName = intent.getStringExtra("fileName")
            downloadImage(imageUrl, fileName)
        }
    }

    private fun downloadImage(urlString: String?, fileName: String?) {
        if (urlString != null && fileName != null) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val file = File(getExternalFilesDir(null), fileName)
                FileOutputStream(file).use { output ->
                    inputStream.copyTo(output)
                }

                val downloadCompleteIntent = Intent("IMAGE_DOWNLOADED")
                sendBroadcast(downloadCompleteIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}