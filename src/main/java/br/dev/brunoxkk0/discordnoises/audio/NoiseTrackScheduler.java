package br.dev.brunoxkk0.discordnoises.audio;

import br.dev.brunoxkk0.discordnoises.core.NoiseBot;
import br.dev.brunoxkk0.discordnoises.core.StateHolder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class NoiseTrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    /**
     * @param player The audio player this scheduler uses
     */
    public NoiseTrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, false)) {
            queue.offer(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext)
            player.playTrack(track.makeClone());
    }

    public void stop(MessageChannel channel, Guild guild, Member member) {
        player.stopTrack();
        channel.sendMessageEmbeds(
                new EmbedBuilder()
                        .setAuthor("NoiseBot")
                        .setColor(new Color(52, 57, 59))
                        .setTitle("Parando de tocar")
                        .setThumbnail("https://cdn.discordapp.com/avatars/1087399282908340297/48cfec413fcb8035250f5655aea2668f.png?size=1024")
                        .setDescription("\uD83C\uDFA7 _Estava tocando ..._ **" + StateHolder.currentNoise(guild).getFormattedName() + "**\n")
                        .setAuthor("Solicitado por " + member.getUser().getName(), null, member.getEffectiveAvatarUrl())
                        .setFooter("Noise Bot " + NoiseBot.version)
                        .build()
        ).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS, null, ErrorResponseException.ignore(ErrorResponse.MISSING_ACCESS)));

    }
}