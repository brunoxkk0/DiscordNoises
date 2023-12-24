package br.dev.brunoxkk0.discordnoises.noise;

import lombok.Getter;

@Getter
public enum NoisesTypes {

    BROWN("/noises/brown_noise.mp3", "\uD83D\uDFEB   Marrom"),
    FIRE("/noises/fire.mp3", "\uD83D\uDD25   Fogo"),
    RAIN("/noises/rain.mp3", "\uD83C\uDF27️   Chuva"),
    WATER("/noises/water.mp3", "\uD83C\uDF0A   Água"),
    WATERFALL("/noises/waterfall.mp3", "\uD83D\uDCA6   Cachoeira"),
    WIND("/noises/wind.mp3", "\uD83C\uDF2C   Vento");

    private final String path;

    private final String formattedName;

    NoisesTypes(String path, String formattedName) {
        this.path = path;
        this.formattedName = formattedName;
    }

}
