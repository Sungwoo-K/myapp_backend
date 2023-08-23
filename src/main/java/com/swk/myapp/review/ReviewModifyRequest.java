package com.swk.myapp.review;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewModifyRequest {

    private String name;

    private String img;

    private String spirit;

    private int score;

    private int vol;

    private String aroma;

    private String taste;

    private String finish;
}
