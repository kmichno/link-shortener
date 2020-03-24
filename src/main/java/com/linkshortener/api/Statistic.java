package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.time.LocalDateTime;

@Data
public class Statistic {

    @Id
    private String id;

    private String system;

    private String browser;

    private String ip;

    private String linkId;

    private LocalDateTime dateTime;

    public Statistic(String system, String browser, String ip, String linkId, LocalDateTime dateTime) {
        this.system = system;
        this.browser = browser;
        this.ip = ip;
        this.linkId = linkId;
        this.dateTime = dateTime;
    }
}
