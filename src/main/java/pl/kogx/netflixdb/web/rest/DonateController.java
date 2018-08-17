package pl.kogx.netflixdb.web.rest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.domain.Przelewy24Trx;
import pl.kogx.netflixdb.service.Przelewy24TrxService;
import pl.kogx.netflixdb.service.dto.Przelewy24TrxDTO;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;

/**
 * REST controller for managing donate service.
 */
@RestController
@RequestMapping("/api/donate")
public class DonateController {

    private final Logger log = LoggerFactory.getLogger(DonateController.class);

    private final ApplicationProperties.Przelewy24 p24Properties;

    private final Przelewy24TrxService p24Service;

    public DonateController(ApplicationProperties applicationProperties, Przelewy24TrxService p24Service) {
        this.p24Properties = applicationProperties.przelewy24;
        this.p24Service = p24Service;
    }

    @PostMapping("/p24/txnRegister")
    public ResponseEntity<String> p24TxnRegister(HttpSession session, @RequestBody Przelewy24TrxDTO p24) throws IOException {
        String sessionId = DigestUtils.md5Hex(System.currentTimeMillis() + session.getId());
        int amount = p24.getAmount().multiply(BigDecimal.valueOf(100L)).intValue();

        // put database entry
        if (null == putTrxEntry(p24, amount, sessionId)) {
            log.error("Unable to create trx entry, sessionId=", sessionId);
            return ResponseEntity.badRequest().build();
        }

        // send txnRegister
        Response registerResponse = sendP24Register(p24, amount, sessionId);
        String registerResponseString = registerResponse.returnContent().asString();
        log.info("p24={}, regResponse={}", p24, registerResponseString);
        if (!registerResponseString.contains("error=0")) {
            return ResponseEntity.badRequest().body(registerResponseString);
        }

        // prepare response
        final String token = registerResponseString.substring(registerResponseString.lastIndexOf("token=") + 6);
        URI reqUrl = URI.create(p24Properties.getHost()).resolve("/trnRequest/" + token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject res = new JSONObject();
        res.put("url", reqUrl.toASCIIString());
        return new ResponseEntity<>(res.toString(), headers, HttpStatus.OK);
    }

    private Przelewy24Trx putTrxEntry(Przelewy24TrxDTO p24, int amount, String sessionId) {
        Przelewy24Trx p24trx = new Przelewy24Trx();
        p24trx.setSessionId(sessionId);
        p24trx.setAmount(amount);
        p24trx.setCreatedDate(Instant.now());
        p24trx.setCountry(p24.getCountry());
        p24trx.setCurrency(p24.getCurrency());
        p24trx.setDescription(p24.getDescription());
        p24trx.setEmail(p24.getEmail());
        return p24Service.save(p24trx);
    }

    private Response sendP24Register(@RequestBody Przelewy24TrxDTO p24, int amount, String sessionId) throws IOException {
        URI regUrl = URI.create(p24Properties.getHost()).resolve("/trnRegister");
        return Request.Post(regUrl).bodyForm(Form.form()
            .add("p24_merchant_id", p24Properties.getMerchantId())
            .add("p24_pos_id", p24Properties.getMerchantId())
            .add("p24_session_id", sessionId)
            .add("p24_amount", String.valueOf(amount))
            .add("p24_currency", p24.getCurrency())
            .add("p24_description", p24.getDescription())
            .add("p24_email", p24.getEmail())
            .add("p24_country", p24.getCountry())
            .add("p24_language", p24.getLanguage())
            .add("p24_url_return", p24.getUrlReturn())
            .add("p24_url_status", "https://" + p24Properties.getStatusHost() + "/api/donate/txnStatus")
            .add("p24_transfer_label", p24.getTransferLabel())
            .add("p24_sign", DigestUtils.md5Hex(String.format("%s|%s|%s|%s|%s",
                sessionId, p24Properties.getMerchantId(), amount, p24.getCurrency(), p24Properties.getCrc())))
            .add("p24_api_version", "3.2").build()).execute();
    }

    @GetMapping("/p24/txnManualVerify")
    public ResponseEntity<String> verify(@RequestParam String p24_session_id, @RequestParam Integer p24_order_id) throws IOException {
        log.info("/txnManualVerify sessionId={}", p24_session_id);
        if (doVerify(p24_session_id, p24_order_id)) {
            return ResponseEntity.ok("verified");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @SuppressWarnings("unused")
    @PostMapping("/p24/txnStatus")
    public void p24TxnStatus(@RequestParam Integer p24_merchant_id,
                             @RequestParam Integer p24_pos_id,
                             @RequestParam String p24_session_id,
                             @RequestParam Integer p24_amount,
                             @RequestParam String p24_currency,
                             @RequestParam Integer p24_order_id,
                             @RequestParam Integer p24_method,
                             @RequestParam String p24_statement,
                             @RequestParam String p24_sign) throws IOException {

        log.info("/txnStatus sessionId={}", p24_session_id);
        doVerify(p24_session_id, p24_order_id);
    }

    private boolean doVerify(String p24_session_id, Integer p24_order_id) throws IOException {
        Przelewy24Trx p24trx = p24Service.findBySessionId(p24_session_id);
        if (p24trx == null) {
            log.error("Unable to find sessionId=", p24_session_id);
            return false;
        }

        URI verifyUrl = URI.create(p24Properties.getHost()).resolve("/trnVerify");
        Request.Post(verifyUrl).bodyForm(Form.form()
            .add("p24_merchant_id", p24Properties.getMerchantId())
            .add("p24_pos_id", p24Properties.getMerchantId())
            .add("p24_session_id", p24trx.getSessionId())
            .add("p24_amount", p24trx.getAmount().toString())
            .add("p24_currency", p24trx.getCurrency())
            .add("p24_orderId", p24_order_id.toString())
            .add("p24_sign", DigestUtils.md5Hex(String.format("%s|%s|%s|%s|%s",
                p24trx.getSessionId(), p24_order_id, p24trx.getAmount(), p24trx.getCurrency(), p24Properties.getCrc())))
            .add("p24_api_version", "3.2").build()).execute();

        p24trx.setConfirmedDate(Instant.now());
        p24Service.save(p24trx);
        return true;
    }
}
