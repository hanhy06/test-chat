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

    public static String geminiBasedFiltering(String message){
        JsonObject aiDetected = null;

        try {
            aiDetected = GeminiAPI.get(message);
        } catch (Exception e) {
            throw new RuntimeException("api 요청 실패",e);
        }

        StringBuilder builder = new StringBuilder(message);
        String temp = message;

        JsonArray detectedWords = aiDetected.getAsJsonArray("inappropriate_words_detected");
        for (JsonElement word:detectedWords){
            JsonObject object = word.getAsJsonObject();

            int startIndex =object.get("start_index").getAsInt();
            int endIndex =object.get("end_index").getAsInt();

            builder.replace(startIndex,endIndex+1,"#".repeat(endIndex-startIndex));
            temp = temp.replace(object.get("word").getAsString(),"#".repeat(object.get("word").getAsString().length()));
        }

//        return aiDetected.get("filter_sentence").getAsString();

        return "\n" + builder.toString()+ "\n\n" + aiDetected.get("filter_sentence").getAsString() + "\n\n" + temp;
    }
}
