package com.example.fiapsoattechchallengeproductapi.domain;

public class ProductImageDTO {
    private String url;

    public ProductImageDTO() {
    }

    public ProductImageDTO(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
