package com.liberty.soundcloud.model;

import com.liberty.model.StreamPlatform;
import com.liberty.model.StreamTrack;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "sc-processed-track")
public class SoundCloudTrack {
    @Id
    private String id;
    private String streamUrl;
    private Long duration;
    private String artistName;
    private String trackName;

    public StreamTrack toStreamTrack() {
        StreamTrack track = new StreamTrack();
        track.setPlatform(StreamPlatform.SOUND_CLOUD);
        track.setTrackName(trackName);
        track.setPlatformTrackId(id);
        track.setStreamLink(streamUrl);
        return track;
    }
}
