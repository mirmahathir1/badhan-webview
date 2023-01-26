package com.mmmbadhanlite;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {
    private WebView mWebView;
    private static String baseURL="https://badhan-buet.web.app/#";
    private static String internalRedirectionString = "badhan-buet.web.app";
    private static String noInternetHTML = "file:///android_asset/landing.html";

    private static String getURL(String subdomain) {
        return baseURL+subdomain;
    }

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      These lines makes the status bar transparent and the website will take up the whole length of the display, Work for Android versions post-KitKat; uncomment if needed
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }

        mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        WebViewClient webViewClient = new MyWebViewClient();
        mWebView.setWebViewClient(webViewClient);

        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U;` Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        loadCorrectUrl();

        mWebView.addJavascriptInterface(new Object()
        {
            @JavascriptInterface
            public void redirect()
            {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadCorrectUrl();

                    }
                });
            }
        }, "browser");

//        if (getResources().getBoolean(R.bool.portrait_only)){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
    }

    private void loadCorrectUrl() {
        if (!DetectConnection.checkInternetConnection(this)) {
            mWebView.loadUrl(noInternetHTML);
        } else {
            mWebView.loadUrl(getURL("/"));
        }
    }

    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            final Uri uri = Uri.parse(url);
            return handleUri(uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            return handleUri(uri);
        }

        public boolean handleUri(final Uri uri) {
            final String host = uri.getHost();
            final String scheme = uri.getScheme();
            if (host.contains(internalRedirectionString)) {
                return false;
            } else {
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        }
    }

    @Override
    public void onBackPressed() {
        WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
        int currIndex = mWebBackForwardList.getCurrentIndex();
        WebHistoryItem item = mWebBackForwardList.getItemAtIndex(currIndex - 1);

        if (currIndex > 0 && !item.getUrl().equals(noInternetHTML)) {
            mWebView.goBack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

}
