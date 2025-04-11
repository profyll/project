package com.example.project;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import org.apache.hc.core5.http.ParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class SpotifyConfig {

    @Bean
    public SpotifyApi spotifyApi() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId("516c5a02c69a42528f49e9816d9fbb00")
                .setClientSecret("365f94c53e9644eb8c35d5092ddabd9a")
                .build();

        try {
            ClientCredentials clientCredentials = spotifyApi.clientCredentials().build().execute();
            spotifyApi.setAccessToken(clientCredentials.getAccessToken());
            System.out.println("Spotify Access Token: " + clientCredentials.getAccessToken());
        } catch (IOException | ParseException | com.wrapper.spotify.exceptions.SpotifyWebApiException e) {
            e.printStackTrace();
        }

        return spotifyApi;
    }
}
