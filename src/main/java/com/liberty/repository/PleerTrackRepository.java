package com.liberty.repository;

import com.liberty.model.PleerTrack;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
public interface PleerTrackRepository {

    List<PleerTrack> findByArtistName(String artistName);

    List<PleerTrack> findAll();

    List<PleerTrack> findAll(PageRequest pageRequest);

    long count();

    void save(PleerTrack track);
}
