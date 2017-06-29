package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: Dimitr
 * Date: 29.06.2017
 * Time: 8:39
 */
@Data
@Document( collection = "zaycev_track")
public class ZaycevTrack {
    @Id
    private Long zaycevId;
    private String dataUrl;
    private String streamLink;
    private String artistName;
    private String artistId;
    private String trackName;
    private Integer duration;
    private Long zaycevArtistId;
    private String zaycevArtistName;
}
