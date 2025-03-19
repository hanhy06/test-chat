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
                          "text": "Please analyze the following sentence and identify all inappropriate words. Return the results in JSON format according to the given schema.\\n\\n\
            1) All character positions in the sentence start at index 0.\\n\
            2) For each detected inappropriate word, only include the actual letters (excluding spaces or punctuation before/after) in the start_index and end_index.\\n\
            3) Make sure to carefully check each character's position in the original sentence.\\n\
            4) Even if the word is in uppercase or contains special symbols (e.g., 'F##k'), the indexes should match exactly where it appears in the text.\\n\
            5) Example: If the sentence is 'Hello f##k you!', then 'f##k' starts at index 6 and ends at index 9.\\n\
            6) Please use the JSON schema below and provide valid JSON as the response.\\n\\n\
            Sentence: %s"
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
                                "description": "The start index of this inappropriate word in the original sentence (0-based). For '---fuck--', the start index is 3."
                              },
                              "end_index": {
                                "type": "integer",
                                "description": "The end index of this inappropriate word in the original sentence (0-based, representing the last character). For '---fuck--', the end index is 6."
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
                        "filter_sentence": {
                          "type": "string",
                          "description": "The original sentence with all inappropriate words replaced by '#'"
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
            """, input
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