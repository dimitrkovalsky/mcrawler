package com.liberty.soundcloud.repo;

import com.liberty.soundcloud.model.SoundCloudTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SoundCloudRepository {

    List<SoundCloudTrack> findByArtistName(String artistName);

    long count();
}
