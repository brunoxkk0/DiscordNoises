package br.dev.brunoxkk0.discordnoises.core;

import br.dev.brunoxkk0.discordnoises.noise.NoisesTypes;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class StateHolder {

    private static final HashMap<Long, NoisesTypes> CURRENT_NOISE_TYPE = new LinkedHashMap<>();

    public static void update(Long guildId, NoisesTypes currentNoise){
        CURRENT_NOISE_TYPE.put(guildId, currentNoise);
    }

    public static void update(Guild guild, NoisesTypes currentNoise){
        update(guild.getIdLong(), currentNoise);
    }

    public static NoisesTypes currentNoise(Long guildId){
        return CURRENT_NOISE_TYPE.get(guildId);
    }

    public static void wipe(Long guildId){
        CURRENT_NOISE_TYPE.remove(guildId);
    }

    public static NoisesTypes currentNoise(Guild guild){
        return currentNoise(guild.getIdLong());
    }

}
