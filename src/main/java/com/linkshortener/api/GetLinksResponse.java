package com.linkshortener.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetLinksResponse {

    public List<LinkObject> links;
}
