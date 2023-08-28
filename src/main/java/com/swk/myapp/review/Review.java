package com.swk.myapp.review;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long no;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024 * 1024 * 20, nullable = false)
    private String img;

    @Column(nullable = false)
    private String spirit;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private int vol;

    @Column(nullable = false)
    private String aroma;

    @Column(nullable = false)
    private String taste;

    @Column(nullable = false)
    private String finish;

    @Column(nullable = false)
    private long ownerId;



}
