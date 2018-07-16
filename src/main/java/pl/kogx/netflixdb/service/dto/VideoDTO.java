package pl.kogx.netflixdb.service.dto;

import pl.kogx.netflixdb.domain.Video;

/**
 * A DTO representing a video, with his authorities.
 */
public class VideoDTO {

    private Long id;

    private String title;

    public VideoDTO() {

    }

    public VideoDTO(Video video) {
        this.id = video.getId();
        this.title = video.getTitle();
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

    @Override
    public String toString() {
        return "VideoDTO{" +
            "id=" + id +
            ", title='" + title + '\'' +
            '}';
    }
}
