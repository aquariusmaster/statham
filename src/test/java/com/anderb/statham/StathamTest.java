package com.anderb.statham;

import com.anderb.statham.TestUtils.Address;
import com.anderb.statham.TestUtils.NasaRes;
import com.anderb.statham.TestUtils.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class StathamTest {

    @Test
    void parseToObj_withInnerObject() {
        var json = "{\n" +
                "  \"firstName\": \"Andrii\",\n" +
                "  \"lastName\": \"Petrov\",\n" +
                "  \"email\": \"apetrov@gmail.com\",\n" +
                "  \"active\": true,\n" +
                "  \"age\": 19,\n" +
                "  \"address\": {\n" +
                "    \"line1\": \"Kiev\",\n" +
                "    \"line2\": \"Svitla\"\n" +
                "  }\n" +
                "}";
        User actual = new Statham().jsonToObj(json, User.class);
        assertNotNull(actual);
        User expected = new User(
                "Andrii",
                "Petrov",
                "apetrov@gmail.com",
                19,
                true,
                new Address("Kiev", "Svitla")
        );
        assertEquals(expected, actual);
    }

    @Test
    void parseToObj_withJsonHasWhiteSpaces_returnValidResult() {
        var json = "{\n" +
                "\"firstName\":          \"Andrii\",\n" +
                "  \"lastName\": \"Petrov\"      ,     \n" +
                "  \"email\" :\"apetrov@gmail.com\",\n" +
                "  \"age\": 19,\n" +
                "  \"address\": {\n" +
                "    \"line1\":\"Kiev\" , \n" +
                "    \"line2\": \"Svitla\"\n" +
                "  } ,  \n" +
                "  \"active\"      : true   \n" +
                "}";
        User actual = new Statham().jsonToObj(json, User.class);
        assertNotNull(actual);
        User expected = new User(
                "Andrii",
                "Petrov",
                "apetrov@gmail.com",
                19,
                true,
                new Address("Kiev", "Svitla")
        );
        assertEquals(expected, actual);
    }

    @Test
    void parseToObj_whenEndNotComma_returnValidResult() {
        var json = "{\n" +
                "  \"firstName\": \"Andrii\",\n" +
                "  \"lastName\": \"Petrov\",\n" +
                "  \"email\": \"apetrov@gmail.com\",\n" +
                "  \"active\": true,\n" +
                "  \"age\": 19\n" +
                "}";
        User actual = new Statham().jsonToObj(json, User.class);
        assertNotNull(actual);
        User expected = new User(
                "Andrii",
                "Petrov",
                "apetrov@gmail.com",
                19,
                true,
                null
        );
        assertEquals(expected, actual);
    }

    @Test
    void parseToObj_withEmptyJson_returnNull() {
        var json = "{}";
        User actual = new Statham().jsonToObj(json, User.class);
        assertNull(actual);
    }

    @Test
    void parseToObj_withList() {
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

    @Test
    void parseToObj_largeObject() throws IOException {
        String json = Files.readString(Paths.get("src/test/resources/large.json"));
        NasaRes nasaRes = new Statham().jsonToObj(json, NasaRes.class);
        assertNotNull(nasaRes);
        assertNotNull(nasaRes.getPhotos());
        assertEquals(856, nasaRes.getPhotos().size());
        assertEquals(
                "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/fcam/FLB_486265257EDR_F0481570FHAZ00323M_.JPG",
                nasaRes.getPhotos().get(0).getImg_src()
        );
        assertEquals(
                "http://mars.jpl.nasa.gov/msl-raw-images/msss/01000/mcam/1000ML0044630750405172I01_DXXX.jpg",
                nasaRes.getPhotos().get(400).getImg_src()
        );
        assertEquals(
                "http://mars.jpl.nasa.gov/msl-raw-images/proj/msl/redops/ods/surface/sol/01000/opgs/edr/ncam/NRB_486270860EDR_F0481570NCAM00323M_.JPG",
                nasaRes.getPhotos().get(855).getImg_src()
        );
    }

}