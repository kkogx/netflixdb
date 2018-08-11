package pl.kogx.netflixdb.service.dto;

import org.springframework.format.annotation.NumberFormat;
import pl.kogx.netflixdb.domain.Video;

import java.util.Date;

/**
 * A DTO representing a video, with his authorities.
 */
public class VideoDTO {

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

    private String fwebPlot;

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
        this.fwebAvailable = video.getFwebAvailable();
        this.imdbRating = video.getImdbRating();
        this.fwebRating = round(video.getFwebRating(), 1);
        this.imdbVotes = video.getImdbVotes();
        this.fwebVotes = video.getFwebVotes();
        this.metascore = video.getMetascore();
        this.tomatoRating = video.getTomatoRating();
        this.tomatoUserRating = video.getTomatoUserRating();
        this.imdbID = video.getImdbID();
        this.fwebID = video.getFwebID();
        this.fwebTitle = video.getFwebTitle();
        this.boxart = video.getBoxart();
        this.timestamp = video.getTimestamp();
        this.fwebPlot = video.getFwebPlot();
    }

    private static Float round (Float value, int precision) {
        if(value == null) {
            return null;
        }
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    public String getFwebPlot() {
        return fwebPlot;
    }

    public void setFwebPlot(String fwebPlot) {
        this.fwebPlot = fwebPlot;
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

    public String getFwebTitle() {
        return fwebTitle;
    }

    public void setFwebTitle(String fwebTitle) {
        this.fwebTitle = fwebTitle;
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

    public Boolean getFwebAvailable() {
        return fwebAvailable;
    }

    public void setFwebAvailable(Boolean fwebAvailable) {
        this.fwebAvailable = fwebAvailable;
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

    @Override
    public String toString() {
        return "VideoDTO{" +
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
            ", fwebPlot='" + fwebPlot + '\'' +
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
            '}';
    }
}
