package pl.kogx.netflixdb.service.dto;

import pl.kogx.netflixdb.domain.Video;

/**
 * A DTO representing a video, with his authorities.
 */
public class VideoDTO {

    private Long id;

    private String title;

    private Integer releaseYear;

    public VideoDTO() {

    }

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
        this.releaseYear = video.getReleaseYear();
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

    @Override
    public String toString() {
        return "VideoDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", releaseYear=" + releaseYear +
            '}';
    }
}
