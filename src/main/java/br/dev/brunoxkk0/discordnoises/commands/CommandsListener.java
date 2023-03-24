package br.dev.brunoxkk0.discordnoises.commands;

import br.dev.brunoxkk0.discordnoises.audio.GuildMusicManager;
import br.dev.brunoxkk0.discordnoises.audio.NoiseTrackScheduler;
import br.dev.brunoxkk0.discordnoises.core.NoiseBot;
import br.dev.brunoxkk0.discordnoises.core.StateHolder;
import br.dev.brunoxkk0.discordnoises.noise.NoisesTypes;
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
public class CommandsListener extends ListenerAdapter {

    private final NoiseBot noiseBot;

    public CommandsListener(NoiseBot bot){
        this.noiseBot = bot;
    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().getGuilds().forEach(
                SlashCommands::registerCommands
        );
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        switch (event.getInteraction().getName()){
            case "tocar" -> {
                List<SelectOption> options = new ArrayList<>();

                for(NoisesTypes type : NoisesTypes.values()){
                    options.add(SelectOption.of(type.getFormattedName(), "NOISE_" + type.name().toUpperCase()));
                }

                event.reply("Escolha qual som você deseja ouvir:").addActionRow(
                        StringSelectMenu
                                .create("NOISE_PLAY_MENU")
                                .setPlaceholder("Escolha um valor.")
                                .addOptions(options)
                                .setRequiredRange(1,1)
                                .build()
                ).queue();
            }

            case "parar" -> {

                GuildMusicManager guildMusicManager = noiseBot.getNoiseBotAudioManager().getMusicManagers().get(event.getGuild().getIdLong());

                if(guildMusicManager == null){
                    event.reply("Nenum som está tocando neste canal...").setEphemeral(true).queue();
                    return;
                }

                event.reply("Ok...").setEphemeral(true).queue();

                NoiseTrackScheduler noiseTrackScheduler =  guildMusicManager.scheduler;

                if(noiseTrackScheduler != null){
                    noiseTrackScheduler.stop(event.getMessageChannel(), event.getGuild(), event.getMember());
                }

            }
        }

    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {

        if(!event.getComponentId().equals("NOISE_PLAY_MENU"))
            return;

        event.getMessageChannel().deleteMessageById(event.getMessageIdLong()).queue();

        SelectOption firstSelected = event.getInteraction().getSelectedOptions().stream().findFirst().orElse(null);

        if(firstSelected != null){
            if(firstSelected.getValue().startsWith("NOISE_")){
                NoisesTypes noisesType = NoisesTypes.valueOf(firstSelected.getValue().substring(6));

                String path = noiseBot.getConfigurationProvider().getBasePath() + noisesType.getFullPath();

                if(path != null && new File(path).isFile()){
                    StateHolder.update(event.getGuild(), noisesType);
                    noiseBot.getNoiseBotAudioManager().loadAndPlay(event.getChannel().asTextChannel(), path);
                    return;
                }
            }
        }

        event.reply("Erro ao tocar...").queue();
    }
}
