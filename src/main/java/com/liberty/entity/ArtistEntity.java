package com.liberty.entity;

import com.liberty.model.MbArtist;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Table(name = "artist")
@Entity(name = "artist")
@Data
public class ArtistEntity {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "gid")
    @Type(type = "pg-uuid")
    private UUID mbid;
    @Column(name = "name")
    private String name;

    @OneToMany(targetEntity = TagEntity.class, fetch = FetchType.EAGER)
    @JoinTable(name = "artist_tag", joinColumns = @JoinColumn(name = "artist", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tag", referencedColumnName = "id"))
    private List<TagEntity> tags;

    public MbArtist toMongoEntity() {
        MbArtist mbArtist = new MbArtist();
        mbArtist.setMbid(mbid.toString());
        mbArtist.setInternalId(id);
        mbArtist.setName(name);
        if (tags != null) {
            mbArtist.setTags(tags.stream().map(TagEntity::getName).collect(Collectors.toList()));
        }
        return mbArtist;
    }
}
