package com.liberty.repository;

import com.liberty.model.ZaycevArtist;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface ZaycevArtistRepository extends MongoRepository<ZaycevArtist, Long> {
}
