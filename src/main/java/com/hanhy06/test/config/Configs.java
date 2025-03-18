package com.hanhy06.test.config;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Configs {
    public static boolean enableMarkup = true;
    public static boolean enableFilter = true;
    public static List<String> filterKeywords = List.of("explain", "keyword");
    public static boolean enableAIFilter = false;
    public static String apiProvider = "Google or OpenAI";
    public static String apiKey = "Your API Key";
    public static String apiModelName = "Model Name";
    public static boolean enableOnDeviceAIFilter = false;
    public static Path onDeviceAIFilterPath = null;

    public static void writeConfigs(Path filePath) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        Representer representer = new Representer(options) {
            private DumperOptions.FlowStyle determineFlowStyle(Object data) {
                return data instanceof List ? DumperOptions.FlowStyle.FLOW : DumperOptions.FlowStyle.BLOCK;
            }
        };

        Yaml yaml = new Yaml(representer);
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("enableMarkup", enableMarkup);
        data.put("enableFilter", enableFilter);
        data.put("filterKeywords", filterKeywords);
        data.put("enableAIFilter", enableAIFilter);
        data.put("apiProvider", apiProvider);
        data.put("apiKey", apiKey);
        data.put("apiModelName", apiModelName);
        data.put("enableOnDeviceAIFilter", enableOnDeviceAIFilter);
        data.put("onDeviceAIFilterPath", onDeviceAIFilterPath != null ? onDeviceAIFilterPath.toString() : null);
        String yamlString = yaml.dump(data);

        StringBuilder builder = new StringBuilder(yamlString);
        builder.insert(yamlString.indexOf("apiModelName"),
                "#You can find the prices and details of each model on these websites:  \n" +
                "#Google Gemini API Models https://ai.google.dev/gemini-api/docs/models/gemini?hl=en  \n" +
                "#OpenAI Pricing https://platform.openai.com/docs/pricing\n"
        );

        try {
            Files.writeString(filePath, builder.toString());
        } catch (IOException e) {
            throw new RuntimeException("데이터를 쓰는 도중 오류가 발생했습니다.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void readConfigs(Path filePath) {
        Yaml yaml = new Yaml();
        try {
            String yamlString = Files.readString(filePath);
            Map<String, Object> data = (Map<String, Object>) yaml.load(yamlString);

            enableMarkup = (Boolean) data.get("enableMarkup");
            enableFilter = (Boolean) data.get("enableFilter");
            filterKeywords = (List<String>) data.get("filterKeywords");
            enableAIFilter = (Boolean) data.get("enableAIFilter");
            apiProvider = (String) data.get("apiProvider");
            apiKey = (String) data.get("apiKey");
            apiModelName = (String) data.get("apiModelName");
            enableOnDeviceAIFilter = (Boolean) data.get("enableOnDeviceAIFilter");
            onDeviceAIFilterPath = data.get("onDeviceAIFilterPath") != null ? Path.of((String) data.get("onDeviceAIFilterPath")) : null;
        } catch (IOException e) {
            throw new RuntimeException("데이터를 읽는 도중 오류가 발생했습니다.", e);
        }
    }

    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            Path path = minecraftServer.getPath("better-chat").resolve("Config.yml");
            if (Files.exists(path)) {
                readConfigs(path);
            }
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
            Path path = minecraftServer.getPath("better-chat");
            if (Files.exists(path)) {
                writeConfigs(path.resolve("Config.yml"));
            } else {
                try {
                    Files.createDirectory(path);
                    writeConfigs(path.resolve("Config.yml"));
                } catch (IOException e) {
                    throw new RuntimeException("파일을 생성하는 도중 오류가 발생했습니다.", e);
                }
            }
        });
    }
}
