package pl.kogx.netflixdb.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.dto.Przelewy24DTO;

/**
 * REST controller for managing donate service.
 */
@RestController
@RequestMapping("/api/donate")
public class DonateController {

    private final Logger log = LoggerFactory.getLogger(DonateController.class);

    private final ApplicationProperties applicationProperties;

    public DonateController(ApplicationProperties applicationProperties) {
        this.applicationProperties=applicationProperties;
    }

    @GetMapping("/p24")
    public ResponseEntity<Przelewy24DTO> getDonateInfo() {
        log.info("donate info");
        Przelewy24DTO dto = new Przelewy24DTO();
        ApplicationProperties.Przelewy24 p24 = applicationProperties.getPrzelewy24();
        dto.setCrc(p24.getCrc());
        dto.setHost(p24.getHost());
        dto.setMerchantId(p24.getMerchantId());
        return ResponseEntity.ok(dto);
    }
}
