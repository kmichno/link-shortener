package com.linkshortener.repositories;

import com.linkshortener.api.LinkObject;
import com.linkshortener.api.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StatisticRepository extends MongoRepository<Statistic, String> {


    @Query("{ 'linkId' : ?0 }")
    List<Statistic> findAllByLinkId(String linkId);

    @Query("{ linkId: ?0, dateTime: { $gte: ?1, $lte: ?2 }}")
    List<Statistic> findAllByLinkIdAndDate(String linkId, LocalDate startDate, LocalDate endDate);

    @Query("{ dateTime: { $gte: ?0, $lte: ?1 }}")
    List<Statistic> findAllByDate(LocalDate startDate, LocalDate endDate);

}