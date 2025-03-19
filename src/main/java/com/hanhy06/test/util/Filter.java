package com.hanhy06.test.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanhy06.test.config.Configs;

public class Filter {
    public static String wordBasedFiltering(String message){

        for (String word : Configs.filterKeywords){
            message = message.replace(word,"#".repeat(word.length()));
        }

        return message;
    }

    public static String aiBasedFiltering(JsonObject aiDetected,String message){
        StringBuilder builder = new StringBuilder(message);

        JsonArray detectedWords = aiDetected.getAsJsonArray("inappropriate_words_detected");
        for (JsonElement word:detectedWords){
            JsonObject object = word.getAsJsonObject();

            int startIndex =object.get("start_index").getAsInt();
            int endIndex =object.get("end_index").getAsInt();

            builder.replace(startIndex,endIndex,"#".repeat(endIndex-startIndex));
        }

        return aiDetected.get("filter_sentence").getAsString();

//        return builder.toString();
    }
}
