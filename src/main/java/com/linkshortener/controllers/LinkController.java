package com.linkshortener.controllers;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.linkshortener.api.GetLinksResponse;
import com.linkshortener.api.LinkObject;
import com.linkshortener.api.Result;
import com.linkshortener.api.ShortenLinkRequest;
import com.linkshortener.api.SimpleResult;
import com.linkshortener.api.Statistic;
import com.linkshortener.api.StatisticRequest;
import com.linkshortener.api.TimeOfDay;
import com.linkshortener.api.TimeOfDayResult;
import com.linkshortener.repositories.LinkRepository;
import com.linkshortener.repositories.StatisticRepository;
import com.linkshortener.services.SequenceGeneratorService;
import com.linkshortener.services.ShortenerService;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

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
    public LinkObject getLinkById(@PathVariable("id") String id) throws Exception{
        return linkRepository.findById(id).orElseThrow(Exception::new);
    }

    @DeleteMapping("/link/delete/{id}")
    public void deleteLinkById(@PathVariable("id") String id) {
        linkRepository.deleteById(id);
    }

    @RequestMapping(path = "/{shortLink:[a-z]+}")
    public ResponseEntity moveToAnotherPage(HttpServletRequest request, @PathVariable("shortLink") String shortLink) {
        LinkObject linkObject = linkRepository.findByShortUrl(shortLink);
        linkObject.addNumberAllEntries();
        linkObject.addNumberUniqueEntries();
        linkRepository.save(linkObject);
        saveStatistic(request, linkObject);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, linkObject.getLongUrl()).cacheControl(CacheControl.noCache()).build();
    }

    private void saveStatistic(HttpServletRequest request, LinkObject linkObject) {
        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        OperatingSystem os = userAgent.getOperatingSystem();
        Browser browser = userAgent.getBrowser();
        Statistic statistic = new Statistic(os.getName(), browser.getName(), "", linkObject.getId(), LocalDateTime.now());
        statisticRepository.save(statistic);
    }

    @PostMapping("link/short")
    public LinkObject shortenLink(@RequestBody ShortenLinkRequest shortenLinkRequest) {
        int id = sequenceGenerator.generateSequence(LinkObject.SEQUENCE_NAME);
        return linkRepository.save(new LinkObject(String.valueOf(id), ShortenerService.encode(id), shortenLinkRequest.getLongUrl()));
    }

    @PostMapping("/statistic/browser/{linkId}")
    public List<SimpleResult> getAllStatisticsBrowserByLinkId(@PathVariable("linkId") String linkId, @RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByLinkIdAndDate(linkId, startDate, endDate);
        Map<String, Long> occurrances = statistics.stream().collect(
                groupingBy(d -> d.getBrowser(), counting()));
        return occurrances.keySet().stream().map(p-> new SimpleResult(p, occurrances.get(p).intValue())).collect(Collectors.toList());
    }

    @PostMapping("/statistic/system/{linkId}")
    public List<SimpleResult> getAllStatisticsSystemByLinkId(@PathVariable("linkId") String linkId, @RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByLinkIdAndDate(linkId, startDate, endDate);
        Map<String, Long> occurrances = statistics.stream().collect(
                groupingBy(d -> d.getSystem(), counting()));
        return occurrances.keySet().stream().map(p-> new SimpleResult(p, occurrances.get(p).intValue())).collect(Collectors.toList());
    }

    @PostMapping("/statistic/days/{id}")
    public List<Result> getAllStatisticsByLinkId(@PathVariable("id") String linkId, @RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByLinkIdAndDate(linkId, startDate, endDate);
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;
        List<LocalDate> dates = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());

        Map<LocalDate, Long> occurrances = statistics.stream().collect(
                groupingBy(d -> d.getDateTime().toLocalDate(), counting()));
        for (LocalDate date : dates) {
            if(!occurrances.containsKey(date)) {
                occurrances.put(date, 0L);
            }
        }
        List<Result> results = new ArrayList<>();
        for(LocalDate date : occurrances.keySet()) {
            results.add(new Result(String.valueOf(date.getDayOfMonth()), occurrances.get(date).toString()));
        }
        return results.stream().sorted(Comparator.comparingInt(p-> Integer.valueOf(p.getDay()))).collect(Collectors.toList());
    }

    @PostMapping("/statistic/all/days")
    public List<Result> getAllStatistics(@RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByDate(startDate, endDate);
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;
        List<LocalDate> dates = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());

        Map<LocalDate, Long> occurrances = statistics.stream().collect(
                groupingBy(d -> d.getDateTime().toLocalDate(), counting()));
        for (LocalDate date : dates) {
            if(!occurrances.containsKey(date)) {
                occurrances.put(date, 0L);
            }
        }
        List<Result> results = new ArrayList<>();
        for(LocalDate date : occurrances.keySet()) {
            results.add(new Result(String.valueOf(date.getDayOfMonth()), occurrances.get(date).toString()));
        }
        return results.stream().sorted(Comparator.comparingInt(p-> Integer.valueOf(p.getDay()))).collect(Collectors.toList());
    }

    public static boolean isBetween(LocalTime candidate, LocalTime start, LocalTime end) {
        return !candidate.isBefore(start) && !candidate.isAfter(end);
    }

    @PostMapping("/statistic/times/{id}")
    public List<TimeOfDayResult> getAllStatisticsTimeByLinkId(@PathVariable("id") String linkId, @RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByLinkIdAndDate(linkId, startDate, endDate);
        return getTimeOfDayResults(startDate, endDate, statistics);
    }

    @PostMapping("/statistic/all/times")
    public List<TimeOfDayResult> getAllStatisticsTime(@RequestBody StatisticRequest statisticRequest) {
        LocalDate startDate = LocalDate.of(statisticRequest.getYear(), statisticRequest.getMonth(), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Statistic> statistics = statisticRepository.findAllByDate(startDate, endDate);
        return getTimeOfDayResults(startDate, endDate, statistics);
    }

    private List<TimeOfDayResult> getTimeOfDayResults(LocalDate startDate, LocalDate endDate, List<Statistic> statistics) {
        Map<LocalDate, TimeOfDay> timesOfDayInMonth = new HashMap<>();
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate)+1;
        List<LocalDate> allDatesInMonth = IntStream.iterate(0, i -> i + 1)
                .limit(numOfDaysBetween)
                .mapToObj(i -> startDate.plusDays(i))
                .collect(Collectors.toList());

        for (LocalDate date : allDatesInMonth) {
            if(!timesOfDayInMonth.containsKey(date)) {
                timesOfDayInMonth.put(date, new TimeOfDay());
            }
        }
        for(Statistic statistic : statistics) {
            LocalDate date = statistic.getDateTime().toLocalDate();
            if(timesOfDayInMonth.containsKey(date)) {
                LocalTime time = statistic.getDateTime().toLocalTime();
                TimeOfDay timeOfDay = setCorrectTimeOfDay(time, timesOfDayInMonth.get(date));
                timesOfDayInMonth.replace(date, timeOfDay);
            }
        }

        List<TimeOfDayResult> results = new ArrayList<>();
        for(LocalDate date : timesOfDayInMonth.keySet()) {
            TimeOfDay timeOfDay = timesOfDayInMonth.get(date);
            results.add(new TimeOfDayResult(
                    String.valueOf(date.getDayOfMonth()),
                    String.valueOf(timeOfDay.getEntryInMorning()),
                    String.valueOf(timeOfDay.getEntryInAfternoon()),
                    String.valueOf(timeOfDay.getEntryInEvening()),
                    String.valueOf(timeOfDay.getEntryAtNight()))
            );
        }
        return results.stream().sorted().collect(Collectors.toList());
    }

    private TimeOfDay setCorrectTimeOfDay(LocalTime time, TimeOfDay timeOfDay) {
        if(isBetween(time, LocalTime.of(8, 0), LocalTime.of(14, 0))) {
            timeOfDay.addEntryInMorning();
        } else if(isBetween(time, LocalTime.of(14, 0), LocalTime.of(20, 0))) {
            timeOfDay.addEntryInAfternoon();
        } else if(isBetween(time, LocalTime.of(20, 0), LocalTime.of(23, 59)) || isBetween(time, LocalTime.of(0, 0), LocalTime.of(2, 0))) {
            timeOfDay.addEntryInEvening();
        } else if(isBetween(time, LocalTime.of(2, 0), LocalTime.of(8, 0))) {
            timeOfDay.addEntryAtNight();
        }
        return timeOfDay;
    }

}