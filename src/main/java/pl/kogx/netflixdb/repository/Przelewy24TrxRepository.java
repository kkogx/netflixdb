package pl.kogx.netflixdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kogx.netflixdb.domain.Przelewy24Trx;


/**
 * Spring Data  repository for the Przelewy24Trx entity.
 */
@SuppressWarnings("unused")
@Repository
public interface Przelewy24TrxRepository extends JpaRepository<Przelewy24Trx, Long> {

    Przelewy24Trx findBySessionId(String sessionId);
}
