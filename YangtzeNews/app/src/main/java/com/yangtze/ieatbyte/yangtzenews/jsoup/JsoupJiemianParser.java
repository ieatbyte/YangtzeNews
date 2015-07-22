package com.yangtze.ieatbyte.yangtzenews.jsoup;

import android.text.TextUtils;
import android.util.Log;

import com.yangtze.ieatbyte.yangtzenews.model.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

public class JsoupJiemianParser {

    final static String TAG = "JsoupJiemianParser";

    public static ArrayList<NewsItem> getTopNews() {
        Log.d(TAG, "start get jiemian top news.");
        String url = "http://www.jiemian.com/";
        Document doc = null;
        ArrayList<NewsItem> items = new ArrayList<NewsItem>();
        try {
            doc = Jsoup.connect(url).timeout(10 * 1000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36").get();
            Elements allImgTags =  doc.getElementsByTag("img");
            if (allImgTags != null) {
                Iterator<Element> iterator = allImgTags.iterator();
                while(iterator.hasNext()) {
                    Element imgItem = iterator.next();
                    Log.d(TAG, "imgItem:" + imgItem);
                    String title = imgItem.attr("alt");
                    if (!TextUtils.isEmpty(title)) {
                        items.add(new NewsItem(0, title, title, null, imgItem.attr("src"), null));
                    }
                }
            }
            //Log.d(TAG, "Jsoup get2:" + doc.getElementsByClass("FosPicCon").toString());
        } catch (Exception e) {
            Log.d(TAG, "Jsoup connect fail.", e);
        } finally {
            // empty
        }
        return items;
    }
}
