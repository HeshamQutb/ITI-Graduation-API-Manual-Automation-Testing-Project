package com.platzi.pojo.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductRequest {
    private String title;
    private Integer price;
    private String description;
    private Integer categoryId;
    private List<String> images;

    // Constructors
    public ProductRequest() {}

    public ProductRequest(String title, Integer price, String description,
                          Integer categoryId, List<String> images) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
        this.images = images;
    }

    // Getters
    public String getTitle() { return title; }
    public Integer getPrice() { return price; }
    public String getDescription() { return description; }
    public Integer getCategoryId() { return categoryId; }
    public List<String> getImages() { return images; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setPrice(Integer price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public void setImages(List<String> images) { this.images = images; }

    // Builder Pattern
    public static ProductRequestBuilder builder() {
        return new ProductRequestBuilder();
    }

    public static ProductRequestBuilder defaultProduct() {
        return new ProductRequestBuilder()
                .title("Test Product " + System.currentTimeMillis())
                .price(299)
                .description("Test product description")
                .categoryId(1)
                .images(List.of("https://placeimg.com/640/480/any"));
    }

    public static class ProductRequestBuilder {
        private String title;
        private Integer price;
        private String description;
        private Integer categoryId;
        private List<String> images;

        public ProductRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ProductRequestBuilder price(Integer price) {
            this.price = price;
            return this;
        }

        public ProductRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductRequestBuilder categoryId(Integer categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public ProductRequestBuilder images(List<String> images) {
            this.images = images;
            return this;
        }

        public ProductRequest build() {
            return new ProductRequest(title, price, description, categoryId, images);
        }
    }
}