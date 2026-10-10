package net.bouderhem.ebankbot.agents;

import jakarta.servlet.http.HttpSession;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
public class EbankAIAgent {
    private ChatClient chatClient;

    public EbankAIAgent(ChatClient.Builder chatClient, ChatMemory chatMemory, ToolCallbackProvider tools){
        this.chatClient = chatClient
                .defaultSystem("""
                        Vous êtes un assistant qui se charge de répondre aux question de l'utilisateur
                        à propos des clients et des comptes bancaires, en fonction du contexte fourni.
                        Si aucun contexte n'est fourni, répond avec JE NE SAIS PAS
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }
    public String chat(Prompt prompt, String conversationId){
        return chatClient.prompt(prompt)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call().content();
    }

    public Flux<String> chatStream(Prompt prompt, String conversationId){
        return chatClient.prompt(prompt)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .stream().content();
    }
}
