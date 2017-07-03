package com.liberty.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * User: Dimitr
 * Date: 02.07.2017
 * Time: 19:10
 */
@Data
public class GenericArtist {
    @Id
    private String mbid;
    private Long zaycevArtistId;
    private String pleerName;
    private ArtistData artistData;
    private String name;
}
