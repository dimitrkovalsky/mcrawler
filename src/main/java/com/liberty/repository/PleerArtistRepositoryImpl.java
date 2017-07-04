package com.liberty.repository;

import com.liberty.model.PleerArtist;
import com.liberty.model.PleerTrack;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dkovalskyi on 04.07.2017.
 */
@Repository
public class PleerArtistRepositoryImpl implements PleerArtistRepository {
    private final MongoOperations operations;

    @Autowired
    public PleerArtistRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public List<PleerArtist> findByName(String artistName) {
        DBObject dbObject = Criteria.where("pleerArtistName").regex(Pattern.compile(artistName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).getCriteriaObject();

        BasicQuery query = new BasicQuery(dbObject);
        return operations.find(query, PleerArtist.class);
    }
}
