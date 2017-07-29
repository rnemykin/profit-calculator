package ru.tn.profitcalculator.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Data
@Entity
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "setting_id_seq")
    @SequenceGenerator(sequenceName = "setting_id_seq", allocationSize = 1, name = "setting_id_seq")
    private Long id;

    private String key;
    private String name;
    private Integer intValue;
}
