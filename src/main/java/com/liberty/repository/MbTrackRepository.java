package com.liberty.repository;

import com.liberty.model.MbArtist;
import com.liberty.model.MbTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface MbTrackRepository extends MongoRepository<MbTrack, String> {
}
