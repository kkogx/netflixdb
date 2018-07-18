package pl.kogx.netflixdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.Video;
import pl.kogx.netflixdb.repository.search.VideoSearchRepository;
import pl.kogx.netflixdb.service.dto.VideoDTO;
import pl.kogx.netflixdb.service.util.NullAwareBeanUtilsBean;

/**
 * Service class for managing videos.
 */
@Service
@Transactional
public class VideoService {

    private final Logger log = LoggerFactory.getLogger(VideoService.class);

    private final VideoSearchRepository videoSearchRepository;

    public VideoService(VideoSearchRepository videoSearchRepository) {
        this.videoSearchRepository = videoSearchRepository;
    }

    public VideoDTO updateVideo(VideoDTO videoDTO) {
        Video video = videoSearchRepository.findById(videoDTO.getId()).orElse(new Video(videoDTO.getId()));
        new NullAwareBeanUtilsBean().copyProperties(video, videoDTO);
        return new VideoDTO(videoSearchRepository.save(video));
    }

    public void deleteVideo(Long id) {
        videoSearchRepository.deleteById(id);
    }

    public Page<VideoDTO> findByGenreId(Long genreId, Pageable pageable) {
        return videoSearchRepository.findAllByGenreId(genreId, pageable).map(VideoDTO::new);
    }
}
