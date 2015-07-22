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

public class JsoupIfengParser {

    final static String TAG = "JsoupIfengParser";

    public static ArrayList<NewsItem> startTest() {
        Log.d(TAG, "start test.");
        String url = "http://www.ifeng.com/";
        Document doc = null;
        ArrayList<NewsItem> items = new ArrayList<NewsItem>();
        try {
            doc = Jsoup.connect(url).timeout(10 * 1000).userAgent("Mozilla").get();
            //Log.d(TAG, "Jsoup get1:" + doc.body().toString());
            //Log.d(TAG, "Jsoup get2:" + doc.getElementsByClass("NavM").toString());
            //Log.d(TAG, "Jsoup get2:" + doc.getElementById("headLineDefault").toString());
            Elements es =  doc.getElementsByClass("FNewMTopLis");
            //Elements es =  doc.getElementsByClass("clearfix");
            //wrap clearfix
            if (es != null) {
                Iterator<Element> iterator = es.iterator();
                while(iterator.hasNext()) {
                    Element et = iterator.next();
                    Elements lie = et.getElementsByTag("li");
                    if (lie != null) {
                        Iterator<Element> iterator2 = lie.iterator();
                        while(iterator2.hasNext()) {
                            Element item = iterator2.next();
                            if (item != null) {
                                Elements eAs = item.getElementsByTag("a");
                                if (eAs != null && eAs.size() == 1) {
                                    Log.d(TAG, "get " + eAs.get(0).text());
                                    Log.d(TAG, "get " + eAs.get(0).attr("href"));
                                    items.add(new NewsItem(0, eAs.get(0).text(), null, eAs.get(0).attr("href"), null, null));
                                }
                            }
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
        return items;
    }

    public static String parseNewsContentImageUrl(String html, String webUrl) {
        String url = null;
        if (!TextUtils.isEmpty(html)) {
            Document doc = Jsoup.parse(html);
            Elements picsClasses = doc.getElementsByClass("detailPic");
            if (picsClasses != null && picsClasses.size() > 0) {
                Elements imgsElements = picsClasses.get(0).getElementsByTag("img");
                if (imgsElements != null && imgsElements.size() > 0) {
                    Element firstPicElement = imgsElements.get(0);
                    url = firstPicElement.attr("src");
                }
            }
        }
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "parse image url fail from web page:" + webUrl);
        } else {
            Log.d(TAG, "parse image url ok from web page" + webUrl + ", with:" + url);
        }
        return url;
    }
}
