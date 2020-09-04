package com.example.mobilegogain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView mywebView;
    private WebView printwebView;
    ArraySet<PrintJob> printJobs = new ArraySet<>();
    String mainUrl = "http://mobile.go-gain.com/";;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mywebView = findViewById(R.id.webView);
        printwebView = mywebView;
        WebSettings webSettings = mywebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mywebView.loadUrl(mainUrl);
        mywebView.setWebViewClient(new WebViewClient());
        mywebView.addJavascriptInterface(new WebViewJavaScriptInterface(this, mywebView), "window");
        // "window is used to call methods exposed from javascript interface, in this example case print
        // method can be called by window.print()"
        Log.d("creation", mywebView.getUrl());

    }

    @Override
    public void onBackPressed() {
        String historyUrl="";
        WebBackForwardList mWebBackForwardList = mywebView.copyBackForwardList();
        if (mWebBackForwardList.getCurrentIndex() > 0){
            historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()-1).getUrl();
        }

        // Previous url is in historyUrl
        if(mywebView.canGoBack() && !historyUrl.equals(mainUrl)
                && !mywebView.getUrl().equals(mainUrl)){
            mywebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    public class WebViewJavaScriptInterface {

        Context context;
        WebView view;
        public WebViewJavaScriptInterface(Context context, WebView view) {
            this.context = context;
            this.view = view;
            Log.d("window.print()", view.getUrl());
        }

        /**
         * method defined by developer, which will be called by web page , from web developer
         * this you can call when want to take a print, either on click of a button or directly
         * Convert HTML data, to be printed,  in form of a string and pass to this method.
         *
         */
        @android.webkit.JavascriptInterface
        public void print() {
            Log.d("window.print()", view.getUrl());
            createWebPrintJob(view);
        }
    }

    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " Document";

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        PrintJob printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());

        if (printJob.isCompleted()) {
            Toast.makeText(getApplicationContext(), R.string.print_complete, Toast.LENGTH_LONG).show();
        } else if (printJob.isFailed()) {
            Toast.makeText(getApplicationContext(), R.string.print_failed, Toast.LENGTH_LONG).show();
        }

        printJobs.add(printJob);
    }
}