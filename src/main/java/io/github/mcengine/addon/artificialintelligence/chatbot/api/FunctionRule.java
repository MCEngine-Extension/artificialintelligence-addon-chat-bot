package io.github.mcengine.addon.artificialintelligence.chatbot.api;

import java.util.List;

public class FunctionRule {
    public List<String> match;
    public String response;

    public FunctionRule(List<String> match, String response) {
        this.match = match;
        this.response = response;
    }
}
