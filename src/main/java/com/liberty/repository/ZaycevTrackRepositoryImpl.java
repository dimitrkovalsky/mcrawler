package com.liberty.repository;

import com.liberty.model.PleerTrack;
import com.liberty.model.ZaycevTrack;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Repository
public class ZaycevTrackRepositoryImpl implements ZaycevTrackRepository {

    private final MongoOperations operations;

    @Autowired
    public ZaycevTrackRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public void save(Collection<ZaycevTrack> values) {
        operations.save(values);
    }

    @Override
    @Cacheable(value = "zaycevTracks", key = "artistName")
    public List<ZaycevTrack> findByArtistName(String artistName) {
        DBObject dbObject = Criteria.where("zaycevArtistName").regex(Pattern.compile(artistName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).getCriteriaObject();

        BasicQuery query = new BasicQuery(dbObject);
        return operations.find(query, ZaycevTrack.class);
    }
}
