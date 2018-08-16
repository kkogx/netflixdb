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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kogx.netflixdb.config.ApplicationProperties;
import pl.kogx.netflixdb.service.dto.Przelewy24TrxDTO;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;

/**
 * REST controller for managing donate service.
 */
@RestController
@RequestMapping("/api/donate")
public class DonateController {

    private final Logger log = LoggerFactory.getLogger(DonateController.class);

    private final ApplicationProperties.Przelewy24 p24Properties;

    public DonateController(ApplicationProperties applicationProperties) {
        this.p24Properties = applicationProperties.przelewy24;
    }

    @PostMapping("/p24/txnRegister")
    public ResponseEntity<String> p24TxnRegister(HttpSession session, @RequestBody Przelewy24TrxDTO p24) throws IOException {
        Response registerResponse = sendP24Register(session, p24);
        String registerResponseString = registerResponse.returnContent().asString();
        log.info("p24={}, regResponse={}", p24, registerResponseString);
        if (!registerResponseString.contains("error=0")) {
            return ResponseEntity.badRequest().body(registerResponseString);
        }

        final String token = registerResponseString.substring(registerResponseString.lastIndexOf("token=") + 6);
        URI reqUrl = URI.create(p24Properties.getHost()).resolve("/trnRequest/" + token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject res = new JSONObject();
        res.put("url", reqUrl.toASCIIString());
        return new ResponseEntity<>(res.toString(), headers, HttpStatus.OK);
    }

    private Response sendP24Register(HttpSession session, @RequestBody Przelewy24TrxDTO p24) throws IOException {
        URI regUrl = URI.create(p24Properties.getHost()).resolve("/trnRegister");
        String sessionId = DigestUtils.md5Hex(System.currentTimeMillis() + session.getId());
        String amount = String.valueOf(p24.getAmount().multiply(BigDecimal.valueOf(100l)).intValue());
        return Request.Post(regUrl).bodyForm(Form.form()
            .add("p24_merchant_id", p24Properties.getMerchantId())
            .add("p24_pos_id", p24Properties.getMerchantId())
            .add("p24_session_id", sessionId)
            .add("p24_amount", amount)
            .add("p24_currency", p24.getCurrency())
            .add("p24_description", p24.getDescription())
            .add("p24_email", p24.getEmail())
            .add("p24_country", p24.getCountry())
            .add("p24_language", p24.getLanguage())
            .add("p24_url_return", p24.getUrlReturn())
            .add("p24_transfer_label", p24.getTransferLabel())
            .add("p24_sign", DigestUtils.md5Hex(String.format("%s|%s|%s|%s|%s",
                sessionId, p24Properties.getMerchantId(), amount, p24.getCurrency(), p24Properties.getCrc())))
            .add("p24_api_version", "3.2").build()).execute();
    }
}
