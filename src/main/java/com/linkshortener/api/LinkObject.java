package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;

@Data
public class LinkObject implements Serializable {

    @Transient
    public static final String SEQUENCE_NAME = "link_sequence";

    @Id
    private String id;

    private String shortUrl;

    private String longUrl;

    private int numberUniqueEntries = 0;

    private int numberAllEntries = 0;

    public LinkObject(String id, String shortUrl, String longUrl) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
    }

    public void addNumberUniqueEntries() {
        numberUniqueEntries++;
    }

    public void addNumberAllEntries() {
        numberAllEntries++;
    }
}
