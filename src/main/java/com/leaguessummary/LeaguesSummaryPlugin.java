package com.leaguessummary;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Leagues Summary"
)
public class LeaguesSummaryPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private LeaguesSummaryConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Leagues Summary started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Leagues Summary stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Leagues Summary loaded!", null);
		}
	}

	@Provides
	LeaguesSummaryConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LeaguesSummaryConfig.class);
	}
}
