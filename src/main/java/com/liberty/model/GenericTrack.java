package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "generic_track")
public class GenericTrack {
    @Id
    @Indexed
    private String mbid;
    @TextIndexed
    private String name;
    @Indexed
    private String albumMbid;
    @Indexed
    private String artistMbib;

    private List<StreamTrack> streams;

    public GenericTrack(MbTrack track) {
        mbid = track.getMbid();
        name = track.getName();
        albumMbid = track.getAlbumMbid();
        artistMbib = track.getArtistMbib();
    }

    public void addStreams(List<StreamTrack> newStreams) {
        if (streams == null) {
            streams = new ArrayList<>();
        }
        if (!CollectionUtils.isEmpty(newStreams)) {
            streams.addAll(newStreams);
        }
    }
}
