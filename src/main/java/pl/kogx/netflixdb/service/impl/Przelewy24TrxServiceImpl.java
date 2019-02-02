package pl.kogx.netflixdb.service.impl;

import pl.kogx.netflixdb.service.Przelewy24TrxService;
import pl.kogx.netflixdb.domain.Przelewy24Trx;
import pl.kogx.netflixdb.repository.Przelewy24TrxRepository;
import pl.kogx.netflixdb.repository.search.Przelewy24TrxSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Przelewy24Trx.
 */
@Service
@Transactional
public class Przelewy24TrxServiceImpl implements Przelewy24TrxService {

    private final Logger log = LoggerFactory.getLogger(Przelewy24TrxServiceImpl.class);

    private final Przelewy24TrxRepository przelewy24TrxRepository;

    private final Przelewy24TrxSearchRepository przelewy24TrxSearchRepository;

    public Przelewy24TrxServiceImpl(Przelewy24TrxRepository przelewy24TrxRepository, Przelewy24TrxSearchRepository przelewy24TrxSearchRepository) {
        this.przelewy24TrxRepository = przelewy24TrxRepository;
        this.przelewy24TrxSearchRepository = przelewy24TrxSearchRepository;
    }

    /**
     * Save a przelewy24Trx.
     *
     * @param przelewy24Trx the entity to save
     * @return the persisted entity
     */
    @Override
    public Przelewy24Trx save(Przelewy24Trx przelewy24Trx) {
        log.debug("Request to save Przelewy24Trx : {}", przelewy24Trx);        Przelewy24Trx result = przelewy24TrxRepository.save(przelewy24Trx);
        przelewy24TrxSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the przelewy24Trxes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Przelewy24Trx> findAll(Pageable pageable) {
        log.debug("Request to get all Przelewy24Trxes");
        return przelewy24TrxRepository.findAll(pageable);
    }


    /**
     * Get one przelewy24Trx by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Przelewy24Trx> findOne(Long id) {
        log.debug("Request to get Przelewy24Trx : {}", id);
        return przelewy24TrxRepository.findById(id);
    }

    /**
     * Delete the przelewy24Trx by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Przelewy24Trx : {}", id);
        przelewy24TrxRepository.deleteById(id);
        przelewy24TrxSearchRepository.deleteById(id);
    }

    /**
     * Search for the przelewy24Trx corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Przelewy24Trx> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Przelewy24Trxes for query {}", query);
        return przelewy24TrxSearchRepository.search(queryStringQuery(query), pageable);    }
}
