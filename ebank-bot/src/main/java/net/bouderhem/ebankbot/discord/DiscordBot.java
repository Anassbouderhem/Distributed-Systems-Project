package net.bouderhem.ebankbot.discord;

import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import jakarta.servlet.http.HttpSession;
import net.bouderhem.ebankbot.agents.EbankAIAgent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@DiscordController
public class DiscordBot {
    private EbankAIAgent ebankAIAgent;

    public DiscordBot(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }
    @DiscordMapping
    private void perform(MessageReceivedEvent event){
        System.out.println(event);
        if (event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String conversationId = event.getAuthor().getId();
        String response = ebankAIAgent.chat(query, conversationId);
        event.getChannel().sendMessage(response).queue();
    }
}
