package com.liberty.soundcloud.repo;

import com.liberty.soundcloud.model.SoundCloudArtistTracks;
import com.liberty.soundcloud.model.SoundCloudTrack;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SoundCloudArtistTrackRepository extends MongoRepository<SoundCloudArtistTracks, String> {
}
