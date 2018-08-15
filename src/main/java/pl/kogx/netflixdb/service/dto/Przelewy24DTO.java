package pl.kogx.netflixdb.service.dto;

public class Przelewy24DTO {

    private String host;

    private String crc;

    private String merchantId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
            this.merchantId = merchantId;
        }
}
