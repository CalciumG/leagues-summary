package com.leaguessummary;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.callback.ClientThread;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

@Slf4j
@PluginDescriptor(
    name = "Leagues Summary",
    description = "Tracks and displays Leagues progress",
    tags = {"leagues", "trailblazer", "summary"}
)
public class LeaguesSummaryPlugin extends Plugin {
    private static final int LEAGUES_INTERFACE_ID = 529;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    
    private Map<String, String> leagueStats = new HashMap<>();
    private boolean hasLoggedStats = false;
    private boolean hasSentStats = false;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LeaguesSummaryConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private LeaguesSummaryPanel panel;

    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception {
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/panel_icon.png");
        navButton = NavigationButton.builder()
            .tooltip("Leagues Summary")
            .icon(icon)
            .priority(5)
            .panel(panel)
            .build();
        
        clientToolbar.addNavigation(navButton);
        log.info("Leagues Summary started!");
    }

    @Override
    protected void shutDown() throws Exception {
        clientToolbar.removeNavigation(navButton);
        log.info("Leagues Summary stopped!");
    }

    private void sendStatsToEndpoint() {
        if (leagueStats.isEmpty() || hasSentStats) {
            return;
        }

        // Add player name to stats
        String playerName = client.getLocalPlayer().getName();
        leagueStats.put("playerName", playerName);

        // Create JSON payload
        String json = gson.toJson(leagueStats);
        
        // Build request
        Request request = new Request.Builder()
            .url(config.apiEndpoint())
            .post(RequestBody.create(JSON, json))  // Fixed parameter order
            .build();

        // Send request asynchronously
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("Failed to send stats to endpoint", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    log.error("Unexpected response code: " + response);
                } else {
                    log.debug("Successfully sent stats to endpoint");
                    hasSentStats = true;
                }
                response.close();
            }
        });
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event) {
        if (event.getGroupId() != LEAGUES_INTERFACE_ID) {
            return;
        }

        clientThread.invoke(() -> {
            Widget leaguesInterface = client.getWidget(LEAGUES_INTERFACE_ID, 0);
            if (leaguesInterface != null && !leaguesInterface.isHidden()) {
                updateStats();
            }
        });
    }

    private void updateStats() {
        leagueStats.clear();
        hasSentStats = false;  // Reset flag when gathering new stats
        Widget parent = client.getWidget(LEAGUES_INTERFACE_ID, 23);
        
        if (parent != null && parent.getDynamicChildren() != null) {
            Widget[] children = parent.getDynamicChildren();
            String currentKey = null;
            
            for (Widget child : children) {
                if (child == null || child.getText() == null) continue;
                
                String text = child.getText().trim();
                if (text.isEmpty()) continue;

                if (text.matches(".*\\d.*")) {
                    if (currentKey != null) {
                        leagueStats.put(currentKey, text);
                    }
                } else {
                    currentKey = text;
                }
            }
        }

        // Also get points and tasks from specific widgets
        Widget pointsWidget = client.getWidget(LEAGUES_INTERFACE_ID, 13);
        if (pointsWidget != null && pointsWidget.getDynamicChildren() != null) {
            for (Widget child : pointsWidget.getDynamicChildren()) {
                if (child != null && child.getText() != null) {
                    String text = child.getText().trim();
                    if (text.matches(".*\\d.*")) {
                        leagueStats.put("Points", text);
                    }
                }
            }
        }

        // Log stats once
        if (!hasLoggedStats && !leagueStats.isEmpty()) {
            log.info("=== League Stats ===");
            leagueStats.forEach((key, value) -> log.info("{}: {}", key, value));
            log.info("==================");
            hasLoggedStats = true;
        }

        // Update panel and send stats
        if (!leagueStats.isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                panel.updateStats(leagueStats);
                sendStatsToEndpoint();
            });
        }
    }

    @Provides
    LeaguesSummaryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(LeaguesSummaryConfig.class);
    }
}