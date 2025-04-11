package com.example.project;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

public class AccessToken {
    public class CreateToken {

        private static final String CLIENT_ID = "516c5a02c69a42528f49e9816d9fbb00";
        private static final String CLIENT_SECRET = "365f94c53e9644eb8c35d5092ddabd9a";

        private static final SpotifyApi spotifyApi = new SpotifyApi.Builder().setClientId(CLIENT_ID).setClientSecret(CLIENT_SECRET).build();

        public static String accesstoken() throws IOException, ParseException, SpotifyWebApiException {
            ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials().build();

                final ClientCredentials clientCredentials = clientCredentialsRequest.execute();
                spotifyApi.setAccessToken(clientCredentials.getAccessToken());
                return spotifyApi.getAccessToken();
        }
    }
}
