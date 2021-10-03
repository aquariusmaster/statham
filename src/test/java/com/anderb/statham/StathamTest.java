package com.anderb.statham;

import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
                "  },\n" +
                "}";
//        User user = jsonToObj(json, User.class);
//        System.out.println(user);

        var json2 = "{\n" +
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
        Map<String, Object> stringObjectMap = new Statham().parseToMap(json2);
        System.out.println(stringObjectMap);
    }

    @Test
    void parseToList() {
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
    static class TupleKey {
        private String key;
        private int line2;
    }
}