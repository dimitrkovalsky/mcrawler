package com.liberty.repository;

import com.liberty.model.PleerTrack;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Repository
public class PleerTrackRepositoryImpl implements PleerTrackRepository {

    private final MongoOperations operations;

    @Autowired
    public PleerTrackRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    @Cacheable(value = "pleerTracks", key = "artistName")
    public List<PleerTrack> findByArtistName(String artistName) {
        DBObject dbObject = Criteria.where("singer").regex(Pattern.compile(artistName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).getCriteriaObject();

        BasicQuery query = new BasicQuery(dbObject);
        return operations.find(query, PleerTrack.class);
    }

}
