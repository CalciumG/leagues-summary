package com.leaguessummary;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.FontManager;
import java.util.Map;
import java.util.HashMap;

class LeaguesSummaryPanel extends PluginPanel {
    private final JPanel statsPanel = new JPanel();
    private Map<String, JLabel> statLabels = new HashMap<>();

    @Inject
    private LeaguesSummaryPanel() {
        super(false);
        setLayout(new BorderLayout(0, 5));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // Setup stats panel with a grid layout that will expand as needed
        statsPanel.setLayout(new GridLayout(0, 1, 0, 5));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Create a scroll pane for the stats panel
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBackground(ColorScheme.DARK_GRAY_COLOR);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateStats(Map<String, String> stats) {
        statsPanel.removeAll();

        // Sort and display stats
        String[] priorityStats = {"Points", "Tasks", "Rank", "Total level", "Total XP"};
        
        // First add priority stats
        for (String key : priorityStats) {
            if (stats.containsKey(key)) {
                addOrUpdateStat(key, stats.get(key));
                stats.remove(key);
            }
        }

        // Add separator
        JLabel separator = new JLabel("─".repeat(20));
        separator.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        separator.setFont(FontManager.getRunescapeFont());
        statsPanel.add(separator);

        // Then add remaining stats
        stats.forEach(this::addOrUpdateStat);

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void addOrUpdateStat(String key, String value) {
        JLabel label = new JLabel(String.format("%s: %s", key, value));
        label.setFont(FontManager.getRunescapeFont());
        label.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
        statsPanel.add(label);
    }
}