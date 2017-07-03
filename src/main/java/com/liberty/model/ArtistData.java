package com.liberty.model;

import de.umass.lastfm.ImageSize;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author dkovalskyi
 * @since 22.05.2017
 */
@Data
public class ArtistData {
    private ObjectId id;
    private String mbid;
    private String name;
    private String fullName;
    private String wikiDescpiption;
    private String wikiSummary;
    private Set<String> tags;
    private Map<ImageSize, String> images = new HashMap<>();
    private List<SimilarArtist> similar;
}
