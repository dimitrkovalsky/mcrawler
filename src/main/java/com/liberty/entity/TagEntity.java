package com.liberty.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by dkovalskyi on 29.06.2017.
 */
@Table(name = "tag")
@Entity(name = "tag")
@Data
public class TagEntity {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "name")
    private String name;
}
