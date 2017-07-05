package com.liberty.model;

import com.liberty.entity.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "mb_artist")
public class MbArtist {
    @Id
    @Indexed
    private String mbid;
    @Indexed
    private Integer internalId;
    @TextIndexed
    private String name;
    private ArtistData data;
    private List<String> tags;
}
