package com.linkshortener.controllers;

import com.linkshortener.api.GetLinksResponse;
import com.linkshortener.api.LinkObject;
import com.linkshortener.api.ShortenLinkRequest;
import com.linkshortener.api.Statistic;
import com.linkshortener.repositories.LinkRepository;
import com.linkshortener.repositories.StatisticRepository;
import com.linkshortener.services.SequenceGeneratorService;
import com.linkshortener.services.ShortenerService;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class LinkController {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    @GetMapping("/links")
    public GetLinksResponse getLinks() {
        List<LinkObject> links = linkRepository.findAll();
        return new GetLinksResponse(links);
    }

    @GetMapping("/link/{id}")
    public LinkObject getLinkById(@PathVariable String id) throws Exception{
        return linkRepository.findById(id).orElseThrow(Exception::new);
    }

    @DeleteMapping("/link/delete/{id}")
    public void deleteLinkById(@PathVariable String id) {
        linkRepository.deleteById(id);
    }

    @RequestMapping(path = "/{shortLink:[a-z]+}")
    public ResponseEntity moveToAnotherPage(HttpServletRequest request, @PathVariable("shortLink") String shortLink) {
        LinkObject linkObject = linkRepository.findByShortUrl(shortLink);
        linkObject.addNumberAllEntries();
        // obsługa IP:
        linkObject.addNumberUniqueEntries();
        linkRepository.save(linkObject);
        saveStatistic(request, linkObject);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, linkObject.getLongUrl()).build();
    }

    private void saveStatistic(HttpServletRequest request, LinkObject linkObject) {
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        OperatingSystem os = userAgent.getOperatingSystem();
        Browser browser = userAgent.getBrowser();
        Statistic statistic = new Statistic(os.getName(), browser.getName(), "", linkObject.getId());
        statisticRepository.save(statistic);
    }

    @RequestMapping(path = "link/short", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json; charset=utf-8")
    public LinkObject shortenLink(@RequestBody ShortenLinkRequest shortenLinkRequest) {
        int id = sequenceGenerator.generateSequence(LinkObject.SEQUENCE_NAME);
        return linkRepository.save(new LinkObject(String.valueOf(id), ShortenerService.encode(id), shortenLinkRequest.getLongUrl()));
    }

    @GetMapping("/statistic/{linkId}")
    public List<Statistic> getAllStatisticsByLinkId(@PathVariable String linkId) {
        return statisticRepository.findAllByLinkId(linkId);
    }

}