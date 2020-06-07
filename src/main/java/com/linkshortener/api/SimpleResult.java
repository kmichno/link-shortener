package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class SimpleResult implements Serializable {
    private String name;

    private int value;
}
