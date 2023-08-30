package com.swk.myapp.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "select * " +
                    "from review " +
                    "where vol >= :vol", nativeQuery = true)
    Page<Review> findReviewByVolUp(int vol, Pageable page);

    @Query(value = "select * " +
                    "from review " +
                    "where vol <= :vol", nativeQuery = true)
    Page<Review> findReviewByVolDown(int vol, Pageable page);

    @Query(value = "select * " +
                    "from review " +
                    "where name like %:name%", nativeQuery = true)
    Page<Review> findReviewByName(String name, Pageable page);

    @Query(value = "select * " +
            "from review " +
            "where spirit like %:spirit%", nativeQuery = true)
    Page<Review> findReviewBySpirit(String spirit, Pageable page);

}
