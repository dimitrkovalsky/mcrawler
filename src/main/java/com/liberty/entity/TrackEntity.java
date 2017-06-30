package com.liberty.entity;

import com.liberty.model.MbTrack;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Table(name = "track")
@Entity(name = "track")
@Data
public class TrackEntity implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "gid")
    @Type(type = "pg-uuid")
    private UUID mbid;
    @Column(name = "medium")
    private Integer medium;
    @Column(name = "name")
    private String name;
    @Column(name = "artist_credit")
    private Integer artistId;


    public MbTrack toMbTrack() {
        MbTrack track = new MbTrack();
        track.setInternalId(id);
        track.setMbid(mbid.toString());
        track.setName(name);
        track.setArtistInternalId(artistId);
        return track;
    }
}
