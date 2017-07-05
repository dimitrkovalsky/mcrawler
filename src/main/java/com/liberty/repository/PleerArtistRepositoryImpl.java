package com.liberty.repository;

import com.liberty.model.PleerArtist;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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

    @Override
    public PleerArtist findOne(String id) {
        Query query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        return operations.findOne(query, PleerArtist.class);
    }

    @Override
    public List<PleerArtist> findAll() {
        return operations.findAll(PleerArtist.class);
    }

    @Override
    public List<PleerArtist> findAll(List<String> ids) {
        List<ObjectId> objectIds = ids.stream().map(ObjectId::new).collect(Collectors.toList());
        Query query = Query.query(Criteria.where("_id").in(objectIds));
        return operations.find(query, PleerArtist.class);
    }
}
