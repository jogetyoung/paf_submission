package vttp.batch5.paf.movies.Utils;

public class SQL {


    public static final String INSERT_MOVIES_SQL =
            "INSERT INTO imdb (imdb_id, vote_average, vote_count, release_date, revenue, budget, runtime) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "vote_average = VALUES(vote_average), vote_count = VALUES(vote_count), " +
                    "release_date = VALUES(release_date), revenue = VALUES(revenue), " +
                    "budget = VALUES(budget), runtime = VALUES(runtime)";

    public static final String REV_AND_BUDGET = """
            SELECT imdb_id, 
                   COALESCE(SUM(revenue), 0) AS total_revenue, 
                   COALESCE(SUM(budget), 0) AS total_budget
            FROM imdb
            WHERE imdb_id IN (%s)
             AND (revenue > 0 OR budget > 0)
            GROUP BY imdb_id
        """;
}

