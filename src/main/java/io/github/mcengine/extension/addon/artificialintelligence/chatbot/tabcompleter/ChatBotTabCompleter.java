package io.github.mcengine.extension.addon.artificialintelligence.chatbot.tabcompleter;

import io.github.mcengine.api.artificialintelligence.util.MCEngineArtificialIntelligenceApiUtilAi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Tab completer for the /chatbot command.
 */
public class ChatBotTabCompleter implements TabCompleter {

    /**
     * Provides tab completion for /chatbot command.
     *
     * @param sender  The sender of the command.
     * @param command The command being executed.
     * @param label   The alias of the command used.
     * @param args    The arguments passed with the command.
     * @return A list of suggestions.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        Map<String, Map<String, ?>> models = MCEngineArtificialIntelligenceApiUtilAi.getAllModels();

        if (args.length == 1) {
            // Suggest available platforms
            List<String> platforms = new ArrayList<>(models.keySet());
            Collections.sort(platforms);
            return platforms;
        }

        if (args.length == 2) {
            // Suggest models for the given platform
            String platform = args[0];
            if (!models.containsKey(platform)) return Collections.emptyList();

            List<String> modelNames = new ArrayList<>(models.get(platform).keySet());
            Collections.sort(modelNames);
            return modelNames;
        }

        return Collections.emptyList();
    }
}
