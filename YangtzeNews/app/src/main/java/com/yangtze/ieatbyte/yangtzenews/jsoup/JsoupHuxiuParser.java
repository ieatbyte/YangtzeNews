package com.yangtze.ieatbyte.yangtzenews.jsoup;

import android.util.Log;

import com.yangtze.ieatbyte.yangtzenews.model.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

public class JsoupHuxiuParser {

    final static String TAG = "JsoupHuxiuParser";

    public static ArrayList<NewsItem> getTopNews() {
        Log.d(TAG, "start get huxiu top news.");
        String url = "http://www.huxiu.com/";
        Document doc = null;
        ArrayList<NewsItem> items = new ArrayList<NewsItem>();
        try {
            doc = Jsoup.connect(url).timeout(10 * 1000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36").get();
            Elements allItems = doc.select("a.pull-left.mod-thumb"); //doc.getElementsByClass("pull-left mod-thumb");
            if (allItems != null) {
                Log.d(TAG, "get all items size:" + allItems.size());
                String item_url = null;
                String item_title = null;
                String item_img_url = null;
                if (allItems != null && allItems.size() > 0) {
                    for (int i = 0; i < allItems.size(); ++i) {
                        item_url = "http://www.huxiu.com" + allItems.get(i).attr("href");
                        Elements imges = allItems.get(i).getElementsByTag("img");
                        if (imges != null && imges.size() > 0) {
                            item_img_url = imges.get(0).attr("data-original");
                            item_title = imges.get(0).attr("alt");
                            Log.d(TAG, "get img src:" + item_img_url);
                            items.add(new NewsItem(0, item_title, null, item_url, item_img_url, null));
                        }
                    }
                }
            }
            //Log.d(TAG, "Jsoup get2:" + doc.getElementsByClass("FosPicCon").toString());
        } catch (Exception e) {
            Log.d(TAG, "Jsoup connect fail.", e);
        } finally {
            // empty
        }
        Log.d(TAG, "get size:" + items.size());
        return items;
    }
}
