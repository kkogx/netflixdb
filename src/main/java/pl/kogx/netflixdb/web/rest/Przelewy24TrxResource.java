package pl.kogx.netflixdb.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kogx.netflixdb.domain.Przelewy24Trx;
import pl.kogx.netflixdb.service.Przelewy24TrxService;
import pl.kogx.netflixdb.web.rest.errors.BadRequestAlertException;
import pl.kogx.netflixdb.web.rest.util.HeaderUtil;
import pl.kogx.netflixdb.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Przelewy24Trx.
 */
@RestController
@RequestMapping("/api")
public class Przelewy24TrxResource {

    private final Logger log = LoggerFactory.getLogger(Przelewy24TrxResource.class);

    private static final String ENTITY_NAME = "przelewy24Trx";

    private final Przelewy24TrxService przelewy24TrxService;

    public Przelewy24TrxResource(Przelewy24TrxService przelewy24TrxService) {
        this.przelewy24TrxService = przelewy24TrxService;
    }

    /**
     * POST  /przelewy-24-trxes : Create a new przelewy24Trx.
     *
     * @param przelewy24Trx the przelewy24Trx to create
     * @return the ResponseEntity with status 201 (Created) and with body the new przelewy24Trx, or with status 400 (Bad Request) if the przelewy24Trx has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/przelewy-24-trxes")
    @Timed
    public ResponseEntity<Przelewy24Trx> createPrzelewy24Trx(@Valid @RequestBody Przelewy24Trx przelewy24Trx) throws URISyntaxException {
        log.debug("REST request to save Przelewy24Trx : {}", przelewy24Trx);
        if (przelewy24Trx.getId() != null) {
            throw new BadRequestAlertException("A new przelewy24Trx cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Przelewy24Trx result = przelewy24TrxService.save(przelewy24Trx);
        return ResponseEntity.created(new URI("/api/przelewy-24-trxes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /przelewy-24-trxes : Updates an existing przelewy24Trx.
     *
     * @param przelewy24Trx the przelewy24Trx to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated przelewy24Trx,
     * or with status 400 (Bad Request) if the przelewy24Trx is not valid,
     * or with status 500 (Internal Server Error) if the przelewy24Trx couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/przelewy-24-trxes")
    @Timed
    public ResponseEntity<Przelewy24Trx> updatePrzelewy24Trx(@Valid @RequestBody Przelewy24Trx przelewy24Trx) throws URISyntaxException {
        log.debug("REST request to update Przelewy24Trx : {}", przelewy24Trx);
        if (przelewy24Trx.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Przelewy24Trx result = przelewy24TrxService.save(przelewy24Trx);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, przelewy24Trx.getId().toString()))
            .body(result);
    }

    /**
     * GET  /przelewy-24-trxes : get all the przelewy24Trxes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of przelewy24Trxes in body
     */
    @GetMapping("/przelewy-24-trxes")
    @Timed
    public ResponseEntity<List<Przelewy24Trx>> getAllPrzelewy24Trxes(Pageable pageable) {
        log.debug("REST request to get a page of Przelewy24Trxes");
        Page<Przelewy24Trx> page = przelewy24TrxService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/przelewy-24-trxes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /przelewy-24-trxes/:id : get the "id" przelewy24Trx.
     *
     * @param id the id of the przelewy24Trx to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the przelewy24Trx, or with status 404 (Not Found)
     */
    @GetMapping("/przelewy-24-trxes/{id}")
    @Timed
    public ResponseEntity<Przelewy24Trx> getPrzelewy24Trx(@PathVariable Long id) {
        log.debug("REST request to get Przelewy24Trx : {}", id);
        Optional<Przelewy24Trx> przelewy24Trx = przelewy24TrxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(przelewy24Trx);
    }

    /**
     * DELETE  /przelewy-24-trxes/:id : delete the "id" przelewy24Trx.
     *
     * @param id the id of the przelewy24Trx to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/przelewy-24-trxes/{id}")
    @Timed
    public ResponseEntity<Void> deletePrzelewy24Trx(@PathVariable Long id) {
        log.debug("REST request to delete Przelewy24Trx : {}", id);
        przelewy24TrxService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/przelewy-24-trxes?query=:query : search for the przelewy24Trx corresponding
     * to the query.
     *
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/przelewy-24-trxes")
    @Timed
    public ResponseEntity<List<Przelewy24Trx>> searchPrzelewy24Trxes(Pageable pageable) {
        log.debug("REST request to search for a page of Przelewy24Trxes for {}");
        Page<Przelewy24Trx> page = przelewy24TrxService.search( pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders("*", page, "/api/_search/przelewy-24-trxes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
