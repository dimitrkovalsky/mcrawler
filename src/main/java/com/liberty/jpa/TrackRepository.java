package com.liberty.jpa;

import com.liberty.entity.ArtistEntity;
import com.liberty.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface TrackRepository extends JpaRepository<TrackEntity, Integer>{
}
