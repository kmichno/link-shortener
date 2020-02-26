package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Data
public class Statistic {

    @Id
    private String id;

    private String system;

    private String browser;

    private String ip;

    private String linkId;

    public Statistic(String system, String browser, String ip, String linkId) {
        this.system = system;
        this.browser = browser;
        this.ip = ip;
        this.linkId = linkId;
    }
}
