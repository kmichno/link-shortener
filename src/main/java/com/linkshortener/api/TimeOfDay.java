package com.linkshortener.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeOfDay {

    private int entryInMorning = 0;

    private int entryInAfternoon = 0;

    private int entryInEvening = 0;

    private int entryAtNight = 0;

    public void addEntryInMorning() {
        this.entryInMorning++;
    }

    public void addEntryInAfternoon() {
        this.entryInAfternoon++;
    }

    public void addEntryInEvening() {
        this.entryInEvening++;
    }

    public void addEntryAtNight() {
        this.entryAtNight++;
    }
}
