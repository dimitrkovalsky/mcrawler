package com.liberty.service;

import com.liberty.entity.ArtistEntity;
import com.liberty.jpa.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Component
public class DataConverter {
    @Autowired
    private ArtistRepository artistRepository;

    public void runConverter() {
        Page<ArtistEntity> all = artistRepository.findAll(new PageRequest(0, 20));
        System.out.println(all);
    }
}
