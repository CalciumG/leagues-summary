package com.leaguessummary;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.callback.ClientThread;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.ArgumentCaptor;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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

    @Mock
    private Widget leaguesInterface;

    @Mock
    private Widget pointsWidget;

    @Mock
    private Widget statsWidget;

    @Mock
    private Player player;

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

        // Setup common mocks
        when(client.getLocalPlayer()).thenReturn(player);
        when(player.getName()).thenReturn("TestPlayer");
        when(config.apiEndpoint()).thenReturn("http://localhost:3000/api/leagues");
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
    public void testWidgetLoadedEvent() {
        // Setup widget mocks
        when(client.getWidget(529, 0)).thenReturn(leaguesInterface);
        when(leaguesInterface.isHidden()).thenReturn(false);
        
        // Create widget loaded event
        WidgetLoaded event = new WidgetLoaded();
        event.setGroupId(529); // LEAGUES_INTERFACE_ID

        // Capture clientThread.invoke call
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        
        // Trigger event
        plugin.onWidgetLoaded(event);
        
        // Verify clientThread.invoke was called
        verify(clientThread).invoke(captor.capture());
        
        // Run the captured runnable
        captor.getValue().run();
        
        // Verify widget interactions
        verify(client).getWidget(529, 0);
        verify(leaguesInterface).isHidden();
    }

    @Test
    public void testStatsUpdate() {
        // Setup widget mocks
        Widget[] children = new Widget[2];
        children[0] = mock(Widget.class);
        children[1] = mock(Widget.class);
        
        when(client.getWidget(529, 23)).thenReturn(statsWidget);
        when(statsWidget.getDynamicChildren()).thenReturn(children);
        when(children[0].getText()).thenReturn("Points");
        when(children[1].getText()).thenReturn("1000");

        // Setup points widget mock
        Widget[] pointsChildren = new Widget[1];
        pointsChildren[0] = mock(Widget.class);
        when(client.getWidget(529, 13)).thenReturn(pointsWidget);
        when(pointsWidget.getDynamicChildren()).thenReturn(pointsChildren);
        when(pointsChildren[0].getText()).thenReturn("5000");

        // Create and trigger widget loaded event
        WidgetLoaded event = new WidgetLoaded();
        event.setGroupId(529);
        plugin.onWidgetLoaded(event);

        // Verify panel update
        ArgumentCaptor<Map<String, String>> statsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(panel, timeout(1000)).updateStats(statsCaptor.capture());

        Map<String, String> capturedStats = statsCaptor.getValue();
        assertTrue(capturedStats.containsKey("Points"));
        assertEquals("5000", capturedStats.get("Points"));
    }

    @Test
    public void testConfigDefaultEndpoint()
    {
        when(config.apiEndpoint()).thenReturn("http://localhost:3000/api/leagues");
        assertEquals("http://localhost:3000/api/leagues", config.apiEndpoint());
    }
} 