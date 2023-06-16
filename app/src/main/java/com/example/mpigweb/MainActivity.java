package com.example.mpigweb;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.SafeBrowsingResponseCompat;
import androidx.webkit.WebViewClientCompat;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private boolean safeBrowsingIsInitialized;
    private EditText et_url;
    private Button bt_load;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_url = findViewById(R.id.et_url);
        bt_load = findViewById(R.id.bt_load);
        bt_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(et_url.getText().toString());
            }
        });
        Intent intent = getIntent();
         webView = findViewById(R.id.webview);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString("'Mozilla/5.0 (Linux; Android 10; Pixel 3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Mobile Safari/537.36'");
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                // Grant audio permission request
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.getSettings().setSafeBrowsingEnabled(false);
        }

        safeBrowsingIsInitialized = false;

        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this, new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean success) {
                    safeBrowsingIsInitialized = true;
                    if (!success) {
                        Log.e("ambassador app", "Unable to initialize Safe Browsing!");
                    }else{

                        webView.loadUrl(et_url.getText().toString());
                    }
                }
            });
        }

    }

    static class JSBridge {
        Activity mActivity;
        int mTrainingProgress;

        /** Instantiate the interface and set the context */
        JSBridge(Activity activity, int trainingProgress) {
            mActivity = activity;
            mTrainingProgress = trainingProgress;

        }

        @JavascriptInterface
        public void sentTrainingModuleProgress(boolean trainingModuleCompleted, int score){
            Log.i("Progress", String.valueOf(trainingModuleCompleted));
            if (mTrainingProgress == 0)
                mActivity.finish();
            }


    }

    private static class MyWebViewClient extends WebViewClientCompat {
        // Automatically go "back to safety" when attempting to load a website that
        // Google has identified as a known threat. An instance of WebView calls
        // this method only after Safe Browsing is initialized, so there's no
        // conditional logic needed here.
        @Override
        public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponseCompat callback) {
            // The "true" argument indicates that your app reports incidents like
            // this one to Safe Browsing.
            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                callback.backToSafety(true);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Toast.makeText(view.getContext(),"Load Completed",Toast.LENGTH_LONG).show();
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return !Uri.parse(url).getHost().equals("ambassador-misc.s3-ap-southeast-1.amazonaws.com");
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // This is my website, so do not override; let my WebView load the page
            return !"ambassador-misc.s3-ap-southeast-1.amazonaws.com".equals(request.getUrl().getHost());
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        }

    }

    @Override
    public void onBackPressed() {

            super.onBackPressed();
    }
}
