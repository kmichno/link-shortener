package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Data
@AllArgsConstructor
public class LinkObject {

    @Transient
    public static final String SEQUENCE_NAME = "link_sequence";

    @Id
    public String id;

    public String shortUrl;

    public String longUrl;

}
