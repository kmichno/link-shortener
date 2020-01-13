package com.linkshortener.repositories;

import com.linkshortener.api.LinkObject;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkRepository extends MongoRepository<LinkObject, String> {

}