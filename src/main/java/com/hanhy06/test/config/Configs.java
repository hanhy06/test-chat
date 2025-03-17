package com.hanhy06.test.config;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Configs {
    public static boolean markup = true;
    public static boolean filter = true;
    public static List<String> words = List.of("explain","word");
    public static boolean aiFilter = false;
    public static String apiType = "Gemini or OpenAI";
    public static String apiKey = "Your API Key";
    public static boolean onDeviceAiFilter = false;
    public static Path onDeviceAiPath = null;

    public static void writConfigs(Path filePath) {
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

        data.put("markup", markup);
        data.put("filter", filter);
        data.put("words", words);
        data.put("aiFilter", aiFilter);
        data.put("apiType", apiType);
        data.put("apiKey", apiKey);
        data.put("onDeviceAiFilter", onDeviceAiFilter);
        data.put("onDeviceAiPath", onDeviceAiPath != null ? onDeviceAiPath.toString() : null);
        String yamlString = yaml.dump(data);

        try {
            Files.writeString(filePath, yamlString);
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

            markup = (Boolean) data.get("markup");
            filter = (Boolean) data.get("filter");
            words = (List<String>) data.get("words");
            aiFilter = (Boolean) data.get("aiFilter");
            apiKey = (String) data.get("apiKey");
            onDeviceAiFilter = (Boolean) data.get("onDeviceAiFilter");
            onDeviceAiPath = data.get("onDeviceAiPath") != null ? Path.of((String) data.get("onDeviceAiPath")) : null;
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
                writConfigs(path.resolve("Config.yml"));
            } else {
                try {
                    Files.createDirectory(path);
                    writConfigs(path.resolve("Config.yml"));
                } catch (IOException e) {
                    throw new RuntimeException("파일을 생성하는 도중 오류가 발생했습니다.", e);
                }
            }
        });
    }
}
