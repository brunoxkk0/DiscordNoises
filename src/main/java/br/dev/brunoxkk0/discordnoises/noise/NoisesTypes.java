package br.dev.brunoxkk0.discordnoises.noise;

import lombok.Getter;

import java.net.URISyntaxException;
import java.nio.file.Path;

public enum NoisesTypes {

    BROWN(      "/noises/brown_noise.mp3",  "\uD83D\uDFEB   Marrom"),
    RAIN(       "/noises/rain.mp3",         "\uD83C\uDF27️   Chuva"),
    WATER(      "/noises/water.mp3",        "\uD83C\uDF0A   Água"),
    WATERFALL(  "/noises/waterfall.mp3",    "\uD83D\uDCA6   Cachoeira"),
    WIND(       "/noises/wind.mp3",         "\uD83C\uDF2C   Vento");

    private final String path;
    @Getter
    private final String formattedName;

    NoisesTypes(String path, String formattedName){
        this.path = path;
        this.formattedName = formattedName;
    }

    public String getFullPath(){
        try{
            return Path.of(NoisesTypes.class.getResource(path).toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException ignored) {}
        return null;
    }

}
