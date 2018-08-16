package pl.kogx.netflixdb.service.dto;

import java.math.BigDecimal;

public class Przelewy24TrxDTO {

    private BigDecimal amount;

    private String currency;

    private String description;

    private String email;

    private String country;

    private String urlReturn;

    private String transferLabel;

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUrlReturn() {
        return urlReturn;
    }

    public void setUrlReturn(String urlReturn) {
        this.urlReturn = urlReturn;
    }

    public String getTransferLabel() {
        return transferLabel;
    }

    public void setTransferLabel(String transferLabel) {
        this.transferLabel = transferLabel;
    }

    @Override
    public String toString() {
        return "Przelewy24TrxDTO{" +
            "amount=" + amount +
            ", currency='" + currency + '\'' +
            ", description='" + description + '\'' +
            ", email='" + email + '\'' +
            ", country='" + country + '\'' +
            ", urlReturn='" + urlReturn + '\'' +
            ", transferLabel='" + transferLabel + '\'' +
            '}';
    }
}
