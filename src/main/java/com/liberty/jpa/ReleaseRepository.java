package com.liberty.jpa;

import com.liberty.entity.ReleaseEntity;
import com.liberty.model.MbArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface ReleaseRepository extends JpaRepository<ReleaseEntity, Integer> {

    List<ReleaseEntity> findAllByArtistId(int artistId);
}
