package com.liberty.model;

import de.umass.lastfm.ImageSize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mb_album")
public class MbAlbum {
    @Id
    private String mbid;
    @Indexed
    private Integer internalId;
    @TextIndexed
    private String name;
    @Indexed
    private Integer artistInternalId;
    @Indexed
    private String artistMbib;
    private List<String> tags;
    private List<AlbumTrack> tracks;

    private Map<ImageSize, String> images = new HashMap<>();
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlbumTrack {
        private String mbid;
        private String name;
    }

}
