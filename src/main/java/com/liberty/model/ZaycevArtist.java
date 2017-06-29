package com.liberty.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User: Dimitr
 * Date: 29.06.2017
 * Time: 8:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "zaycev_artist")
public class ZaycevArtist {
    @Id
    private Long zaycevArtistId;
    private String zaycevArtistName;
}
