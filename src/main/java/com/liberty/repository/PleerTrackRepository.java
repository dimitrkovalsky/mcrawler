package com.liberty.repository;

import com.liberty.model.PleerTrack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Repository
public interface PleerTrackRepository extends MongoRepository<PleerTrack, String> {
    @Query(value = "{'song':{$regex : ?0, &options: 'i'}}") // Todo: change to $text $search
    List<PleerTrack> findByName(String name);

}
