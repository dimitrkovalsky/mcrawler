package com.liberty.repository;

import com.liberty.model.ZaycevTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface ZaycevTrackRepository extends MongoRepository<ZaycevTrack, Long> {
}
