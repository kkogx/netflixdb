package pl.kogx.netflixdb.domain;

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
        return "Video{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", releaseYear=" + releaseYear +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id) &&
            Objects.equals(title, video.title) &&
            Objects.equals(releaseYear, video.releaseYear);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, releaseYear);
    }
}
