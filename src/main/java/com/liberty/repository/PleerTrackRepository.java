package com.liberty.repository;

import com.liberty.model.PleerTrack;
import org.springframework.data.mongodb.core.query.TextCriteria;

import java.util.List;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
public interface PleerTrackRepository {
    //    @Query(value = "{'singer' : //?0//i}")
//    List<PleerTrack> findByArtistName(String name);

    //    @Query(value = "{'singer' : //?0//i}")
//    List<PleerTrack> findByArtistName(String name);
//
    List<PleerTrack> findByArtistName(String artistName);
}
