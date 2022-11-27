package com.api.goldenraspbarry.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Indicated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_indicated")
    private Integer id;
    @Column(name="_year")
    private Integer year;
    private String title;
    private String studios;
    private String producers;
    private Boolean winner;
    @ManyToMany
    @JoinTable(
            name = "producer_indicated",
            joinColumns =  @JoinColumn(name = "id_indicated"),
            inverseJoinColumns = @JoinColumn(name = "id_producer")
    )
    private List<Producer> producer;

}
