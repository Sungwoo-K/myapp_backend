package com.swk.myapp.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query(value = "select * " +
            "from recipe " +
            "where vol >= :vol", nativeQuery = true)
    Page<Recipe> findRecipeByVolUp(int vol, Pageable page);

    @Query(value = "select * " +
                    "from recipe " +
                    "where vol <= :vol", nativeQuery = true)
    Page<Recipe> findRecipeByVolDown(int vol, Pageable page);

    @Query(value = "select * " +
            "from recipe " +
            "where name like %:name%", nativeQuery = true)
    Page<Recipe> findRecipeByName(String name, Pageable page);

    @Query(value = "select * " +
            "from recipe " +
            "where spirit like %:spirit%", nativeQuery = true)
    Page<Recipe> findRecipeBySpirit(String spirit, Pageable page);
}
