package com.anderb.statham;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class NasaRes {
        private List<Photo> photos;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Photo {
        private Long id;
        private Long sol;
        private String img_src;
        private Camera camera;
        private String earth_date;
        private Rover rover;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Camera {
        private Long id;
        private String name;
        private Long rover_id;
        private String full_name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class Rover {
        private Long id;
        private String name;
        private String landing_date;
        private String launch_date;
        private String status;
    }
}
