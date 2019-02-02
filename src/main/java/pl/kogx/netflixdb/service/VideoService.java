package pl.kogx.netflixdb.service;

import pl.kogx.netflixdb.domain.Video;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Video.
 */
public interface VideoService {

    /**
     * Save a video.
     *
     * @param video the entity to save
     * @return the persisted entity
     */
    Video save(Video video);

    /**
     * Get all the videos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Video> findAll(Pageable pageable);


    /**
     * Get the "id" video.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Video> findOne(Long id);

    /**
     * Delete the "id" video.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the video corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Video> search(String query, Pageable pageable);
}
