package com.hanhy06.test.config;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Configs {
    public static boolean markup = true;
    public static boolean filter = true;
    public static boolean aiFilter = false;
    public static String apiType = "Gemini or OpenAI";
    public static String apiKey = "Your API Key";
    public static boolean onDeviceAiFilter = false;
    public static Path aiPath = null;

    public static void writConfigs(Path filePath) {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndent(2);
        options.setPrettyFlow(true);

        Yaml configs = new Yaml(options);
        Map<String, Object> data = new HashMap<>();

        data.put("markup", markup);
        data.put("filter", filter);
        data.put("aiFilter", aiFilter);
        data.put("apiType",apiType);
        data.put("apiKey", apiKey);
        data.put("onDeviceAiFilter", onDeviceAiFilter);
        data.put("aiPath", aiPath != null ? aiPath.toString() : null);

        String yamlString = configs.dump(data);

        try {
            Files.writeString(filePath, yamlString);
        } catch (IOException e) {
            throw new RuntimeException("데이터를 쓰는 도중 오류가 발생했습니다.", e);
        }
    }

    public static void readConfigs(Path filePath) {
        Yaml yaml = new Yaml();
        try {
            String yamlString = Files.readString(filePath);
            Map<String, Object> data = yaml.load(yamlString);

            markup = (Boolean) data.get("markup");
            filter = (Boolean) data.get("filter");
            aiFilter = (Boolean) data.get("aiFilter");
            apiKey = (String) data.get("apiKey");
            onDeviceAiFilter = (Boolean) data.get("onDeviceAiFilter");
            aiPath = data.get("aiPath") != null ? Path.of((String) data.get("aiPath")) : null;
        } catch (IOException e) {
            throw new RuntimeException("데이터를 읽는 도중 오류가 발생했습니다.", e);
        }
    }

    public static void registerWritAndLoad(){
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            Path path = minecraftServer.getPath("better-chat");

            if(Files.exists(path)){
                readConfigs(path.resolve("Config.yml"));
            }
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
            Path path = minecraftServer.getPath("better-chat");

            if(Files.exists(path)){
                writConfigs(path.resolve("Config.yml"));
            }else {
                try {
                    Files.createDirectory(path);
                    writConfigs(path.resolve("Config.yml"));
                } catch (IOException e) {
                    throw new RuntimeException("파일을 생성하는 도중 오류가 발생했습니다.",e);
                }
            }
        });
    }
}
