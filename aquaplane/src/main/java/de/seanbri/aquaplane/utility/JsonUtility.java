package de.seanbri.aquaplane.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonUtility {

    public static <T> T getObjectFrom(String json, Class<T> toValueType) {
        try {
            ObjectMapper mapper = new ObjectMapper();

           return mapper.readValue(json, toValueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertObjectToType(Object fromValue, Class<T> toValueType) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper.convertValue(fromValue, toValueType);
    }

    public static String prettyPrint(JsonNode jsonNode) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
