package com.yangtze.ieatbyte.yangtzenews.ui.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yangtze.ieatbyte.yangtzenews.HomeFragmentViewCache;
import com.yangtze.ieatbyte.yangtzenews.R;
import com.yangtze.ieatbyte.yangtzenews.adapaters.HomeItemsAdapter;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupCNBETAParser;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupHuanqiuParser;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupHuxiuParser;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupIfengParser;
import com.yangtze.ieatbyte.yangtzenews.jsoup.JsoupJiemianParser;
import com.yangtze.ieatbyte.yangtzenews.model.AdItem;
import com.yangtze.ieatbyte.yangtzenews.model.Item;
import com.yangtze.ieatbyte.yangtzenews.model.NewsItem;
import com.yangtze.ieatbyte.yangtzenews.ui.widgets.swiperefresh.MySwipeRefreshLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    final static String TAG = "HomeFragment";

    RecyclerView mHomeRecyclerView;
    HomeItemsAdapter mHomeItemsAdapter;
    ArrayList<Item> mData;
//    SwipeRefreshLayout mSwipeRefresh;
    MySwipeRefreshLayout mMySwipeRefresh;
    String mPublisher;
    View mRootView;

    Bitmap mDefaultAdBitmap;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPublisher = getArguments().getString("publisher");
        Log.d(TAG, "onCreateView mPublisher:" + mPublisher);
        if(mRootView == null) {
            Log.d(TAG, "inflater view!");
            mRootView = inflater.inflate(R.layout.fragment_main, container, false);
        } else {
            Log.d(TAG, "reuse view!");
        }
        mHomeRecyclerView = (RecyclerView) mRootView.findViewById(R.id.home_recycler_view);
        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mHomeRecyclerView.setHasFixedSize(true);
        mHomeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (mData == null) {
            mData = new ArrayList<Item>();
            new TestJsoupAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPublisher);
        }
        //mData.add(new NewsItem(0, "中文中文", null, null, null, null));
        mHomeItemsAdapter = new HomeItemsAdapter(mData, getActivity());
        mHomeRecyclerView.setAdapter(mHomeItemsAdapter);
        mHomeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged newState:" + newState + ", view:" + recyclerView);
            }
        });
//        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
//        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.d(TAG, "on Refresh");
//            }
//        });
        mMySwipeRefresh = (MySwipeRefreshLayout) mRootView.findViewById(R.id.swipe_refresh);
        mMySwipeRefresh.setOnRefreshListener(new MySwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "on My Refresh");
            }
        });
        mDefaultAdBitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_launcher);
        return mRootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach mPublisher:" + mPublisher);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume mPublisher:" + mPublisher);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause mPublisher:" + mPublisher);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop mPublisher:" + mPublisher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView mPublisher:" + mPublisher);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy mPublisher:" + mPublisher);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart mPublisher:" + mPublisher);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate mPublisher:" + mPublisher);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach mPublisher:" + mPublisher);
    }

    class TestJsoupAsyncTask extends AsyncTask<String, Void, ArrayList<NewsItem>> {
        @Override
        protected void onPostExecute(ArrayList<NewsItem> s) {
            super.onPostExecute(s);
            int oldCount = mData.size();
            mData.addAll(s);
            // add our ad
            if (mData.size() > 3) {
                AdItem adItem = new AdItem(0, "Our best try.", "iEatbyte Inc.", mDefaultAdBitmap);
                mData.add(2, adItem);
            }
            //mHomeItemsAdapter.notifyDataSetChanged();
            Log.d("wh_debug", "size:" + s.size());
            mHomeItemsAdapter.notifyItemRangeInserted(oldCount, s.size());
        }

        @Override
        protected ArrayList<NewsItem> doInBackground(String... params) {
            // test
            if (params.length == 1) {
                if(params[0].equals("Ifeng")) {
                    return JsoupIfengParser.startTest();
                } else if (params[0].equals("Jiemian")) {
                    return JsoupJiemianParser.getTopNews();
                } else if (params[0].equals("cnbeta")) {
                    return JsoupCNBETAParser.getTopNews();
                } else if (params[0].equals("huxiu")) {
                    return JsoupHuxiuParser.getTopNews();
                } else if (params[0].equals("huanqiu")) {
                    return JsoupHuanqiuParser.getTopNews();
                }
            }
            return JsoupJiemianParser.getTopNews();
            //return JsoupIfengParser.startTest();
        }
    }
}