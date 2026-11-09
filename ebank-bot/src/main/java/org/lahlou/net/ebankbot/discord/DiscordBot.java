package org.lahlou.net.ebankbot.discord;

import org.lahlou.net.ebankbot.agents.EbankAIAgent;
import com.zgamelogic.discord.annotations.DiscordController;
import com.zgamelogic.discord.annotations.DiscordMapping;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.ai.chat.prompt.Prompt;

@DiscordController
public class DiscordBot {
    private EbankAIAgent ebankAIagent;

    public DiscordBot(EbankAIAgent ebankAIagent) {
        this.ebankAIagent = ebankAIagent;
    }

    @DiscordMapping
    public void perform(MessageReceivedEvent event) {
        System.out.println(event);
        if (event.getAuthor().isBot()) return;
        String query = event.getMessage().getContentRaw();
        String response = ebankAIagent.chat(new Prompt(query));
        event.getChannel().sendMessage(response).queue();
    }
}
