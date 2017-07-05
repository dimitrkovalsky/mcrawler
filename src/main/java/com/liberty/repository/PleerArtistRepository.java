package com.liberty.repository;

import com.liberty.model.PleerArtist;

import java.util.List;

/**
 * Created by dkovalskyi on 04.07.2017.
 */
public interface PleerArtistRepository {
    List<PleerArtist> findByName(String name);

    PleerArtist findOne(String id);

    List<PleerArtist> findAll();

    List<PleerArtist> findAll(List<String> ids);
}
