package com.example.roomprc

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.roomprc.MyApp.Companion.db
import com.example.roomprc.database.Favicon
import com.example.roomprc.database.History
import com.example.roomprc.database.HistoryDao
import com.example.roomprc.database.faviconDao
import com.example.roomprc.databinding.ActivityMainBinding
import com.example.roomprc.databinding.RvBitBinding
import kotlinx.coroutines.*

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    lateinit var bind: ActivityMainBinding
    lateinit var historyDao: HistoryDao
    lateinit var faviconDao: faviconDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        historyDao = db.historyDao()
        faviconDao = db.faviconDao()

        setWebView()

        bind.search.setOnClickListener {
            bind.webview.post {
                bind.webview.loadUrl(bind.et.text.toString().trim())
            }
        }

        val faviList = arrayListOf<Favicon>()
        val favidapter = MyRvAdapter(faviList) {}
        bind.rvFavicon.adapter = favidapter

        faviconDao.getAllLive().observe(this) {
            faviList.clear()
            faviList.addAll(it)
            favidapter.notifyDataSetChanged()

            bind.rvFavicon.scrollToPosition(faviList.lastIndex)
        }

        historyDao.getAllLive().observe(this) {
            "onCreate: ${it.size}".log()
        }
    }

    private fun setWebView() {
        bind.apply {

            webview.settings.javaScriptEnabled = true
            webview.settings.domStorageEnabled = true
            webview.settings.javaScriptCanOpenWindowsAutomatically = true

            webview.webViewClient = (object : WebViewClient() {

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    "shouldOverrideUrlLoading: ${view.url}".log()
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    "onPageStarted: $url".log()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    "onPageFinished: $url".log()
                }
            })

            webview.webChromeClient = (object : WebChromeClient() {

                override fun onReceivedTitle(view: WebView, title: String) {
                    super.onReceivedTitle(view, title)
                    "onReceivedTitle: ${view.url} $title".log()

                    CoroutineScope(Dispatchers.Main).launch {
                        historyDao.insert(History(title, view.url!!))
                    }
                }

                override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
                    super.onReceivedIcon(view, icon)
                    "onReceivedIcon".log()

                    icon?.let {
                        val st64 = it.to64st()
                        st64.log()
                        CoroutineScope(Dispatchers.Main).launch {
                            faviconDao.insert(Favicon(st64))
                        }
                    }
                }
            })

        }
    }

    override fun onBackPressed() {
        if (bind.webview.canGoBack()) {
            bind.webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

}

class MyRvAdapter(val list: List<Favicon>, val onClick: (Favicon) -> Unit) : RecyclerView.Adapter<MyRvAdapter.VH>() {

    override fun onBindViewHolder(holder: VH, position: Int) {
        val bind = holder.binding
        val data = list[position]


        bind.ivBit.setImageBitmap(data.imgSt64.st64toBmp())


        bind.root.setOnClickListener {
            onClick(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(RvBitBinding.inflate(LayoutInflater.from(parent.context)))
    class VH(var binding: RvBitBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount() = list.size
}