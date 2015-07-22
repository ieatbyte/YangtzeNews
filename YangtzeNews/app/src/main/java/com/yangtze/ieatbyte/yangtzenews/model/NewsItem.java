package com.yangtze.ieatbyte.yangtzenews.model;

public class NewsItem extends Item{
    private String description;
    private String url;
    private String imageUrl;
    private String fromPublisher;

    public NewsItem(int id, String title, String description, String url, String imageUrl, String fromPublisher) {
        super(id, title);
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.fromPublisher = fromPublisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFromPublisher() {
        return fromPublisher;
    }

    public void setFromPublisher(String fromPublisher) {
        this.fromPublisher = fromPublisher;
    }

    @Override
    public String toString() {
        return String.format("NewsItem{%s, description:%s, url:%s, imageUrl:%s, fromPublisher:%s}", super.toString(), description, url,
                imageUrl, fromPublisher);
    }
}
