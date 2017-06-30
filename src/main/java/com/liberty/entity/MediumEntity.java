package com.liberty.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Table(name = "medium")
@Entity(name = "medium")
@Data
public class MediumEntity {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "release")
    private Integer release;
    @Column(name = "name")
    private String name;
    @Column(name = "track_count")
    private Integer trackCount;

    @OneToMany(targetEntity = TrackEntity.class, fetch = FetchType.EAGER, mappedBy = "medium")
    private List<TrackEntity> tracks;
}
