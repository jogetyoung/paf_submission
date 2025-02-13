package vttp.batch5.paf.movies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vttp.batch5.paf.movies.models.DirectorPNL;
import vttp.batch5.paf.movies.services.MovieService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MainController {

    @Autowired
    private MovieService movieService;

  // TODO: Task 3

    @GetMapping("/summary")
    public ResponseEntity<?> getTopDirectors(@RequestParam int count) {
        List<DirectorPNL> directors = movieService.getProlificDirectors(count);

        if (directors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(directors);
    }

   

  
  // TODO: Task 4


}
