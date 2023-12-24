package br.dev.brunoxkk0.discordnoises.commands;

import br.dev.brunoxkk0.discordnoises.audio.GuildMusicManager;
import br.dev.brunoxkk0.discordnoises.audio.NoiseTrackScheduler;
import br.dev.brunoxkk0.discordnoises.core.NoiseBot;
import br.dev.brunoxkk0.discordnoises.core.StateHolder;
import br.dev.brunoxkk0.discordnoises.noise.NoisesTypes;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandsListener extends ListenerAdapter {

    private final NoiseBot noiseBot;

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuilds().forEach(
                SlashCommands::registerCommands
        );
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getInteraction().getName()) {

            case "tocar" -> {

                List<SelectOption> options = new ArrayList<>();

                for (NoisesTypes type : NoisesTypes.values()) {
                    options.add(SelectOption.of(type.getFormattedName(), "NOISE_" + type.name().toUpperCase()));
                }

                event.reply("Escolha qual som você deseja ouvir:").addActionRow(
                        StringSelectMenu
                                .create("NOISE_PLAY_MENU")
                                .setPlaceholder("Escolha um valor.")
                                .addOptions(options)
                                .setRequiredRange(1, 1)
                                .build()
                ).queue();
            }

            case "parar" -> {

                if (event.getGuild() == null) {
                    event.reply("Guild não encontrada.").setEphemeral(true).queue();
                    return;
                }

                Guild guild = event.getGuild();

                if (event.getMember() == null) {
                    event.reply("Membro não encontrada.").setEphemeral(true).queue();
                    return;
                }

                Member member = event.getMember();

                GuildMusicManager guildMusicManager = noiseBot.getNoiseBotAudioManager().getMusicManagers().get(
                        guild.getIdLong()
                );

                if (guildMusicManager == null) {
                    event.reply("Nenhum som está tocando neste canal...").setEphemeral(true).queue();
                    return;
                }

                event.reply("Ok...").setEphemeral(true).queue();

                NoiseTrackScheduler noiseTrackScheduler = guildMusicManager.scheduler;

                guild.getAudioManager().closeAudioConnection();
                noiseTrackScheduler.stop(event.getMessageChannel(), guild, member);
                StateHolder.wipe(guild.getIdLong());

            }

        }

    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if (!event.getComponentId().equals("NOISE_PLAY_MENU"))
            return;

        event.getMessageChannel().deleteMessageById(event.getMessageIdLong()).queue();

        SelectOption firstSelected = event.getInteraction().getSelectedOptions()
                .stream()
                .findFirst()
                .orElse(null);

        if (firstSelected != null) {

            if (firstSelected.getValue().startsWith("NOISE_")) {

                NoisesTypes noisesType = NoisesTypes.valueOf(firstSelected.getValue().substring(6));

                String path = noiseBot.getConfigurationProvider().getBasePath() + noisesType.getPath();
                File file = new File(path);

                if (file.isFile()) {
                    StateHolder.update(event.getGuild(), noisesType);
                    noiseBot.getNoiseBotAudioManager().loadAndPlay(event.getChannel().asTextChannel(), path, event.getMember());
                    return;
                }

            }

        }

        event.reply("Erro ao tocar...").queue();
    }
}
