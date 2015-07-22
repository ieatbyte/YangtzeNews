package com.yangtze.ieatbyte.yangtzenews.adapaters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.yangtze.ieatbyte.yangtzenews.R;
import com.yangtze.ieatbyte.yangtzenews.model.AdItem;
import com.yangtze.ieatbyte.yangtzenews.model.Item;
import com.yangtze.ieatbyte.yangtzenews.model.NewsItem;
import com.yangtze.ieatbyte.yangtzenews.ui.DetailActivity;
import com.yangtze.ieatbyte.yangtzenews.volley.ImageUrlRequest;
import com.yangtze.ieatbyte.yangtzenews.volley.VolleySingleton;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HomeItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final static String TAG = "HomeItemsAdapter";

    final static int VIEW_TYPE_NORMAL = 0;
    final static int VIEW_TYPE_IMAGE = 1;
    final static int VIEW_TYPE_AD = 2;

    private ArrayList<Item> mData;
    private Context mActivityContext;

    public HomeItemsAdapter(ArrayList<Item> data, Context context) {
        mData = data;
        mActivityContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof AdItem) {
            return VIEW_TYPE_AD;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof  NewsItemsNormalViewHolder) {
            final NewsItemsNormalViewHolder realViewHolder = (NewsItemsNormalViewHolder) viewHolder;
            final NewsItem item = (NewsItem)mData.get(i);
            realViewHolder.mTitleView.setText(item.getTitle());
            realViewHolder.mImageView.setImageResource(R.mipmap.ic_launcher);
            realViewHolder.mTag = item;
            if (!TextUtils.isEmpty(item.getUrl())) {
                realViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailActivityIntent = new Intent();
                        detailActivityIntent.setClass(mActivityContext.getApplicationContext(), DetailActivity.class);
                        detailActivityIntent.putExtra("url", item.getUrl());
                        mActivityContext.startActivity(detailActivityIntent);
                    }
                });
            } else {
                realViewHolder.mItemView.setOnClickListener(null);
            }
            if (item.getImageUrl() == null) {
                RequestQueue requestQueue = VolleySingleton.getInstance(mActivityContext.getApplicationContext()).getRequestQueue();
                requestQueue.add(new ImageUrlRequest(Request.Method.GET, item.getUrl(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse response:" + response);
                        item.setImageUrl(response);
                        if (realViewHolder.mTag == item) {
                            fetchImage(realViewHolder, item.getImageUrl());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse", error);
                        item.setImageUrl(""); // Do not load fro network anymore
                    }
                }));
            } else {
                fetchImage(realViewHolder, item.getImageUrl());
            }
        } else if (viewHolder instanceof AdItemsViewHolder) {
            AdItem item = (AdItem)mData.get(i);
            Log.d(TAG, "bind ad item:" + item);
            final AdItemsViewHolder realViewHolder = (AdItemsViewHolder) viewHolder;
            realViewHolder.mImageView.setImageBitmap(item.getAdImage());
            realViewHolder.mOwnerTitleView.setText(item.getAdOwner());
            realViewHolder.mTitleView.setText(item.getTitle());
        }

    }

    private void fetchImage(final NewsItemsNormalViewHolder viewHolder, final String imageUrl) {
        Log.d(TAG, "fetchImage imageUrl:" + imageUrl);
        if (!TextUtils.isEmpty(imageUrl)) {
            VolleySingleton.getInstance(mActivityContext.getApplicationContext()).getImageLoader().get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Log.d(TAG, "fetchImage imageUrl done:" + imageUrl);
                    if (response.getBitmap()!= null && response.getRequestUrl().equals(((NewsItem) viewHolder.mTag).getImageUrl())) {
                        viewHolder.mImageView.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "fetchImage imageUrl cancel:" + imageUrl);
                }
            }, 700, 300);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_AD) {
            return new AdItemsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ad_item_normal_layout, viewGroup, false));
        } else {
            return new NewsItemsNormalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item_normal_layout, viewGroup, false));
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class NewsItemsNormalViewHolder extends RecyclerView.ViewHolder {

        View mItemView;
        ImageView mImageView;
        TextView mTitleView;
        NewsItem mTag;

        public NewsItemsNormalViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mImageView = (ImageView) itemView.findViewById(R.id.news_item_normal_image);
            mTitleView = (TextView) itemView.findViewById(R.id.news_item_normal_title);
        }
    }

    public static class AdItemsViewHolder extends RecyclerView.ViewHolder {
        View mItemView;
        ImageView mImageView;
        TextView mTitleView;
        TextView mOwnerTitleView;

        public AdItemsViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            this.mImageView = (ImageView) itemView.findViewById(R.id.ad_item_image);
            this.mTitleView = (TextView) itemView.findViewById(R.id.ad_item_title);
            this.mOwnerTitleView = (TextView) itemView.findViewById(R.id.ad_item_owner);
        }
    }
}
