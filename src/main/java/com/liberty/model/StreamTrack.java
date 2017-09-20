package com.liberty.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Data
@EqualsAndHashCode
public class StreamTrack {
    private StreamPlatform platform;
    private String streamLink;
    private String trackName;
    private String platformTrackId;
}
