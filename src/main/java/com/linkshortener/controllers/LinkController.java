package com.linkshortener.controllers;

import com.linkshortener.api.GetLinksResponse;
import com.linkshortener.api.LinkObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class LinkController {

    @GetMapping("/links}")
    public GetLinksResponse getLinks() {
        List<LinkObject> links = Arrays.asList(new LinkObject("1", "short url", "long url"));
        return new GetLinksResponse(links);
    }

}