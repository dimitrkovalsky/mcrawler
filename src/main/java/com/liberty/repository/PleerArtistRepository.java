package com.liberty.repository;

import com.liberty.model.PleerArtist;

import java.util.List;

/**
 * Created by dkovalskyi on 04.07.2017.
 */
public interface PleerArtistRepository {
    List<PleerArtist> findByName(String name);
}
