package pl.kogx.netflixdb.domain;


import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Video.
 */
@Entity
@Table(name = "video")
@Document(indexName = "video")
public class Video implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "fweb_title")
    private String fwebTitle;

    @Column(name = "release_year")
    private Integer releaseYear;

    @Column(name = "genre")
    private String genre;

    @Column(name = "genre_id")
    private Long genreId;

    @Column(name = "original")
    private Boolean original;

    @Column(name = "jhi_type")
    private String type;

    @Column(name = "omdb_available")
    private Boolean omdbAvailable;

    @Column(name = "fweb_available")
    private Boolean fwebAvailable;

    @Column(name = "imdb_rating")
    private Float imdbRating;

    @Column(name = "fweb_rating")
    private Float fwebRating;

    @Column(name = "imdb_votes")
    private Long imdbVotes;

    @Column(name = "fweb_votes")
    private Long fwebVotes;

    @Column(name = "metascore")
    private Integer metascore;

    @Column(name = "tomato_rating")
    private Float tomatoRating;

    @Column(name = "tomato_user_rating")
    private Float tomatoUserRating;

    @Column(name = "imdb_id")
    private String imdbID;

    @Column(name = "fweb_id")
    private Long fwebID;

    @Column(name = "boxart")
    private String boxart;

    @Column(name = "fweb_plot")
    private String fwebPlot;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Video title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFwebTitle() {
        return fwebTitle;
    }

    public Video fwebTitle(String fwebTitle) {
        this.fwebTitle = fwebTitle;
        return this;
    }

    public void setFwebTitle(String fwebTitle) {
        this.fwebTitle = fwebTitle;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public Video releaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public Video genre(String genre) {
        this.genre = genre;
        return this;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Long getGenreId() {
        return genreId;
    }

    public Video genreId(Long genreId) {
        this.genreId = genreId;
        return this;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public Boolean isOriginal() {
        return original;
    }

    public Video original(Boolean original) {
        this.original = original;
        return this;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

    public String getType() {
        return type;
    }

    public Video type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isOmdbAvailable() {
        return omdbAvailable;
    }

    public Video omdbAvailable(Boolean omdbAvailable) {
        this.omdbAvailable = omdbAvailable;
        return this;
    }

    public void setOmdbAvailable(Boolean omdbAvailable) {
        this.omdbAvailable = omdbAvailable;
    }

    public Boolean isFwebAvailable() {
        return fwebAvailable;
    }

    public Video fwebAvailable(Boolean fwebAvailable) {
        this.fwebAvailable = fwebAvailable;
        return this;
    }

    public void setFwebAvailable(Boolean fwebAvailable) {
        this.fwebAvailable = fwebAvailable;
    }

    public Float getImdbRating() {
        return imdbRating;
    }

    public Video imdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
        return this;
    }

    public void setImdbRating(Float imdbRating) {
        this.imdbRating = imdbRating;
    }

    public Float getFwebRating() {
        return fwebRating;
    }

    public Video fwebRating(Float fwebRating) {
        this.fwebRating = fwebRating;
        return this;
    }

    public void setFwebRating(Float fwebRating) {
        this.fwebRating = fwebRating;
    }

    public Long getImdbVotes() {
        return imdbVotes;
    }

    public Video imdbVotes(Long imdbVotes) {
        this.imdbVotes = imdbVotes;
        return this;
    }

    public void setImdbVotes(Long imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    public Long getFwebVotes() {
        return fwebVotes;
    }

    public Video fwebVotes(Long fwebVotes) {
        this.fwebVotes = fwebVotes;
        return this;
    }

    public void setFwebVotes(Long fwebVotes) {
        this.fwebVotes = fwebVotes;
    }

    public Integer getMetascore() {
        return metascore;
    }

    public Video metascore(Integer metascore) {
        this.metascore = metascore;
        return this;
    }

    public void setMetascore(Integer metascore) {
        this.metascore = metascore;
    }

    public Float getTomatoRating() {
        return tomatoRating;
    }

    public Video tomatoRating(Float tomatoRating) {
        this.tomatoRating = tomatoRating;
        return this;
    }

    public void setTomatoRating(Float tomatoRating) {
        this.tomatoRating = tomatoRating;
    }

    public Float getTomatoUserRating() {
        return tomatoUserRating;
    }

    public Video tomatoUserRating(Float tomatoUserRating) {
        this.tomatoUserRating = tomatoUserRating;
        return this;
    }

    public void setTomatoUserRating(Float tomatoUserRating) {
        this.tomatoUserRating = tomatoUserRating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public Video imdbID(String imdbID) {
        this.imdbID = imdbID;
        return this;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public Long getFwebID() {
        return fwebID;
    }

    public Video fwebID(Long fwebID) {
        this.fwebID = fwebID;
        return this;
    }

    public void setFwebID(Long fwebID) {
        this.fwebID = fwebID;
    }

    public String getBoxart() {
        return boxart;
    }

    public Video boxart(String boxart) {
        this.boxart = boxart;
        return this;
    }

    public void setBoxart(String boxart) {
        this.boxart = boxart;
    }

    public String getFwebPlot() {
        return fwebPlot;
    }

    public Video fwebPlot(String fwebPlot) {
        this.fwebPlot = fwebPlot;
        return this;
    }

    public void setFwebPlot(String fwebPlot) {
        this.fwebPlot = fwebPlot;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Video video = (Video) o;
        if (video.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), video.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Video{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", fwebTitle='" + getFwebTitle() + "'" +
            ", releaseYear=" + getReleaseYear() +
            ", genre='" + getGenre() + "'" +
            ", genreId=" + getGenreId() +
            ", original='" + isOriginal() + "'" +
            ", type='" + getType() + "'" +
            ", omdbAvailable='" + isOmdbAvailable() + "'" +
            ", fwebAvailable='" + isFwebAvailable() + "'" +
            ", imdbRating=" + getImdbRating() +
            ", fwebRating=" + getFwebRating() +
            ", imdbVotes=" + getImdbVotes() +
            ", fwebVotes=" + getFwebVotes() +
            ", metascore=" + getMetascore() +
            ", tomatoRating=" + getTomatoRating() +
            ", tomatoUserRating=" + getTomatoUserRating() +
            ", imdbID='" + getImdbID() + "'" +
            ", fwebID=" + getFwebID() +
            ", boxart='" + getBoxart() + "'" +
            ", fwebPlot='" + getFwebPlot() + "'" +
            "}";
    }
}
