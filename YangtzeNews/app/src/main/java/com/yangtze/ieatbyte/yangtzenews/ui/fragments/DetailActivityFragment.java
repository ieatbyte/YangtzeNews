package com.yangtze.ieatbyte.yangtzenews.ui.fragments;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yangtze.ieatbyte.yangtzenews.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    View mRootView;
    WebView mWebView;

    ProgressDialog mProgressBar;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_detail, container, false);
        }
        mWebView = (WebView)mRootView.findViewById(R.id.detail_webview);
        String url = getActivity().getIntent().getStringExtra("url");
        //mProgressBar = ProgressDialog.show(getActivity(), "Loading", url);
        if (TextUtils.isEmpty(url)) {
            url  = "http://www.baidu.com";
        }
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.setInitialScale(1);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        //mWebView.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3");
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                if (mProgressBar.isShowing()) {
//                    mProgressBar.dismiss();
//                }
            }

            public boolean shouldOverrideUrlLoading(WebView viewx, String urlx) {
                viewx.loadUrl(urlx);
                return true;
            }
        });

        return mRootView;
    }
}
