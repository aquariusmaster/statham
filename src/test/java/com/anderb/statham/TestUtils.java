package com.anderb.statham;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class TestUtils {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private String firstName;
        private String lastName;
        private String email;
        private Integer age;
        private boolean active;
        private Address address;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Address {
        private String line1;
        private String line2;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class NasaRes {
        private List<Photo> photos;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Photo {
        private String img_src;
    }
}
