package com.liberty.model;

import lombok.Data;

/**
 * Created by dkovalskyi on 03.07.2017.
 */
@Data
public class StreamTrack {
    private StreamPlatform platform;
    private String streamLink;
    private String trackName;
    private String platformTrackId;
}
