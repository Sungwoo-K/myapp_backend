package com.swk.myapp.recipe;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class RecipeModifyRequest {

    private String name;

    private String img;

    private String spirit;

    private int vol;

    private String ingredients;

    private ArrayList<String> recipe;
}
