package pl.kogx.netflixdb.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Przelewy24Trx.
 */
@Entity
@Table(name = "przelewy_24_trx")
@Document(indexName = "przelewy24trx")
public class Przelewy24Trx implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "confirmed_date")
    private Instant confirmedDate;

    @Column(name = "email")
    private String email;

    @NotNull
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "description")
    private String description;

    @Column(name = "currency")
    private String currency;

    @Column(name = "country")
    private String country;

    @Column(name = "amount")
    private Integer amount;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Przelewy24Trx createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getConfirmedDate() {
        return confirmedDate;
    }

    public Przelewy24Trx confirmedDate(Instant confirmedDate) {
        this.confirmedDate = confirmedDate;
        return this;
    }

    public void setConfirmedDate(Instant confirmedDate) {
        this.confirmedDate = confirmedDate;
    }

    public String getEmail() {
        return email;
    }

    public Przelewy24Trx email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Przelewy24Trx sessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDescription() {
        return description;
    }

    public Przelewy24Trx description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public Przelewy24Trx currency(String currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public Przelewy24Trx country(String country) {
        this.country = country;
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getAmount() {
        return amount;
    }

    public Przelewy24Trx amount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Przelewy24Trx przelewy24Trx = (Przelewy24Trx) o;
        if (przelewy24Trx.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), przelewy24Trx.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Przelewy24Trx{" +
            "id=" + getId() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", confirmedDate='" + getConfirmedDate() + "'" +
            ", email='" + getEmail() + "'" +
            ", sessionId='" + getSessionId() + "'" +
            ", description='" + getDescription() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", country='" + getCountry() + "'" +
            ", amount=" + getAmount() +
            "}";
    }
}
