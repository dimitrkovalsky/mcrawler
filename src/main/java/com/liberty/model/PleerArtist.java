package com.liberty.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @since 18.05.2017
 */
@Data
@Document(collection = "artist")
@NoArgsConstructor
public class PleerArtist {
    @Id
    private ObjectId id;
    private String pleerArtistName;
    private ArtistData artistData;

    public String getStringId() {
        return id.toString();
    }

    public PleerArtist(ObjectId id, String pleerArtistName) {
        this.id = id;
        this.pleerArtistName = pleerArtistName;
    }
}