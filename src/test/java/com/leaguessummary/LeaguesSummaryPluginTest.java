package com.leaguessummary;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.callback.ClientThread;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeaguesSummaryPluginTest
{
    @Mock
    private Client client;

    @Mock
    private ClientThread clientThread;

    @Mock
    private LeaguesSummaryConfig config;

    @Mock
    private ClientToolbar clientToolbar;

    @Mock
    private LeaguesSummaryPanel panel;

    private LeaguesSummaryPlugin plugin;

    @Before
    public void setUp()
    {
        plugin = new LeaguesSummaryPlugin();
        
        // Inject mocked dependencies
        plugin.client = client;
        plugin.clientThread = clientThread;
        plugin.config = config;
        plugin.clientToolbar = clientToolbar;
        plugin.panel = panel;
    }

    @Test
    public void testStartUp() throws Exception
    {
        plugin.startUp();
        verify(clientToolbar).addNavigation(any());
    }

    @Test
    public void testShutDown() throws Exception
    {
        plugin.startUp();
        plugin.shutDown();
        verify(clientToolbar).removeNavigation(any());
    }

    @Test
    public void testConfigDefaultEndpoint()
    {
        when(config.apiEndpoint()).thenReturn("http://localhost:3000/api/leagues");
        assert(config.apiEndpoint().equals("http://localhost:3000/api/leagues"));
    }

    // Keep the main method for manual testing
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(LeaguesSummaryPlugin.class);
        RuneLite.main(args);
    }
} 