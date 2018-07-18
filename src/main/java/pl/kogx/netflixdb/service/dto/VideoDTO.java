package pl.kogx.netflixdb.service.dto;

import pl.kogx.netflixdb.domain.Video;

/**
 * A DTO representing a video, with his authorities.
 */
public class VideoDTO {

    private Long id;

    private String title;

    private Integer releaseYear;

    private String genre;

    private Long genreId;

    private Boolean original;

    private String type;

    private Boolean omdbAvailable;

    private String imdbRating;

    private String imdbVotes;

    private Integer metascore;

    private String tomatoRating;

    private String tomatoUserRating;

    private String imdbID;

    public VideoDTO() {

    }

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.releaseYear = video.getReleaseYear();
        this.genre = video.getGenre();
        this.genreId = video.getGenreId();
        this.original = video.getOriginal();
        this.type = video.getType();
        this.omdbAvailable = video.getOmdbAvailable();
        this.imdbRating = video.getImdbRating();
        this.imdbVotes = video.getImdbVotes();
        this.metascore = video.getMetascore();
        this.tomatoRating = video.getTomatoRating();
        this.tomatoUserRating = video.getTomatoUserRating();
        this.imdbID = video.getImdbID();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Boolean getOriginal() {
        return original;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getOmdbAvailable() {
        return omdbAvailable;
    }

    public void setOmdbAvailable(Boolean omdbAvailable) {
        this.omdbAvailable = omdbAvailable;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public Integer getMetascore() {
        return metascore;
    }

    public void setMetascore(Integer metascore) {
        this.metascore = metascore;
    }

    public String getTomatoRating() {
        return tomatoRating;
    }

    public void setTomatoRating(String tomatoRating) {
        this.tomatoRating = tomatoRating;
    }

    public String getTomatoUserRating() {
        return tomatoUserRating;
    }

    public void setTomatoUserRating(String tomatoUserRating) {
        this.tomatoUserRating = tomatoUserRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    @Override
    public String toString() {
        return "VideoDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", releaseYear=" + releaseYear +
            ", genre='" + genre + '\'' +
            ", genreId=" + genreId +
            ", original=" + original +
            ", type='" + type + '\'' +
            ", omdbAvailable=" + omdbAvailable +
            ", imdbRating='" + imdbRating + '\'' +
            ", imdbVotes='" + imdbVotes + '\'' +
            ", metascore=" + metascore +
            ", tomatoRating='" + tomatoRating + '\'' +
            ", tomatoUserRating='" + tomatoUserRating + '\'' +
            ", imdbID='" + imdbID + '\'' +
            '}';
    }
}