package de.seanbri.aquaplane.utility;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PythonAPI {

    public static String post(String endpoint, ObjectNode request_body) {
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.post(endpoint)
                    .header("Content-type", "application/json")
                    .body(request_body.toString())
                    .asJson();

            return httpResponse.getBody().toString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

}
