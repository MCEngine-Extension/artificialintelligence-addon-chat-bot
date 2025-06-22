package io.github.mcengine.addon.artificialintelligence.chatbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ChatBotUtil {

    public static void createSimpleFile(Plugin plugin) {
        // Target folder: <plugin_data_folder>/addons/MCEngineChatBot/data/
        File dataFolder = new File(plugin.getDataFolder(), "configs/addons/MCEngineChatBot/data/");

        // If folder exists, skip
        if (dataFolder.exists()) {
            return;
        }

        // Create folder
        if (!dataFolder.mkdirs()) {
            System.err.println("Failed to create directory: " + dataFolder.getAbsolutePath());
            return;
        }

        // Define full JSON structure
        List<Map<String, Object>> data = List.of(
            Map.of(
                "match", Arrays.asList(
                    "What is my name?",
                    "Who am I?"
                ),
                "response", "Your name is {player_name}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "What is my uuid?",
                    "Tell me my player ID"
                ),
                "response", "Your UUID is {player_uuid}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "What time is it on the server?",
                    "Tell me the server time"
                ),
                "response", "The current server time is {time_server}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "What time is it in Bangkok?",
                    "Tell me Bangkok time"
                ),
                "response", "The current time in Bangkok is {time_bangkok}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "What time is it in UTC?",
                    "Tell me the UTC time"
                ),
                "response", "The current UTC time is {time_utc}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "What time is it in GMT+7?",
                    "Tell me time in GMT+07:00"
                ),
                "response", "The current time in GMT+7 is {time_gmt_plus_07_00}."
            ),
            Map.of(
                "match", Arrays.asList(
                    "Tell me all placeholders",
                    "Show me the AI variables"
                ),
                "response", "You can use: {player_name}, {player_uuid}, {time_server}, {time_utc}, {time_bangkok}, etc."
            )
        );

        // Write to JSON file
        File jsonFile = new File(dataFolder, "data.json");
        try (FileWriter writer = new FileWriter(jsonFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(data, writer);
            System.out.println("Created default chatbot data: " + jsonFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to write chatbot data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createConfig(Plugin plugin) {
        // Path: <plugin_data_folder>/addons/MCEngineChatBot/config.yml
        File configFile = new File(plugin.getDataFolder(), "configs/addons/MCEngineChatBot/config.yml");

        if (configFile.exists()) return;

        File configDir = configFile.getParentFile();
        if (!configDir.exists() && !configDir.mkdirs()) {
            System.err.println("Failed to create config directory: " + configDir.getAbsolutePath());
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("token.type", "server");

        try {
            config.save(configFile);
            System.out.println("Created default chatbot config: " + configFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save chatbot config: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
