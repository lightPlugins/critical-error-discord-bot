package io.lightplugins.crit.modules.profiles.lookup;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lightplugins.crit.master.LightMaster;
import io.lightplugins.crit.util.LightPrinter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TwitchAPI {

    private final String CLIENT_ID = LightMaster.instance.getConfig().get("TWITCH_CLIENT_ID");
    private final String CLIENT_SECRET = LightMaster.instance.getConfig().get("TWITCH_CLIENT_TOKEN");
    private final String authToken;
    @Getter
    private String profileImageUrl;
    @Getter
    private String streamTitle;

    public TwitchAPI() {
        this.authToken = getOAuthToken();
    }

    public boolean isUserLive(String twitchChannelUrl) {
        String userName = twitchChannelUrl.substring(twitchChannelUrl.lastIndexOf("/") + 1);
        String urlString = "https://api.twitch.tv/helix/streams?user_login=" + userName;

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", CLIENT_ID);
            connection.setRequestProperty("Authorization", "Bearer " + authToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                if (!jsonResponse.getAsJsonArray("data").isEmpty()) {
                    JsonObject streamData = jsonResponse.getAsJsonArray("data").get(0).getAsJsonObject();
                    this.streamTitle = streamData.get("title").getAsString();

                    // Fetch user information to get profile image URL
                    HttpURLConnection userConnection = getUrlConnection(userName);

                    int userResponseCode = userConnection.getResponseCode();
                    if (userResponseCode == 200) {
                        InputStreamReader userReader = new InputStreamReader(userConnection.getInputStream());
                        JsonObject userJsonResponse = JsonParser.parseReader(userReader).getAsJsonObject();
                        if (!userJsonResponse.getAsJsonArray("data").isEmpty()) {
                            JsonObject userData = userJsonResponse.getAsJsonArray("data").get(0).getAsJsonObject();
                            this.profileImageUrl = userData.get("profile_image_url").getAsString();
                        }
                    } else {
                        LightPrinter.printError("Error while fetching user profile: " + userResponseCode);
                    }

                    return true;
                }
            } else {
                LightPrinter.printError("Error while checking user live status: " + responseCode);
                InputStreamReader reader = new InputStreamReader(connection.getErrorStream());
                JsonObject errorResponse = JsonParser.parseReader(reader).getAsJsonObject();
                LightPrinter.printError("Error details: " + errorResponse);
            }
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

    private @NotNull HttpURLConnection getUrlConnection(String userName) throws URISyntaxException, IOException {
        String userUrlString = "https://api.twitch.tv/helix/users?login=" + userName;
        URI userUri = new URI(userUrlString);
        URL userUrl = userUri.toURL();
        HttpURLConnection userConnection = (HttpURLConnection) userUrl.openConnection();
        userConnection.setRequestMethod("GET");
        userConnection.setRequestProperty("Client-ID", CLIENT_ID);
        userConnection.setRequestProperty("Authorization", "Bearer " + authToken);
        return userConnection;
    }

    private String getOAuthToken() {
        String urlString = "https://id.twitch.tv/oauth2/token";
        String params = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&grant_type=client_credentials";

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = params.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                return jsonResponse.get("access_token").getAsString();
            } else {
                LightPrinter.printError("Error while getting oAuth token: " + responseCode);
                InputStreamReader reader = new InputStreamReader(connection.getErrorStream());
                JsonObject errorResponse = JsonParser.parseReader(reader).getAsJsonObject();
                LightPrinter.printError("Error details: " + errorResponse);
            }
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}