package com.liberty.repository;

import com.liberty.model.MbAlbum;
import com.liberty.model.MbTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface MbAlbumRepository extends MongoRepository<MbAlbum, String> {
}
