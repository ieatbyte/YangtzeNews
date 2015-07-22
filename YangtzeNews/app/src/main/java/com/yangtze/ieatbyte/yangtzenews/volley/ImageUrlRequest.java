package com.yangtze.ieatbyte.yangtzenews.volley;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupIfengParser;

import java.io.UnsupportedEncodingException;

public class ImageUrlRequest extends Request<String> {

    final static String TAG = "ImageUrlRequest";

    private String mUrl;
    private Response.Listener<String> mListener;

    public ImageUrlRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mUrl = url;
        mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            return doParse(response);
        } catch (Exception e) {
            Log.d(TAG, "ImageUrlRequest parse error.", e);
            return Response.error(new ParseError(e));
        }
    }

    private Response<String> doParse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        parsed = JsoupIfengParser.parseNewsContentImageUrl(parsed, mUrl);
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
}
