package com.yangtze.ieatbyte.yangtzenews;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * Invalid. Just as singleton model.
 */
public class HomeFragmentViewCache {

    HashMap<String, ViewGroup> mCache;

    private static class HomeFragmentViewCacheLoader {
        private static HomeFragmentViewCache INSTANCE = new HomeFragmentViewCache();
    }

    private HomeFragmentViewCache() {
        if (HomeFragmentViewCacheLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
        mCache = new HashMap<String, ViewGroup>();
    }

    public static HomeFragmentViewCache getInstance() {
        return HomeFragmentViewCacheLoader.INSTANCE;
    }

    public void put(String key, ViewGroup viewGroup) {
        mCache.put(key, viewGroup);
    }

    public ViewGroup get(String key) {
        return mCache.get(key);
    }

    public void remove(String key) {
        mCache.remove(key);
    }

    public void clear() {
        mCache.clear();
    }
}
