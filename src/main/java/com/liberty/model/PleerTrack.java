package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

/**
 * @author dkovalskyi
 * @since 18.05.2017
 */
@Data
@Document(collection = "song")
@NoArgsConstructor
@AllArgsConstructor
public class PleerTrack {
    @Id
    private String id;
    private Integer duration;
    private String fileId;
    @TextIndexed
    private String singer;
    @TextIndexed
    private String song;
    private String rate;
    private String size;
    private String source;
    @TextScore
    private Float score;

    public PleerTrack(String song) {
        this.song = song;
    }

    public StreamTrack toStreamTrack() {
        StreamTrack track = new StreamTrack();
        track.setPlatform(StreamPlatform.PLEER);
        track.setPlatformTrackId(fileId);
        track.setTrackName(song);
        track.setStreamLink(null);
        return track;
    }
}
