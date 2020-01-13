package com.linkshortener.controllers;

import com.linkshortener.api.GetLinksResponse;
import com.linkshortener.api.LinkObject;
import com.linkshortener.api.ShortenLinkRequest;
import com.linkshortener.repositories.LinkRepository;
import com.linkshortener.services.SequenceGeneratorService;
import com.linkshortener.services.ShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LinkController {

    @Autowired
    private LinkRepository repository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    @GetMapping("/links")
    public GetLinksResponse getLinks() {
        List<LinkObject> links = repository.findAll();
        return new GetLinksResponse(links);
    }

    @GetMapping("/link/{id}")
    public LinkObject getLinkById(@PathVariable String id) throws Exception{
        return repository.findById(id).orElseThrow(Exception::new);
    }

    @DeleteMapping("/link/delete/{id}")
    public void deleteLinkById(@PathVariable String id) {
        repository.deleteById(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity moveToAnotherPage(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, "https://www.google.com/").build();
    }

    @RequestMapping(path = "link/short", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json; charset=utf-8")
    public LinkObject shortenLink(@RequestBody ShortenLinkRequest shortenLinkRequest) {
        int id = sequenceGenerator.generateSequence(LinkObject.SEQUENCE_NAME);
        return repository.save(new LinkObject(String.valueOf(id), ShortenerService.encode(id), shortenLinkRequest.longUrl));
    }

}