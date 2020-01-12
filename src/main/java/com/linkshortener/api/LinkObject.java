package com.linkshortener.api;

public class LinkObject {

    public String id;

    public String shortUrl;

    public String longUrl;

    public LinkObject(String id, String shortUrl, String longUrl) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }
}
