package com.liberty.soundcloud.repo;

import com.liberty.soundcloud.model.SoundCloudTrack;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Repository
public class SoundCloudTrackRepositoryImpl implements SoundCloudRepository {

    private final MongoOperations operations;

    @Autowired
    public SoundCloudTrackRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }


    @Override
    @Cacheable(value = "scTracks", key = "#artistName")
    public List<SoundCloudTrack> findByArtistName(String artistName) {
        DBObject dbObject = Criteria.where("artistName").regex(Pattern.compile(artistName, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)).getCriteriaObject();

        BasicQuery query = new BasicQuery(dbObject);
        return operations.find(query, SoundCloudTrack.class);
    }


    @Override
    public long count() {
        return operations.getCollection("sc-processed-track").count();
    }
}
