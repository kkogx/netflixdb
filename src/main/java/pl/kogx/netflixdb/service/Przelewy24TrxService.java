package pl.kogx.netflixdb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kogx.netflixdb.domain.Przelewy24Trx;
import pl.kogx.netflixdb.repository.Przelewy24TrxRepository;

import java.util.Optional;

/**
 * Service Interface for managing Przelewy24Trx.
 */
@Service
@Transactional
public class Przelewy24TrxService {

    private final Logger log = LoggerFactory.getLogger(Przelewy24TrxService.class);

    private final Przelewy24TrxRepository przelewy24TrxRepository;

    public Przelewy24TrxService(Przelewy24TrxRepository przelewy24TrxRepository) {
        this.przelewy24TrxRepository = przelewy24TrxRepository;
    }

    /**
     * Save a przelewy24Trx.
     *
     * @param przelewy24Trx the entity to save
     * @return the persisted entity
     */
    public Przelewy24Trx save(Przelewy24Trx przelewy24Trx) {
        log.debug("Request to save Przelewy24Trx : {}", przelewy24Trx);
        Przelewy24Trx result = przelewy24TrxRepository.save(przelewy24Trx);
        return result;
    }

    /**
     * Get all the przelewy24Trxes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
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
    public void delete(Long id) {
        log.debug("Request to delete Przelewy24Trx : {}", id);
        przelewy24TrxRepository.deleteById(id);
    }

    /**
     * Search for the przelewy24Trx corresponding to the query.
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<Przelewy24Trx> search(Pageable pageable) {
        return przelewy24TrxRepository.findAll(pageable);
    }

    public Przelewy24Trx findBySessionId(String sessionId) {
        return przelewy24TrxRepository.findBySessionId(sessionId);
    }
}
