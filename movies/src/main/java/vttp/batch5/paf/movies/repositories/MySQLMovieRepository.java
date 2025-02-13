package vttp.batch5.paf.movies.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vttp.batch5.paf.movies.Utils.SQL;
import vttp.batch5.paf.movies.models.DirectorPNL;
import vttp.batch5.paf.movies.models.Movies;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MySQLMovieRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int BATCH_SIZE = 25;

    private static final Date MIN_DATE = Date.valueOf("2018-01-01");

    // TODO: Task 2.3
    // You can add any number of parameters and return any type from the method
    public void batchInsertMovies(List<Movies> movies) {

        if (movies.isEmpty()) {
            System.out.println("No movies inserted");
            return;
        }

        List<Movies> filteredMovies = movies.stream()
                .filter(movie -> movie.getRelease_date().compareTo(MIN_DATE) >= 0)
                .collect(Collectors.toList());


        if (filteredMovies.isEmpty()) {
            //System.out.println("No movies met the filtering criteria (release_date >= 2018-01-01).");
            return;
        }

        int count = 0;

        for (int i = 0; i < filteredMovies.size(); i += BATCH_SIZE) {

            List<Movies> batch = filteredMovies.subList(i, Math.min(i + BATCH_SIZE, filteredMovies.size()));

            for (Movies movie : batch) {
                jdbcTemplate.update(SQL.INSERT_MOVIES_SQL,
                        movie.getImdb_id(), movie.getVote_average(), movie.getVote_count(),
                        movie.getRelease_date(), movie.getRevenue(), movie.getBudget(), movie.getRuntime());
            }
            count += batch.size();
            //System.out.println("Inserted " + count + " movies");

        }

        //System.out.println("Batch insert complete! Total inserted " + count);

    }

    // TODO: Task 3

    public List<DirectorPNL> getRevenueAndBudgetByImdbIDs(Map<String, List<String>> directorToImdbIDs) {

        // Flatten the IMDB ID list
        List<String> imdbIDs = directorToImdbIDs.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        if (imdbIDs.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",", Collections.nCopies(imdbIDs.size(), "?"));
        String sql = String.format("""
        SELECT imdb_id, 
               COALESCE(SUM(revenue), 0) AS total_revenue, 
               COALESCE(SUM(budget), 0) AS total_budget
        FROM imdb
        WHERE imdb_id IN (%s)
        GROUP BY imdb_id
    """, placeholders);

        // Execute SQL query
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, imdbIDs.toArray());

        // Convert results to a Map { "imdb_id": { "revenue": X, "budget": Y } }
        Map<String, Map<String, Double>> revenueBudgetMap = results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("imdb_id"),
                        row -> Map.of(
                                "total_revenue", ((BigDecimal) row.get("total_revenue")).doubleValue(),  // Convert BigDecimal to double
                                "total_budget", ((BigDecimal) row.get("total_budget")).doubleValue()  // Convert BigDecimal to double
                        )
                ));

        // Now, map revenue/budget back to directors
        List<DirectorPNL> directorList = new ArrayList<>();
        for (String director : directorToImdbIDs.keySet()) {
            List<String> imdbList = directorToImdbIDs.get(director);

            double totalRevenue = imdbList.stream()
                    .mapToDouble(id -> revenueBudgetMap.getOrDefault(id, Map.of("total_revenue", 0.0)).get("total_revenue"))
                    .sum();

            double totalBudget = imdbList.stream()
                    .mapToDouble(id -> revenueBudgetMap.getOrDefault(id, Map.of("total_budget", 0.0)).get("total_budget"))
                    .sum();

            if (totalRevenue > 0 || totalBudget > 0) {
                directorList.add(new DirectorPNL(director, imdbList.size(), totalRevenue, totalBudget));
            }
        }

        return directorList;
    }

}
