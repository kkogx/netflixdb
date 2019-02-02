package pl.kogx.netflixdb.repository;

import pl.kogx.netflixdb.domain.Przelewy24Trx;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Przelewy24Trx entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Przelewy24TrxRepository extends JpaRepository<Przelewy24Trx, Long> {

}
