package com.liberty.repository;

import com.liberty.model.ZaycevTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface ZaycevTrackRepository {
    void save(Collection<ZaycevTrack> values);

    List<ZaycevTrack> findByArtistName(String artistName);
}
