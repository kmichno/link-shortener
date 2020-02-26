package com.linkshortener.repositories;

import com.linkshortener.api.LinkObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LinkRepository extends MongoRepository<LinkObject, String> {


    @Query("{ 'shortUrl' : ?0 }")
    LinkObject findByShortUrl(String shortUrl);

}