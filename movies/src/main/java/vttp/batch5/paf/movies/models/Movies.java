package vttp.batch5.paf.movies.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Movies {

    private String imdb_id;

    private Integer vote_average;

    private Integer vote_count;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date release_date;

    private Integer revenue;

    private Integer budget;

    private Integer runtime;

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public Integer getVote_average() {
        return vote_average;
    }

    public void setVote_average(Integer vote_average) {
        this.vote_average = vote_average;
    }

    public Integer getVote_count() {
        return vote_count;
    }

    public void setVote_count(Integer vote_count) {
        this.vote_count = vote_count;
    }

    public Date getRelease_date() {
        return release_date;
    }

    public void setRelease_date(Date release_date) {
        this.release_date = release_date;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Movies(String imdb_id, Integer vote_average, Integer vote_count, Date release_date, Integer revenue, Integer budget, Integer runtime) {
        this.imdb_id = imdb_id;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.release_date = release_date;
        this.revenue = revenue;
        this.budget = budget;
        this.runtime = runtime;
    }

    public Movies() {
    }
}
