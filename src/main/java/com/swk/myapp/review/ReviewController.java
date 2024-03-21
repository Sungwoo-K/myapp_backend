package com.swk.myapp.review;

import com.swk.myapp.auth.Auth;
import com.swk.myapp.auth.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "리뷰 관리 API")
@RestController
@RequestMapping(value = "/reviews")
public class ReviewController {

    @Autowired
    ReviewRepository repo;

    @Operation(summary = "리뷰 단일 조회")
    @GetMapping
    public ResponseEntity<Review> getReview(@RequestParam long no) {
        Optional<Review> findReview = repo.findById(no);

        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review review = findReview.get();

        return ResponseEntity.ok(review);
    }

    @Operation(summary = "수정할 리뷰 조회 및 권한 체크")
    @Auth
    @GetMapping(value = "/edit")
    public ResponseEntity<Review> getReviewForEdit(@RequestParam long no,@RequestAttribute("authUser") AuthUser authUser ){
        Optional<Review> findReview = repo.findById(no);

        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review review = findReview.get();

        if(authUser.getId() != review.getOwnerId()){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        return ResponseEntity.ok(review);
    }

    @Operation(summary = "리뷰 목록 페이징 조회")
    @GetMapping(value = "/paging")
    public Page<Review> getReviewsPaging(@RequestParam int page, @RequestParam int size) {

        PageRequest pageRequest =PageRequest.of(page, size);

        return repo.findAll(pageRequest);
    };

    @Operation(summary = "리뷰 목록 조건 페이징 조회")
    @GetMapping(value = "/paging/search")
    public Page<Review> getSearchReviewsPaging(@RequestParam Map<String,String> allParams) {
        int page = Integer.parseInt(allParams.get("page"));
        int size = Integer.parseInt(allParams.get("size"));

        PageRequest pageRequest = PageRequest.of(page, size);

        if(allParams.containsKey("volup")) {
          int volUp = Integer.parseInt(allParams.get("volup"));
          return repo.findReviewByVolUp(volUp, pageRequest);
        }

        if(allParams.containsKey("voldown")) {
            int volDown = Integer.parseInt(allParams.get("voldown"));
            return repo.findReviewByVolDown(volDown, pageRequest);
        }

        if(allParams.containsKey("name")) {
            return repo.findReviewByName(allParams.get("name"),pageRequest);
        }

        if(allParams.containsKey("spirit")) {
            return repo.findReviewBySpirit(allParams.get("spirit"), pageRequest);
        }

        return null;
    }

    @Operation(summary = "리뷰 추가")
    @Auth
    @PostMapping
    public ResponseEntity<Map<String,String>> addReview(@RequestBody Review review, @RequestAttribute("authUser") AuthUser authUser) {
        if(review.getName() == null || review.getImg() == null || review.getSpirit() == null ||
                review.getAroma() == null || review.getTaste() == null || review.getFinish() == null ||
                review.getScore() == 0 || review.getVol() == 0 ||
                review.getName().isEmpty() || review.getImg().isEmpty() || review.getSpirit().isEmpty() ||
                review.getAroma().isEmpty() || review.getTaste().isEmpty() || review.getFinish().isEmpty()){
            Map<String, String> res = new HashMap<>();
            res.put("message", "입력된 정보가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        review.setOwnerId(authUser.getId());
        Review savedReview = repo.save(review);

        if(savedReview != null) {
            Map<String, String> res = new HashMap<>();
            res.put("message", "소중한 리뷰 감사합니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "리뷰 권한 체크 및 삭제")
    @Auth
    @DeleteMapping(value = "/{no}")
    public ResponseEntity removeReview(@PathVariable long no,@RequestAttribute("authUser") AuthUser authUser) {
        Optional<Review> findReview = repo.findById(no);
        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review toRemoveReview = findReview.get();

        if(authUser.getId() != toRemoveReview.getOwnerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        repo.deleteById(no);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "리뷰 권한 체크 및 수정")
    @Auth
    @PutMapping
    public ResponseEntity<Map<String,String>> modifyReview(@RequestParam long no, @RequestBody ReviewModifyRequest review, @RequestAttribute("authUser") AuthUser authUser) {

        Optional<Review> findReview = repo.findById(no);

        if(!findReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Review toModifyReview = findReview.get();

        if(authUser.getId() != toModifyReview.getOwnerId()) {
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
