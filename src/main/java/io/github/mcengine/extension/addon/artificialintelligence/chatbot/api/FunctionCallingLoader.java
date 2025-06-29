package io.github.mcengine.extension.addon.artificialintelligence.chatbot.api;

import io.github.mcengine.api.mcengine.extension.addon.MCEngineAddOnLogger;
import io.github.mcengine.extension.addon.artificialintelligence.chatbot.api.json.FunctionCallingJson;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static io.github.mcengine.extension.addon.artificialintelligence.chatbot.api.util.FunctionCallingLoaderUtilTime.*;

/**
 * Loads and handles matching of function calling rules for the MCEngineChatBot plugin.
 * Supports placeholder replacement and time zone formatting in responses.
 */
public class FunctionCallingLoader {

    private final List<FunctionRule> mergedRules = new ArrayList<>();

    /**
     * Constructs the loader and loads rules from all `.json` files in the configured directory.
     * Logs the number of rules loaded.
     *
     * @param plugin The plugin instance used for locating the data folder and logging.
     */
    public FunctionCallingLoader(Plugin plugin, MCEngineAddOnLogger logger) {
        IFunctionCallingLoader loader = new FunctionCallingJson(
                new java.io.File(plugin.getDataFolder(), "configs/addons/MCEngineChatBot/data/")
        );
        mergedRules.addAll(loader.loadFunctionRules());

        logger.info("Loaded " + mergedRules.size() + " function rules.");
    }

    public static void check(MCEngineAddOnLogger logger) {
        logger.info("Class: FunctionCallingLoader is loadded.");
    }

    /**
     * Matches the input string against known function rules for the given player.
     * Performs case-insensitive fuzzy matching and applies dynamic placeholders.
     *
     * @param player The player providing the input (used for placeholder replacement).
     * @param input  The user-provided input string to match against.
     * @return A list of resolved responses from matched rules.
     */
    public List<String> match(Player player, String input) {
        List<String> results = new ArrayList<>();
        String lowerInput = input.toLowerCase().trim();

        for (FunctionRule rule : mergedRules) {
            for (String pattern : rule.match) {
                String lowerPattern = pattern.toLowerCase();
                if (lowerInput.contains(lowerPattern) || lowerPattern.contains(lowerInput)) {
                    String resolved = applyPlaceholders(rule.response, player);
                    results.add(resolved);
                    break;
                }
            }
        }

        return results;
    }

    /**
     * Applies placeholders to a rule's response string based on the provided player's data and various time zones.
     *
     * @param response The raw response string containing placeholders.
     * @param player   The player whose data will be used for placeholder replacement.
     * @return The formatted response with all placeholders replaced.
     */
    private String applyPlaceholders(String response, Player player) {
        response = response
                // Player info
                .replace("{player_name}", player.getName())
                .replace("{player_uuid}", player.getUniqueId().toString())
                .replace("{player_uuid_short}", player.getUniqueId().toString().split("-")[0])
                .replace("{player_displayname}", player.getDisplayName())
                .replace("{player_ip}", player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "unknown")
                .replace("{player_gamemode}", player.getGameMode().name())
                .replace("{player_world}", player.getWorld().getName())
                .replace("{player_location}", String.format("X: %.1f, Y: %.1f, Z: %.1f",
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ()))
                .replace("{player_health}", String.valueOf(player.getHealth()))
                .replace("{player_max_health}", String.valueOf(player.getMaxHealth()))
                .replace("{player_food_level}", String.valueOf(player.getFoodLevel()))
                .replace("{player_exp_level}", String.valueOf(player.getLevel()))

                // Static time zones
                .replace("{time_server}", getFormattedTime(TimeZone.getDefault()))
                .replace("{time_utc}", getFormattedTime(TimeZone.getTimeZone("UTC")))
                .replace("{time_gmt}", getFormattedTime(TimeZone.getTimeZone("GMT")));

        // Named time zones
        Map<String, String> namedZones = Map.ofEntries(
                Map.entry("{time_new_york}", getFormattedTime("America/New_York")),
                Map.entry("{time_london}", getFormattedTime("Europe/London")),
                Map.entry("{time_tokyo}", getFormattedTime("Asia/Tokyo")),
                Map.entry("{time_bangkok}", getFormattedTime("Asia/Bangkok")),
                Map.entry("{time_sydney}", getFormattedTime("Australia/Sydney")),
                Map.entry("{time_paris}", getFormattedTime("Europe/Paris")),
                Map.entry("{time_berlin}", getFormattedTime("Europe/Berlin")),
                Map.entry("{time_singapore}", getFormattedTime("Asia/Singapore")),
                Map.entry("{time_los_angeles}", getFormattedTime("America/Los_Angeles")),
                Map.entry("{time_toronto}", getFormattedTime("America/Toronto"))
        );

        for (Map.Entry<String, String> entry : namedZones.entrySet()) {
            response = response.replace(entry.getKey(), entry.getValue());
        }

        // UTC/GMT offsets from -12:00 to +14:00
        for (int hour = -12; hour <= 14; hour++) {
            for (int min : new int[]{0, 30, 45}) {
                String utcLabel = getZoneLabel("utc", hour, min);
                String gmtLabel = getZoneLabel("gmt", hour, min);
                TimeZone tz = TimeZone.getTimeZone(String.format("GMT%+03d:%02d", hour, min));
                String time = getFormattedTime(tz);
                response = response.replace(utcLabel, time);
                response = response.replace(gmtLabel, time);
            }
        }

        return response;
    }
}
