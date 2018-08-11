package pl.kogx.netflixdb.domain;

import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "videos")
public class Video implements Serializable {

    @Id
    @NotNull
    private Long id;

    private String title;

    private String fwebTitle;

    private Integer releaseYear;

    private String genre;

    private Long genreId;

    private Boolean original;

    private String type;

    private Boolean omdbAvailable;

    private Boolean fwebAvailable;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float imdbRating;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float fwebRating;

    private Long imdbVotes;

    private Long fwebVotes;

    private Integer metascore;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float tomatoRating;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float tomatoUserRating;

    private String imdbID;

    private Long fwebID;

    private String boxart;

    private Date timestamp;

    private String fwebPlot;

    public Video() {
    }

    public Video(@NotNull Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getFwebAvailable() {
        return fwebAvailable;
    }

    public void setFwebAvailable(Boolean fwebAvailable) {
        this.fwebAvailable = fwebAvailable;
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

    public Boolean getOmdbAvailable() {
        return omdbAvailable;
    }

    public void setOmdbAvailable(Boolean omdbAvailable) {
        this.omdbAvailable = omdbAvailable;
    }

    public Float getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Long getImdbVotes() {
        return imdbVotes;
    }

    public void setImdbVotes(Long imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public Integer getMetascore() {
        return metascore;
    }

    public void setMetascore(Integer metascore) {
        this.metascore = metascore;
    }

    public Float getTomatoRating() {
        return tomatoRating;
    }

    public void setTomatoRating(Float tomatoRating) {
        this.tomatoRating = tomatoRating;
    }

    public Float getTomatoUserRating() {
        return tomatoUserRating;
    }

    public void setTomatoUserRating(Float tomatoUserRating) {
        this.tomatoUserRating = tomatoUserRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public Float getFwebRating() {
        return fwebRating;
    }

    public void setFwebRating(Float fwebRating) {
        this.fwebRating = fwebRating;
    }

    public Long getFwebVotes() {
        return fwebVotes;
    }

    public void setFwebVotes(Long fwebVotes) {
        this.fwebVotes = fwebVotes;
    }

    public Long getFwebID() {
        return fwebID;
    }

    public void setFwebID(Long fwebID) {
        this.fwebID = fwebID;
    }

    public String getFwebTitle() {
        return fwebTitle;
    }

    public void setFwebTitle(String fwebTitle) {
        this.fwebTitle = fwebTitle;
    }

    public String getBoxart() {
        return boxart;
    }

    public void setBoxart(String boxart) {
        this.boxart = boxart;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFwebPlot() {
        return fwebPlot;
    }

    public void setFwebPlot(String fwebPlot) {
        this.fwebPlot = fwebPlot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id) &&
            Objects.equals(title, video.title) &&
            Objects.equals(fwebTitle, video.fwebTitle) &&
            Objects.equals(releaseYear, video.releaseYear) &&
            Objects.equals(genre, video.genre) &&
            Objects.equals(genreId, video.genreId) &&
            Objects.equals(original, video.original) &&
            Objects.equals(type, video.type) &&
            Objects.equals(omdbAvailable, video.omdbAvailable) &&
            Objects.equals(fwebAvailable, video.fwebAvailable) &&
            Objects.equals(imdbRating, video.imdbRating) &&
            Objects.equals(fwebRating, video.fwebRating) &&
            Objects.equals(imdbVotes, video.imdbVotes) &&
            Objects.equals(fwebVotes, video.fwebVotes) &&
            Objects.equals(metascore, video.metascore) &&
            Objects.equals(tomatoRating, video.tomatoRating) &&
            Objects.equals(tomatoUserRating, video.tomatoUserRating) &&
            Objects.equals(imdbID, video.imdbID) &&
            Objects.equals(fwebID, video.fwebID) &&
            Objects.equals(boxart, video.boxart) &&
            Objects.equals(timestamp, video.timestamp) &&
            Objects.equals(fwebPlot, video.fwebPlot);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, fwebTitle, releaseYear, genre, genreId, original, type, omdbAvailable, fwebAvailable, imdbRating, fwebRating, imdbVotes, fwebVotes, metascore, tomatoRating, tomatoUserRating, imdbID, fwebID, boxart, timestamp, fwebPlot);
    }

    @Override
    public String toString() {
        return "Video{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", fwebTitle='" + fwebTitle + '\'' +
            ", releaseYear=" + releaseYear +
            ", genre='" + genre + '\'' +
            ", genreId=" + genreId +
            ", original=" + original +
            ", type='" + type + '\'' +
            ", omdbAvailable=" + omdbAvailable +
            ", fwebAvailable=" + fwebAvailable +
            ", imdbRating=" + imdbRating +
            ", fwebRating=" + fwebRating +
            ", imdbVotes=" + imdbVotes +
            ", fwebVotes=" + fwebVotes +
            ", metascore=" + metascore +
            ", tomatoRating=" + tomatoRating +
            ", tomatoUserRating=" + tomatoUserRating +
            ", imdbID='" + imdbID + '\'' +
            ", fwebID=" + fwebID +
            ", boxart='" + boxart + '\'' +
            ", timestamp=" + timestamp +
            ", fwebPlot='" + fwebPlot + '\'' +
            '}';
    }
}
