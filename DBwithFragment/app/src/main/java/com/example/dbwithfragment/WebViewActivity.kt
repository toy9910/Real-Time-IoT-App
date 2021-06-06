package com.example.dbwithfragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //webview_pycam.loadUrl("203.229.55.130:8000")
//        val mws = webview_pycam.settings
//        mws.javaScriptEnabled = true
//        mws.loadWithOverviewMode = true
        webview_pycam.setWebViewClient(object : WebViewClient() {})
        webview_pycam.loadUrl("http://toy9910.tistory.com")
    }
}