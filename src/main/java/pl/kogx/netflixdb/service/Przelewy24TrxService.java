package pl.kogx.netflixdb.service;

import pl.kogx.netflixdb.domain.Przelewy24Trx;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Przelewy24Trx.
 */
public interface Przelewy24TrxService {

    /**
     * Save a przelewy24Trx.
     *
     * @param przelewy24Trx the entity to save
     * @return the persisted entity
     */
    Przelewy24Trx save(Przelewy24Trx przelewy24Trx);

    /**
     * Get all the przelewy24Trxes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Przelewy24Trx> findAll(Pageable pageable);


    /**
     * Get the "id" przelewy24Trx.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Przelewy24Trx> findOne(Long id);

    /**
     * Delete the "id" przelewy24Trx.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the przelewy24Trx corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Przelewy24Trx> search(String query, Pageable pageable);
}
