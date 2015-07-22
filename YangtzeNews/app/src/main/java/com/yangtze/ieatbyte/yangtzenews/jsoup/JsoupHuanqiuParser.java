package com.yangtze.ieatbyte.yangtzenews.jsoup;

import android.util.Log;

import com.yangtze.ieatbyte.yangtzenews.model.NewsItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;

public class JsoupHuanqiuParser {

    final static String TAG = "JsoupHuanqiuParser";

    public static ArrayList<NewsItem> getTopNews() {
        Log.d(TAG, "start get huanqiu top news.");
        String url = "http://world.huanqiu.com/photo/";
        Document doc = null;
        ArrayList<NewsItem> items = new ArrayList<NewsItem>();
        try {
            doc = Jsoup.connect(url).timeout(10 * 1000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.132 Safari/537.36").get();
            Elements allItems =  doc.getElementsByClass("item");
            if (allItems != null) {
                Iterator<Element> iterator = allItems.iterator();
                while(iterator.hasNext()) {
                    Element item = iterator.next();
                    Elements titles = item.getElementsByTag("a");
                    String item_url = null;
                    String item_title = null;
                    if (titles != null && titles.size() > 0) {
                        item_url = titles.get(0).attr("href");
                        item_title = titles.get(0).attr("title");
                    }
                    Elements pics = item.getElementsByTag("img");
                    if (pics != null && pics.size() > 0) {
                        items.add(new NewsItem(0, item_title, null, item_url, pics.get(0).attr("src"), null));
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
