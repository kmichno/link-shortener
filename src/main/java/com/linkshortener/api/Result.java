package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class Result implements Serializable {
    private String day;

    private String numberOfEntries;
}
