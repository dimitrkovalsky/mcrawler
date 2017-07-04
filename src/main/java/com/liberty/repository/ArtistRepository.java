package com.liberty.repository;

import com.liberty.model.PleerArtist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Repository
public interface ArtistRepository extends MongoRepository<PleerArtist, ObjectId> {
    @Query(value = "{'pleerArtistName':{$regex : ?0, &options: 'i'}}")
    List<PleerArtist> findByName(String name);
}