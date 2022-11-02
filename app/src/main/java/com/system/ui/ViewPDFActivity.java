package com.system.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.system.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewPDFActivity extends AppCompatActivity {

    @BindView(R.id.wView) WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        String url=getIntent().getStringExtra("pdf_url");
        ButterKnife.bind(this);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https:/docs.google.com/gview?embedded=true&url="+url);
    }
}