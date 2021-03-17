package toeicLab.toeicLab.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Word {

    @Id @GeneratedValue
    private Long id;

    private String word;

    private String meaning;

//////////////////////////////////////////////////
//    private String example;
//
//    private String translationExample;
///////////////////////////////////////////////////
}
