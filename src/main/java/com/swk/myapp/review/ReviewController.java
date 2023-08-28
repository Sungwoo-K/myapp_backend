package com.swk.myapp.review;

import com.swk.myapp.auth.Auth;
import com.swk.myapp.auth.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/reviews")
public class ReviewController {

    @Autowired
    ReviewRepository repo;

    @GetMapping
    public ResponseEntity<Review> getReview(@RequestParam long no) {
        Optional<Review> findReview = repo.findById(no);

        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review review = findReview.get();

        return ResponseEntity.ok(review);
    }

    @GetMapping(value = "/paging")
    public Page<Review> getReviewsPaging(@RequestParam int page, @RequestParam int size) {

        PageRequest pageRequest =PageRequest.of(page, size);

        return repo.findAll(pageRequest);
    };

    @Auth
    @PostMapping
    public ResponseEntity<Map<String,String>> addReview(@RequestBody Review review) {

        if(review.getName() == null || review.getImg() == null || review.getSpirit() == null ||
                review.getAroma() == null || review.getTaste() == null || review.getFinish() == null ||
                review.getScore() == 0 || review.getVol() == 0 ||
                review.getName().isEmpty() || review.getImg().isEmpty() || review.getSpirit().isEmpty() ||
                review.getAroma().isEmpty() || review.getTaste().isEmpty() || review.getFinish().isEmpty()){
            Map<String, String> res = new HashMap<>();
            res.put("message", "입력된 정보가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        Review savedReview = repo.save(review);

        if(savedReview != null) {
            Map<String, String> res = new HashMap<>();
            res.put("message", "소중한 리뷰 감사합니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.ok().build();
    }

    @Auth
    @DeleteMapping(value = "/{no}")
    public ResponseEntity removeReview(@PathVariable long no,@RequestAttribute AuthUser user) {
        Optional<Review> findReview = repo.findById(no);
        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review toRemoveReview = findReview.get();

        if(user.getId() != toRemoveReview.getOwnerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        repo.deleteById(no);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Auth
    @PutMapping
    public ResponseEntity<Map<String,String>> modifyReview(@RequestParam long no, @RequestBody ReviewModifyRequest review, @RequestAttribute AuthUser user) {

        Optional<Review> findReview = repo.findById(no);

        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review toModifyReview = findReview.get();

        if(user.getId() != toModifyReview.getOwnerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(review.getName() != null && !review.getName().isEmpty()) {
            toModifyReview.setName(review.getName());
        }

        if(review.getImg() != null && !review.getImg().isEmpty()) {
            toModifyReview.setImg(review.getImg());
        }

        if(review.getSpirit() != null && !review.getSpirit().isEmpty()) {
            toModifyReview.setSpirit(review.getSpirit());
        }

        if(0 < review.getScore() && review.getScore() <= 5) {
            toModifyReview.setScore(review.getScore());
        }

        if(0 < review.getVol() && review.getVol() <= 100) {
            toModifyReview.setVol(review.getVol());
        }

        if(review.getAroma() != null && !review.getAroma().isEmpty()) {
            toModifyReview.setAroma(review.getAroma());
        }

        if(review.getTaste() != null && !review.getTaste().isEmpty()) {
            toModifyReview.setTaste(review.getTaste());
        }

        if(review.getFinish() != null && !review.getFinish().isEmpty()) {
            toModifyReview.setFinish(review.getFinish());
        }


        repo.save(toModifyReview);

        Map<String, String> res = new HashMap<>();
        res.put("message", "정상적으로 수정되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
