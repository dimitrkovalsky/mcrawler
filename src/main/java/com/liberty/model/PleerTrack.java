package com.liberty.model;

import lombok.AllArgsConstructor;
import  lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String singer;
    private String song;
    private String rate;
    private String size;
    private String source;

    public PleerTrack(String song) {
        this.song = song;
    }
}
