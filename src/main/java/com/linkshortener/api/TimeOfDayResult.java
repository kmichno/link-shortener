package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class TimeOfDayResult implements Comparable{

    private String day;

    private String entryInMorning;

    private String entryInAfternoon;

    private String entryInEvening;

    private String entryAtNight;

    @Override
    public int compareTo(Object o) {
        TimeOfDayResult timeOfDayResult = (TimeOfDayResult) o;
        return Integer.compare(Integer.parseInt(this.day), Integer.parseInt(timeOfDayResult.day));
    }

}