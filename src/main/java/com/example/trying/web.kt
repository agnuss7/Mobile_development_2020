package com.example.trying

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.ConnectivityManager
import android.webkit.WebResourceError

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.File


class web : AppCompatActivity() {
    var msg: String? = ""
    var lastMsg = ""

    private fun downloadFile(url: String) {

        val directory = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/quests")


        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalFilesDir(
                    applicationContext,
                    Environment.DIRECTORY_DOWNLOADS,
                    "/quests/"+
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Atsiuntimas nepavyko"
            DownloadManager.STATUS_RUNNING -> "Siunčiama..."
            DownloadManager.STATUS_PAUSED->"Pauzė..."
            DownloadManager.STATUS_PENDING->"Laukiama..."
            DownloadManager.STATUS_SUCCESSFUL -> "Failas atsiųstas"
            else -> "There's nothing to download"
        }
        return msg
    }
    public fun isConnected():Boolean{
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo.isConnected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        val myWebView: WebView = findViewById(R.id.shop)
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

            myWebView.settings.javaScriptEnabled = true
            myWebView.loadUrl("http://100.24.14.70:8080/")

            myWebView.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.contains("download_files")) {
                        view.loadUrl(url)
                        return false
                    } else {

                        return false
                    }

                }

            }

            )
            myWebView.setDownloadListener(DownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                downloadFile(url)
            })




    }
    override fun onRestart() {
        super.onRestart()
        finish()
        startActivity(getIntent())
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.title, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.to_shop -> {
            val inti = Intent(this, web::class.java)
            finish()
            this.startActivity(inti)
            true
        }

        R.id.to_main -> {

            val inti=Intent(this,MainActivity::class.java)
            this.startActivity(inti)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
