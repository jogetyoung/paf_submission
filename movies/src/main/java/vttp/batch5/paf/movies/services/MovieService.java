package vttp.batch5.paf.movies.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vttp.batch5.paf.movies.models.DirectorPNL;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {

  @Autowired
  private MongoMovieRepository mongoMovieRepository;

  @Autowired
  private MySQLMovieRepository mySQLMovieRepository;

  // TODO: Task 2
  

  // TODO: Task 3
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public List<DirectorPNL> getProlificDirectors(int limit) {
    // Step 1: Get Top Directors from MongoDB
    List<Document> mongoDirectors = mongoMovieRepository.getProlificDirectors(limit);

    List<String> directorNames = mongoDirectors.stream()
            .map(doc -> doc.getString("director_name"))
            .collect(Collectors.toList());

    System.out.println("Directors Retrieved from MongoDB: " + directorNames);


    // Step 2: Get IMDB IDs for each director
    Map<String, List<String>> directorToImdbIDs = mongoMovieRepository.getDirectorImdbIDs(directorNames);

    // Step 3: Get Revenue & Budget for those IMDB IDs
    return mySQLMovieRepository.getRevenueAndBudgetByImdbIDs(directorToImdbIDs);
  }


  // TODO: Task 4
  // You may change the signature of this method by passing any number of parameters
  // and returning any type
  public void generatePDFReport() {

  }

}
