package com.liberty.soundcloud.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "processedTrack")
public class SoundCloudArtistTracks {
    @Id
    private String artistName;

    private List<SoundCloudTrack> tracks;
}
