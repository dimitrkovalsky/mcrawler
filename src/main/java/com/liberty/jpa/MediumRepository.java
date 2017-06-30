package com.liberty.jpa;

import com.liberty.entity.MediumEntity;
import com.liberty.entity.ReleaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
public interface MediumRepository extends JpaRepository<MediumEntity, Integer> {
    List<MediumEntity> findAllByRelease(Integer release);
}
