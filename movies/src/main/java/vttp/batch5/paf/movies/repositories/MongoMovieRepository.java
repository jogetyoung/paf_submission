package vttp.batch5.paf.movies.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MongoMovieRepository {

    @Autowired
    private MongoTemplate mongoTemplate;


    // TODO: Task 2.3
/*db.imdb.insertMany(
        db.movies.aggregate([    (i manually imported the data into mongodb collection named movies first
 {
  $match: {  // Filter movies with release_date >= 2018
   release_date: { $gte: "2018-01-01" }
  }
 },

 {
  $project: {  // Select only required fields
   _id: "$imdb_id",  // Use imdb_id as the primary key
           imdb_id: "$imdb_id",
           tagline: "$tagline",
           title: "$title",
           genres: "$genres",
           directors: "$directors",
           imdb_rating: "$imdb_rating",
           overview: "$overview",
           imdb_votes: "$imdb_votes"
  }
 },

 {
  $group: {  // Remove duplicates by grouping on _id (imdb_id)
   _id: "$_id",
           imdb_id: { $first: "$imdb_id" },
   tagline: { $first: "$tagline" },
   title: { $first: "$title" },
   genres: { $first: "$genres" },
   directors: { $first: "$directors" },
   imdb_rating: { $first: "$imdb_rating" },
   overview: { $first: "$overview" },
   imdb_votes: { $first: "$imdb_votes" }
  }
 }
  ]).toArray() // Convert aggregation output into an array before inserting
);
 */
    public List<Document> batchInsertMovies() throws ParseException {

        Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01");

        MatchOperation matchOperation = Aggregation.match(Criteria.where("release_date").gte(fromDate));

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("imdb_id").as("_id")  // Use imdb_id as the primary key
                .and("imdb_id").as("imdb_id")
                .and("tagline").as("tagline")
                .and("title").as("title")
                .and("genres").as("genres")
                .and("directors").as("directors")
                .and("imdb_rating").as("imdb_rating")
                .and("overview").as("overview")
                .and("imdb_votes").as("imdb_votes");

        GroupOperation groupOperation = Aggregation.group("_id")
                .first("imdb_id").as("imdb_id")
                .first("tagline").as("tagline")
                .first("title").as("title")
                .first("genres").as("genres")
                .first("directors").as("directors")
                .first("imdb_rating").as("imdb_rating")
                .first("overview").as("overview")
                .first("imdb_votes").as("imdb_votes");

        Aggregation pipeline = Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation);

        List<Document> output = mongoTemplate.aggregate(pipeline, "movies", Document.class).getMappedResults();

        if (!output.isEmpty()) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "imdb");
            int batchSize = 25;
            int count = 0;

            for (Document doc : output) {
                Query query = new Query(Criteria.where("_id").is(doc.get("_id")));
                Update update = new Update()
                        .set("imdb_id", doc.get("imdb_id"))
                        .set("tagline", doc.get("tagline"))
                        .set("title", doc.get("title"))
                        .set("genres", doc.get("genres"))
                        .set("directors", doc.get("directors"))
                        .set("imdb_rating", doc.get("imdb_rating"))
                        .set("overview", doc.get("overview"))
                        .set("imdb_votes", doc.get("imdb_votes"));

                bulkOps.upsert(query, update);
                count++;

                if (count % batchSize == 0) {
                    bulkOps.execute();
                    bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, "imdb");
                }

            }
            if (count % batchSize != 0) {
                bulkOps.execute();
            }

            System.out.println("Inserted/Updated " + output.size() + " movies into imdb in batches of 25.");
        } else {
            System.out.println("No movies met the filtering criteria (release_date >= 2018-01-01).");
        }


        return output;
    }

    // TODO: Task 2.4
    // You can add any number of parameters and return any type from the method
    // You can throw any checked exceptions from the method
    // Write the native Mongo query you implement in the method in the comments
    //
    //    native MongoDB query here
    //
    public void logError () {

    }

    // TODO: Task 3
    // Write the native Mongo query you implement in the method in the comments
    //
    //    native MongoDB query here

   /* db.imdb.aggregate([
    {
        "$group": {
        "_id": "$director",
                "movies_count": { "$sum": 1 }
    }
    },
    {
        "$sort": { "movies_count": -1 }
    },
    {
        "$limit": 10  // Replace 10 with 'limit' parameter
    },
    {
        "$project": {
        "_id": 0,
                "director_name": "$_id",
                "movies_count": 1
    }
    }
]) */
    //
    public List<Document> getProlificDirectors(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("director"),
                Aggregation.group("director").count().as("movies_count"),
                Aggregation.sort(Sort.Direction.DESC, "movies_count"),
                Aggregation.limit(limit),
                Aggregation.project()
                        .and("_id").as("director_name")
                        .and("movies_count").as("movies_count")
        );

        return mongoTemplate.aggregate(aggregation, "imdb", Document.class).getMappedResults();
    }

    public Map<String, List<String>> getDirectorImdbIDs(List<String> directors) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.unwind("director"),
                Aggregation.match(Criteria.where("director").in(directors)),
                Aggregation.project()
                        .and("director").as("director_name")
                        .and("imdb_id").as("imdb_id"),
                Aggregation.group("director_name")
                        .push("imdb_id").as("imdb_ids"),
                Aggregation.match(Criteria.where("_id").ne(""))

        );

        List<Document> results = mongoTemplate.aggregate(aggregation, "imdb", Document.class).getMappedResults();

        // Debugging: Print raw MongoDB results
        System.out.println("MongoDB Raw Results for IMDb IDs: " + results);

        // Convert List<Document> to Map<String, List<String>> (director_name -> imdb_ids)
        Map<String, List<String>> directorToImdbIDs = results.stream()
                .collect(Collectors.toMap(
                        doc -> doc.getString("_id"),
                        doc -> doc.getList("imdb_ids", String.class)
                ));

        // Debugging: Print final IMDb ID mapping
        System.out.println("Final Director to IMDb IDs Map: " + directorToImdbIDs);

        return directorToImdbIDs;
    }



}
