package com.platzi.pojo.request;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryRequest {
    private String name;
    private String image;

    // Constructors
    public CategoryRequest() {}

    public CategoryRequest(String name, String image) {
        this.name = name;
        this.image = image;
    }

    // Getters
    public String getName() { return name; }
    public String getImage() { return image; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setImage(String image) { this.image = image; }

    // Builder Pattern
    public static CategoryRequestBuilder builder() {
        return new CategoryRequestBuilder();
    }

    public static CategoryRequestBuilder defaultCategory() {
        return new CategoryRequestBuilder()
                .name("Test Category " + System.currentTimeMillis())
                .image("https://api.lorem.space/image?w=640&h=480");
    }

    public static class CategoryRequestBuilder {
        private String name;
        private String image;

        public CategoryRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CategoryRequestBuilder image(String image) {
            this.image = image;
            return this;
        }

        public CategoryRequest build() {
            return new CategoryRequest(name, image);
        }
    }
}
