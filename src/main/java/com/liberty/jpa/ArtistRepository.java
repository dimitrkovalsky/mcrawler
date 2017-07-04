package com.liberty.jpa;

import com.liberty.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, Integer>{
}
