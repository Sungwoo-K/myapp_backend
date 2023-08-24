package com.swk.myapp.recipe;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipe {
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
    private int vol;

    @Column(nullable = false)
    private String ingredients;

    @Column(nullable = false)
    private ArrayList<String> recipe;



}
