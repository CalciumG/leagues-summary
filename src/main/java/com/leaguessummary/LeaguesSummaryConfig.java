package com.leaguessummary;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("leaguessummary")
public interface LeaguesSummaryConfig extends Config
{
	@ConfigItem(
		keyName = "greeting",
		name = "Welcome Greeting",
		description = "The message to show to the user when they login"
	)
	default String greeting()
	{
		return "Welcome to Leagues Summary!";
	}

	@ConfigItem(
		keyName = "showTaskProgress",
		name = "Show Task Progress",
		description = "Show progress towards league tasks"
	)
	default boolean showTaskProgress()
	{
		return true;
	}
}
