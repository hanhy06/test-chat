package com.hanhy06.test.util;

import com.google.gson.*;
import com.hanhy06.test.config.Configs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GeminiAPI {
    public static JsonObject get(String input) throws Exception {
        URI uri = new URI("https://generativelanguage.googleapis.com/v1beta/models/"
                + Configs.apiModelName
                + ":generateContent?key="
                + Configs.apiKey);

        String prompt = String.format(
                """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 1,
                    "topK": 40,
                    "topP": 0.95,
                    "maxOutputTokens": 8192,
                    "responseMimeType": "application/json",
                    "responseSchema": {
                      "type": "object",
                      "properties": {
                        "original_sentence": {
                          "type": "string",
                          "description": "Original sentence"
                        },
                        "inappropriate_word_count": {
                          "type": "integer",
                          "description": "Total number of inappropriate words detected"
                        },
                        "inappropriate_words_detected": {
                          "type": "array",
                          "description": "List of inappropriate words detected",
                          "items": {
                            "type": "object",
                            "properties": {
                              "word": {
                                "type": "string",
                                "description": "Inappropriate word detected"
                              },
                              "severity_level": {
                                "type": "integer",
                                "description": "Severity level of the inappropriate word (1: low, 2: medium, 3: high)",
                                "minimum": 1,
                                "maximum": 3
                              },
                              "start_index": {
                                "type": "integer",
                                "description": "The start index of this inappropriate word in the original sentence (0-based indexing). For example, in '---fuck--', the start index should be 3."
                              },
                              "end_index": {
                                "type": "integer",
                                "description": "The end index of this inappropriate word in the original sentence (0-based indexing, representing the last character of the word). For example, in '---fuck--', the end index should be 6."
                              },
                              "category": {
                                "type": "string",
                                "description": "Category of the detected word (e.g., curse, hate speech, etc.)"
                              }
                            },
                            "required": [
                              "word",
                              "severity_level",
                              "start_index",
                              "end_index"
                            ]
                          }
                        },
                        "overall_severity_level": {
                          "type": "integer",
                          "description": "Overall severity level of the sentence (0: safe, 1: low, 2: medium, 3: high)",
                          "minimum": 0,
                          "maximum": 3
                        },
                        "filter_sentence":{
                            "type": "string",
                            "description": "Replace inappropriate words in the original sentence with #"
                        }
                      },
                      "required": [
                        "inappropriate_word_count",
                        "inappropriate_words_detected",
                        "overall_severity_level",
                        "filter_sentence"
                      ]
                    }
                  }
                }
                """,
                input
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(prompt, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

        return getInnerJson(jsonResponse);
    }

    private static JsonObject getInnerJson(JsonObject json) {
        JsonArray candidates = json.getAsJsonArray("candidates");

        if (candidates != null && candidates.size() > 0) {
            JsonObject candidate = candidates.get(0).getAsJsonObject();

            JsonObject content = candidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");

            if (parts != null && parts.size() > 0) {
                JsonObject firstPart = parts.get(0).getAsJsonObject();
                if (firstPart.has("text")) {
                    String text = firstPart.get("text").getAsString();
                    return JsonParser.parseString(text).getAsJsonObject();
                }
            }
        }
        return new JsonObject();
    }
}