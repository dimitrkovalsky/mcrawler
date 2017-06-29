package com.liberty.jpa;

import com.liberty.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface ArtistRepository extends JpaRepository<ArtistEntity, Long>{
}
