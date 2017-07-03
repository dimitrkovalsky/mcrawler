package com.liberty.model;

import de.umass.lastfm.ImageSize;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Dimitr
 * Date: 23.05.2017
 * Time: 10:18
 */
@Data
public class SimilarArtist {
    private String mbid;
    private String name;
    private Float match;
    private Map<ImageSize, String> images = new HashMap<>();
}
