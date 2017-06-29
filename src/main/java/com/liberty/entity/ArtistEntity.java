package com.liberty.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Table(name = "artist")
@Entity(name = "artist")
@Data
public class ArtistEntity {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "gid")
    private String mbid;
    @Column(name = "name")
    private String name;
    @Column(name = "sort_name")
    private String sort_name;
    @Column(name = "area")
    private Integer area;
}
