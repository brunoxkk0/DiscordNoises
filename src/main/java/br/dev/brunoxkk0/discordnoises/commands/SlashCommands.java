package br.dev.brunoxkk0.discordnoises.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class SlashCommands {

    public static void registerCommands(Guild guild){
        guild.updateCommands().addCommands(
                Commands.slash("tocar", "Comando para começar a tocar algum som."),
                Commands.slash("parar", "Comando para parar de tocar.")
        ).queue();
    }

}
