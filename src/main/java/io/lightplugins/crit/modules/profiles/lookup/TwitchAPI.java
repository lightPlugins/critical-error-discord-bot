package io.lightplugins.crit.modules.profiles.lookup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lightplugins.crit.master.LightMaster;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchAPI {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final String CLIENT_ID = LightMaster.instance.getConfig().get("TWITCH_CLIENT_ID");
    private final String AUTH_TOKEN = LightMaster.instance.getConfig().get("TWITCH_CLIENT_TOKEN");
    private final String twitchChannelName;

    public TwitchAPI(String twitchChannelName) {
        // Scheduler is not started automatically
        this.twitchChannelName = twitchChannelName;
    }

    public void startScheduler(int period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(this::checkUserLiveStatus, 0, period, timeUnit);
    }

    public void stopScheduler() {
        scheduler.shutdown();
    }

    public void checkUserLiveStatus() {
        String twitchChannelUrl = "https://www.twitch.tv/" + twitchChannelName;
        if (isUserLive(twitchChannelUrl)) {
            System.out.println("User is live on Twitch!");
        } else {
            System.out.println("User is not live on Twitch.");
        }
    }

    private boolean isUserLive(String twitchChannelUrl) {
        String userName = twitchChannelUrl.substring(twitchChannelUrl.lastIndexOf("/") + 1);
        String urlString = "https://api.twitch.tv/helix/streams?user_login=" + userName;

        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Client-ID", CLIENT_ID);
            connection.setRequestProperty("Authorization", "Bearer " + AUTH_TOKEN);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonObject jsonResponse = JsonParser.parseReader(reader).getAsJsonObject();
                JsonArray dataArray = jsonResponse.getAsJsonArray("data");
                return !dataArray.isEmpty();
            } else {
                System.out.println("Error: " + responseCode);
            }
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
        }

        return false;
    }

}