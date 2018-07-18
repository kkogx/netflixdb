package pl.kogx.netflixdb.domain;

import org.springframework.format.annotation.NumberFormat;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "videos")
public class Video extends AbstractAuditingEntity implements Serializable {

    @Id
    @NotNull
    private Long id;

    private String title;

    private Integer releaseYear;

    private String genre;

    private Long genreId;

    private Boolean original;

    private String type;

    private Boolean omdbAvailable;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float imdbRating;

    private Long imdbVotes;

    private Integer metascore;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float tomatoRating;

    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Float tomatoUserRating;

    private String imdbID;

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

    @Override
    public String toString() {
        return "Video{" +
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id) &&
            Objects.equals(title, video.title) &&
            Objects.equals(releaseYear, video.releaseYear) &&
            Objects.equals(genre, video.genre) &&
            Objects.equals(genreId, video.genreId) &&
            Objects.equals(original, video.original) &&
            Objects.equals(type, video.type) &&
            Objects.equals(omdbAvailable, video.omdbAvailable) &&
            Objects.equals(imdbRating, video.imdbRating) &&
            Objects.equals(imdbVotes, video.imdbVotes) &&
            Objects.equals(metascore, video.metascore) &&
            Objects.equals(tomatoRating, video.tomatoRating) &&
            Objects.equals(tomatoUserRating, video.tomatoUserRating) &&
            Objects.equals(imdbID, video.imdbID);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, releaseYear, genre, genreId, original, type, omdbAvailable, imdbRating, imdbVotes, metascore, tomatoRating, tomatoUserRating, imdbID);
    }
}
