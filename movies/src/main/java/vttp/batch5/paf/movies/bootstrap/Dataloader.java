package vttp.batch5.paf.movies.bootstrap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vttp.batch5.paf.movies.models.Movies;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class Dataloader implements CommandLineRunner {

    private final ObjectMapper objectMapper;

    @Value("${data.zip.filepath}")
    private String zipFilePath;

    @Autowired
    private MySQLMovieRepository mySQLMovieRepository;

    public Dataloader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    //TODO: Task 2


    @Override
    public void run(String... args) {
        System.out.println("Reading ZIP file....");

        ObjectMapper mapper = new ObjectMapper();

        List<Movies> moviesList = new ArrayList<>();

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (!entry.isDirectory() && entry.getName().endsWith(".json")) {

                    System.out.println("Reading JSON file...." + entry.getName());

                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            JsonNode jsonNode = mapper.readTree(line);
                            //System.out.println("Movie: " + jsonNode.get("title").asText() + " (IMDB ID: " + jsonNode.get("imdb_id").asText() + ")");

                            Movies movies = new Movies(
                                    jsonNode.get("imdb_id").asText(),
                                    jsonNode.get("vote_average").asInt(),
                                    jsonNode.get("vote_count").asInt(),
                                    Date.valueOf(jsonNode.get("release_date").asText()), // Convert String to SQL Date
                                    jsonNode.get("revenue").asInt(),
                                    jsonNode.get("budget").asInt(),
                                    jsonNode.get("runtime").asInt()
                            );
                            moviesList.add(movies);

                            if (moviesList.size() == 25) {
                                mySQLMovieRepository.batchInsertMovies(moviesList);
                                moviesList.clear();
                            }

                        }

                    }
                }
            }

            if (!moviesList.isEmpty()) {
                mySQLMovieRepository.batchInsertMovies(moviesList);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}





