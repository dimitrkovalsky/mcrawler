package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

/**
 * User: Dimitr
 * Date: 29.06.2017
 * Time: 8:39
 */
@Data
@Document(collection = "zaycev_track")
public class ZaycevTrack {
    @Id
    private Long zaycevId;
    private String dataUrl;
    private String streamLink;
    @TextIndexed
    private String artistName;
    private String artistId;
    @TextIndexed
    private String trackName;
    private Integer duration;
    private Long zaycevArtistId;
    @TextIndexed
    private String zaycevArtistName;
    @TextScore
    @ReadOnlyProperty
    private Float score;

    public StreamTrack toStreamTrack() {
        StreamTrack track = new StreamTrack();
        track.setPlatform(StreamPlatform.ZAYCEV);
        track.setTrackName(trackName);
        track.setPlatformTrackId(zaycevId.toString());
        track.setStreamLink(streamLink);
        return track;
    }
}
