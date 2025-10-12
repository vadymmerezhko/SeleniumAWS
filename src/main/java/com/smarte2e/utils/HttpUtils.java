package com.smarte2e.utils;

import okhttp3.*;
import com.smarte2e.exceptions.SmartRuntimeException;
import org.json.JSONArray;
import org.json.JSONObject;

public final class HttpUtils {

    private HttpUtils() {}

    /**
     * Sends HTTP request in JSON format and returns response string.
     * @param json The JSON prompt.
     * @return The response string.
     */
    public static String sendHttpRequest(String url, String json, String apiKey) {

        try {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    if (response.body() != null){
                        return parseJsonContent(response.body().string());
                    }
                } else {
                    String message = response.body() != null ?
                            String.format("Failed to get HTTP response.\n%s",
                                    response.body().string()) :
                            "Failed to get HTTP response";
                    throw new SmartRuntimeException(message);
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Exception when sending Open AI request.", e);
        }
        return null;
    }

    private static String parseJsonContent(String jsonString) {
        // Parse the JSON string
        JSONObject jsonObject = new JSONObject(jsonString);

        // Navigate to the "content" value
        JSONArray choicesArray = jsonObject.getJSONArray("choices");
        JSONObject firstChoice = choicesArray.getJSONObject(0);
        JSONObject messageObject = firstChoice.getJSONObject("message");
        return messageObject.getString("content");
    }
}
