package br.dev.brunoxkk0.discordnoises.core;

import br.dev.brunoxkk0.discordnoises.StaticData;
import br.dev.brunoxkk0.discordnoises.audio.NoiseBotAudioManager;
import br.dev.brunoxkk0.discordnoises.commands.CommandsListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.*;

@Component
@Getter
public class NoiseBot {

    public static final Logger logger = LoggerFactory.getLogger(NoiseBot.class);
    public static final String version = StaticData.VERSION;

    @Autowired
    private ConfigurationProvider configurationProvider;

    @Autowired
    private NoiseBotAudioManager noiseBotAudioManager;

    public List<GatewayIntent> gatewayIntents(){
        return Arrays.asList(
                MESSAGE_CONTENT,
                GUILD_MESSAGES,
                GUILD_MEMBERS,
                GUILD_MESSAGE_REACTIONS,
                GUILD_VOICE_STATES,
                GUILD_EMOJIS_AND_STICKERS
        );
    }

    public List<CacheFlag> cacheFlags(){
        return Arrays.asList(EMOJI, VOICE_STATE);
    }

    @Bean
    public JDA create(){
        return JDABuilder
                .create(configurationProvider.getBotToken(), gatewayIntents())
                .enableCache(cacheFlags())
                .setChunkingFilter(ChunkingFilter.NONE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.listening("alguns sons..."))
                .addEventListeners(new CommandsListener(this))
                .build();
    }

}
