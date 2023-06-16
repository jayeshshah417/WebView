package com.example.mpigweb

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.SafeBrowsingResponseCompat
import androidx.webkit.WebViewClientCompat
import androidx.webkit.WebViewFeature

class MainActivitykt : AppCompatActivity() {

    private lateinit var myWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myWebView = findViewById(R.id.webview)

        // Configure the settings for the WebView
        val webSettings: WebSettings = myWebView.settings
        webSettings.userAgentString = "'Mozilla/5.0 (Linux; Android 10; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36'"
        webSettings.javaScriptEnabled = true // JavaScript support
        webSettings.domStorageEnabled = true // DOM Storage support
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)
        webSettings.builtInZoomControls = true // Zoom controls
        webSettings.displayZoomControls = false // Don't display built-in zoom controls
        webSettings.setSupportMultipleWindows(true) // Multiple windows support
        webSettings.allowContentAccess = true // Content access
        webSettings.allowFileAccess = true // File access
        webSettings.allowFileAccessFromFileURLs = true // File access from file URLs
        webSettings.allowUniversalAccessFromFileURLs = true // Universal access from file URLs

        // Load a website
        myWebView.loadUrl("https://red-dev.mayamd.ai/?id=64875bab9823de967336b3cd")

        // Set a WebViewClient for this WebView
        myWebView.webViewClient = WebViewClient()
    }

    // This method is called when the back button is pressed.
    override fun onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }


    class MyWebViewClient : WebViewClientCompat() {
        // Automatically go "back to safety" when attempting to load a website that
        // Google has identified as a known threat. An instance of WebView calls
        // this method only after Safe Browsing is initialized, so there's no
        // conditional logic needed here.
        override fun onSafeBrowsingHit(
            view: WebView,
            request: WebResourceRequest,
            threatType: Int,
            callback: SafeBrowsingResponseCompat
        ) {
            // The "true" argument indicates that your app reports incidents like
            // this one to Safe Browsing.
            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                callback.backToSafety(true)
            }
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            Toast.makeText(view.context, "Load Completed", Toast.LENGTH_LONG).show()
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return Uri.parse(url).host != "ambassador-misc.s3-ap-southeast-1.amazonaws.com"
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            // This is my website, so do not override; let my WebView load the page
            return "ambassador-misc.s3-ap-southeast-1.amazonaws.com" != request.url.host
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        }
    }

}
