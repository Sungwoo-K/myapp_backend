package com.swk.myapp.recipe;

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
@RequestMapping(value = "/recipes")
public class RecipeController {

    @Autowired
    RecipeRepository repo;

    @GetMapping
    public ResponseEntity<Recipe> getRecipe(@RequestParam long no) {
        Optional<Recipe> findRecipe = repo.findById(no);

        if(!findRecipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe recipe = findRecipe.get();

        return ResponseEntity.ok(recipe);
    }

    @GetMapping(value = "/paging")
    public Page<Recipe> getRecipesPaging(@RequestParam int page, @RequestParam int size) {

        PageRequest pageRequest =  PageRequest.of(page, size);

        return repo.findAll(pageRequest);
    }

    @GetMapping(value = "/paging/search")
    public Page<Recipe> getSearchRecipePaging(@RequestParam Map<String,String> allParams){
        int page = Integer.parseInt(allParams.get("page"));
        int size = Integer.parseInt(allParams.get("size"));

        PageRequest pageRequest = PageRequest.of(page, size);

        if(allParams.containsKey("volup")) {
            int volUp = Integer.parseInt(allParams.get("volup"));
            return repo.findRecipeByVolUp(volUp, pageRequest);
        }

        if(allParams.containsKey("voldown")) {
            int volDown = Integer.parseInt(allParams.get("voldown"));
            return repo.findRecipeByVolDown(volDown, pageRequest);
        }

        if(allParams.containsKey("name")) {
            return repo.findRecipeByName(allParams.get("name"),pageRequest);
        }

        if(allParams.containsKey("spirit")) {
            return repo.findRecipeBySpirit(allParams.get("spirit"), pageRequest);
        }

        return null;
    }

    @Auth
    @PostMapping
    public ResponseEntity<Map<String,String>> addRecipe(@RequestBody Recipe recipe, @RequestAttribute("authUser") AuthUser authUser) {
        if(recipe.getName() == null || recipe.getImg() == null || recipe.getSpirit() == null ||
                recipe.getIngredients() == null || recipe.getRecipe() == null || recipe.getVol() == 0 ||
                recipe.getName().isEmpty() || recipe.getImg().isEmpty() || recipe.getSpirit().isEmpty() ||
                recipe.getIngredients().isEmpty() || recipe.getRecipe().isEmpty()){
            Map<String, String> res = new HashMap<>();
            res.put("message", "입력된 정보가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        recipe.setOwnerId(authUser.getId());
        Recipe savedRecipe = repo.save(recipe);

        if(savedRecipe != null) {
            Map<String, String> res = new HashMap<>();
            res.put("message", "소중한 레시피 감사합니다.");

            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        }
        return ResponseEntity.ok().build();
    }

    @Auth
    @DeleteMapping(value = "/{no}")
    public ResponseEntity removeRecipe(@PathVariable long no, @RequestAttribute("authUser") AuthUser authUser) {
        Optional<Recipe> findRecipe = repo.findById(no);
        if(!findRecipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe toRemoveRecipe = findRecipe.get();

        if(authUser.getId() != toRemoveRecipe.getOwnerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        repo.deleteById(no);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Auth
    @PutMapping
    public ResponseEntity<Map<String, String>> modifyRecipe(@RequestParam long no,@RequestBody RecipeModifyRequest recipe, @RequestAttribute("authUser") AuthUser authUser){
        Optional<Recipe> findRecipe = repo.findById(no);
        if(!findRecipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Recipe toModifyRecipe = findRecipe.get();

        if(authUser.getId() != toModifyRecipe.getOwnerId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if(recipe.getName() != null && !recipe.getName().isEmpty()) {
            toModifyRecipe.setName(recipe.getName());
        }

        if(recipe.getImg() != null && !recipe.getImg().isEmpty()) {
            toModifyRecipe.setImg(recipe.getImg());
        }

        if(recipe.getSpirit() != null && !recipe.getSpirit().isEmpty()) {
            toModifyRecipe.setSpirit(recipe.getSpirit());
        }

        if(0 < recipe.getVol() && recipe.getVol() <= 100) {
            toModifyRecipe.setVol(recipe.getVol());
        }

        if(recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            toModifyRecipe.setIngredients(recipe.getIngredients());
        }

        if(recipe.getRecipe() != null && !recipe.getRecipe().isEmpty()) {
            toModifyRecipe.setRecipe(recipe.getRecipe());
        }

        repo.save(toModifyRecipe);

        Map<String, String> res = new HashMap<>();
        res.put("message", "정상적으로 수정되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}
