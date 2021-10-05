package com.anderb.statham;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StathamTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void jsonToObj() {
        var json = "{\n" +
                "  \"firstName\": \"Andrii\",\n" +
                "  \"lastName\": \"Shtramak\",\n" +
                "  \"email\": \"shtramak@gmail.com\",\n" +
                "  \"active\": true,\n" +
                "  \"age\": 19,\n" +
                "  \"address\": {\n" +
                "    \"line1\": \"Kiev\",\n" +
                "    \"line2\": \"Kopernika\"\n" +
                "  }\n" +
                "}";
        User user = new Statham().jsonToObj(json, User.class);
        System.out.println(user);
    }

    @Test
    void parseToList() {
        var json = "{\n" +
                "  \"photos\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"sol\": 10,\n" +
                "      \"camera\": {\n" +
                "        \"id\": 11,\n" +
                "        \"name\": \"CHEMCAM\",\n" +
                "        \"rover_id\": 5,\n" +
                "        \"full_name\": \"Chemistry and Camera Complex\"\n" +
                "      },\n" +
                "      \"img_src\": \"http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/00010/soas/rdr/ccam/CR0_398380645PRCLF0030000CCAM04010L1.PNG\",\n" +
                "      \"earth_date\": \"2012-08-16\",\n" +
                "      \"rover\": {\n" +
                "        \"id\": 111,\n" +
                "        \"name\": \"Curiosity\",\n" +
                "        \"landing_date\": \"2012-08-06\",\n" +
                "        \"launch_date\": \"2011-11-26\",\n" +
                "        \"status\": \"active\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"sol\": 10,\n" +
                "      \"camera\": {\n" +
                "        \"id\": 22,\n" +
                "        \"name\": \"CHEMCAM\",\n" +
                "        \"rover_id\": 5,\n" +
                "        \"full_name\": \"Chemistry and Camera Complex\"\n" +
                "      },\n" +
                "      \"img_src\": \"http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/00010/opgs/edr/ccam/CR0_398381687EDR_F0030000CCAM05010M_.JPG\",\n" +
                "      \"earth_date\": \"2012-08-16\",\n" +
                "      \"rover\": {\n" +
                "        \"id\": 222,\n" +
                "        \"name\": \"Curiosity\",\n" +
                "        \"landing_date\": \"2012-08-06\",\n" +
                "        \"launch_date\": \"2011-11-26\",\n" +
                "        \"status\": \"active\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        NasaRes nasaRes = new Statham().jsonToObj(json, NasaRes.class);
        assertNotNull(nasaRes);
        assertNotNull(nasaRes.getPhotos());
        assertEquals(2, nasaRes.getPhotos().size());
        assertEquals(
                "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/00010/soas/rdr/ccam/CR0_398380645PRCLF0030000CCAM04010L1.PNG",
                nasaRes.getPhotos().get(0).getImg_src()
        );
        assertEquals(
                "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/00010/opgs/edr/ccam/CR0_398381687EDR_F0030000CCAM05010M_.JPG",
                nasaRes.getPhotos().get(1).getImg_src()
        );
    }

    @Data
    static class User {
        private String firstName;
        private String lastName;
        private String email;
        private Integer age;
        private boolean active;
        private Address address;
    }

    @Data
    static class Address {
        private String line1;
        private String line2;
    }

    @Data
    static class NasaRes {
        private List<Photo> photos;
    }

    @Data
    static class Photo {
        private String img_src;
    }

}