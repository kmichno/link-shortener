package com.linkshortener.repositories;

import com.linkshortener.api.LinkObject;
import com.linkshortener.api.Statistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface StatisticRepository extends MongoRepository<Statistic, String> {


    @Query("{ 'linkId' : ?0 }")
    List<Statistic> findAllByLinkId(String linkId);

}