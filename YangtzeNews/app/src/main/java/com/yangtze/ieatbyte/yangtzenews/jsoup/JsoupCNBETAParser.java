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

public class JsoupCNBETAParser {

    final static String TAG = "JsoupCNBETAParser";

    public static ArrayList<NewsItem> getTopNews() {
        Log.d(TAG, "start get cnbeta top news.");
        String url = "http://www.cnbeta.com/";
        Document doc = null;
        ArrayList<NewsItem> items = new ArrayList<NewsItem>();
        try {
            doc = Jsoup.connect(url).timeout(10 * 1000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36").get();
            Elements allItems =  doc.getElementsByClass("item");
            if (allItems != null) {
                Iterator<Element> iterator = allItems.iterator();
                while(iterator.hasNext()) {
                    Element item = iterator.next();
                    Elements titles = item.getElementsByClass("title");
                    String item_url = null;
                    String item_title = null;
                    if (titles != null && titles.size() > 0) {
                        Elements urlElements = titles.get(0).getElementsByTag("a");
                        if (urlElements != null && urlElements.size() > 0) {
                            item_url = "http://www.cnbeta.com" + urlElements.get(0).attr("href");
                            item_title = urlElements.get(0).text();
                        }
                    }
                    Elements pics = item.getElementsByClass("pic");
                    if (pics != null && pics.size() > 0) {
                        Elements imgElements = pics.get(0).getElementsByTag("img");
                        if (imgElements != null && imgElements.size() > 0) {
                            items.add(new NewsItem(0, item_title, null, item_url, imgElements.get(0).attr("src"), null));
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
