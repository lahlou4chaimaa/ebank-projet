package org.lahlou.net.ebankbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class EbankAIAgent {

    private final ChatClient chatClient;

    public EbankAIAgent(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory, ToolCallbackProvider tools) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Vous êtes un assistant qui se charge de répondre aux questions
                        de l'utilisateur à propos des clients et des comptes bancaires, 
                        en fonction du contexte fourni. Si aucun contexte n'est fourni, 
                        répond JE NE SAIS PAS
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                // 1. Correction du warning : on utilise defaultTools() à la place
                .defaultTools(tools)
                .build();
    }

    public String chat(Prompt prompt) {
        return chatClient.prompt(prompt)
                // 2. Correction de l'erreur 500 : on définit l'ID de conversation
                .advisors(a -> a.param("chat_memory_conversation_id", "session-utilisateur-1"))
                .call()
                .content();
    }

    public Flux<String> chatStream(Prompt prompt) {
        return chatClient.prompt(prompt)
                // 2. Correction de l'erreur 500 : on définit l'ID de conversation
                .advisors(a -> a.param("chat_memory_conversation_id", "session-utilisateur-1"))
                .stream()
                .content();
    }
}