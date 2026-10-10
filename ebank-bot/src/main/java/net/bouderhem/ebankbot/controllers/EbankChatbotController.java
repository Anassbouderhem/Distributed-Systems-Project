package net.bouderhem.ebankbot.controllers;

import jakarta.servlet.http.HttpSession;
import net.bouderhem.ebankbot.agents.EbankAIAgent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbankChatbotController {
    private EbankAIAgent ebankAIAgent;

    public EbankChatbotController(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }

    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(@RequestParam(name = "query", defaultValue = "Bonjour") String query, HttpSession session){
        return ebankAIAgent.chat(query, session);
    }
}
