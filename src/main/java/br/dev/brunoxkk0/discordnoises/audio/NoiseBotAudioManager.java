package br.dev.brunoxkk0.discordnoises.audio;

import br.dev.brunoxkk0.discordnoises.core.NoiseBot;
import br.dev.brunoxkk0.discordnoises.core.StateHolder;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class NoiseBotAudioManager extends ListenerAdapter {

    @Getter
    private final Map<Long, GuildMusicManager> musicManagers;

    @Getter
    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    @Getter
    AudioPlayer audioPlayer;


    public NoiseBotAudioManager() {
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        this.musicManagers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final TextChannel channel, final String trackUrl, final Member source) {

        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {

                channel.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor("NoiseBot")
                                .setColor(new Color(16, 133, 163))
                                .setTitle("Começando a tocar o seguinte som")
                                .setThumbnail("https://cdn.discordapp.com/avatars/1087399282908340297/48cfec413fcb8035250f5655aea2668f.png?size=1024")
                                .setDescription("\uD83C\uDFA7 _Tocando agora..._ **" + StateHolder.currentNoise(channel.getGuild()).getFormattedName() + "**")
                                .setFooter("Noise Bot " + NoiseBot.version)
                                .build()
                ).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS, null, ErrorResponseException.ignore(ErrorResponse.MISSING_ACCESS)));

                play(channel.getGuild(), musicManager, track, source);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setAuthor("NoiseBot")
                                .setColor(new Color(163, 16, 16))
                                .setTitle("Falha ao tocar o som solicitado")
                                .setThumbnail("https://cdn.discordapp.com/avatars/1087399282908340297/48cfec413fcb8035250f5655aea2668f.png?size=1024")
                                .setDescription("\uD83C\uDFA7 _Tentei tocar..._ **" + StateHolder.currentNoise(channel.getGuild()).getFormattedName() + "**")
                                .setFooter("Noise Bot " + NoiseBot.version)
                                .build()
                ).queue(message ->
                        message.delete().queueAfter(10, TimeUnit.SECONDS, null, ErrorResponseException.ignore(ErrorResponse.MISSING_ACCESS))
                );

            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member) {

        if (member.getVoiceState().inAudioChannel()) {
            AudioChannel audioChannel = member.getVoiceState().getChannel().asVoiceChannel();
            guild.getAudioManager().openAudioConnection(audioChannel);
        } else {
            connectToFirstVoiceChannel(guild.getAudioManager());
        }

        musicManager.scheduler.queue(track);
    }


}
