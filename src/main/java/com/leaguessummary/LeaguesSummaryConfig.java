package com.leaguessummary;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("leaguessummary")
public interface LeaguesSummaryConfig extends Config
{
	@ConfigItem(
		keyName = "apiEndpoint",
		name = "API Endpoint",
		description = "The endpoint URL where league stats will be sent",
		position = 1
	)
	default String apiEndpoint()
	{
		return "http://localhost:3000/api/leagues";
	}
}
