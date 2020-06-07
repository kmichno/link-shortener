package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StatisticRequest {

    private int month;

    private int year;
}
