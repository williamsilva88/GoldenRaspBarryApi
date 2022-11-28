package com.api.goldenraspbarry.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "`producer_indicated`")
public class ProducerIndicated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="id_producer")
    private Integer producerId;
    @Column(name="id_indicated")
    private Integer indicatedId;

}
