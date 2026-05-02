package ui;

import model.board.Route;
import model.cards.TransportationCard;
import model.player.Player;
import strategy.RouteScoreStrategy;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Modal that previews a route claim and lets the player select which cards to spend.
 * Mirrors the ClaimModal from app.jsx.
 */
public class ClaimDialog extends JDialog {

    private List<TransportationCard> selectedCards;

    /**
     * @param parent  parent frame
     * @param route   the route the player wants to claim
     * @param player  the acting player
     * @param onConfirm called with the selected cards when the player confirms
     */
    public ClaimDialog(Frame parent, Route route, Player player,
                       Consumer<List<TransportationCard>> onConfirm) {
        super(parent, "Claim Route", true);
        selectedCards = new ArrayList<>();

        setUndecorated(false);
        setBackground(UiTheme.PANEL_BG);
        getContentPane().setBackground(UiTheme.PANEL_BG);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Title
        JLabel title = new JLabel(route.getCityA() + " → " + route.getCityB());
        title.setFont(UiTheme.display(16));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        // Centre: cost cells + card selector
        JPanel centre = new JPanel();
        centre.setOpaque(false);
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));

        centre.add(makeCostCells(route));
        centre.add(Box.createVerticalStrut(10));
        centre.add(makeCostInfo(route, player));
        centre.add(Box.createVerticalStrut(10));
        centre.add(makeCardSelector(route, player));
        add(centre, BorderLayout.CENTER);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton cancel = new JButton("Cancel");
        cancel.setFont(UiTheme.body(12));
        cancel.setForeground(Color.WHITE);
        cancel.setBackground(UiTheme.PANEL_BORDER);
        cancel.addActionListener(e -> dispose());

        JButton confirm = new JButton("Pay & Claim");
        confirm.setFont(UiTheme.display(13));
        confirm.setForeground(UiTheme.INK);
        confirm.setBackground(UiTheme.GOLD);
        confirm.addActionListener(e -> {
            if (!selectedCards.isEmpty()) {
                onConfirm.accept(new ArrayList<>(selectedCards));
                dispose();
            }
        });

        btns.add(cancel);
        btns.add(confirm);
        add(btns, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel makeCostCells(Route route) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        Color fill = UiTheme.routeColour(route.getRequiredColour());
        for (int i = 0; i < route.getLength(); i++) {
            JPanel cell = new JPanel();
            cell.setPreferredSize(new Dimension(28, 18));
            cell.setBackground(fill);
            cell.setBorder(BorderFactory.createLineBorder(UiTheme.INK_DIM, 1, true));
            row.add(cell);
        }
        return row;
    }

    private JLabel makeCostInfo(Route route, Player player) {
        int pts = new RouteScoreStrategy().scoreRoute(route);
        String colourName = route.getRequiredColour() != null
            ? route.getRequiredColour().displayName() : "Any colour";
        long handCount = player.getHand().stream()
            .filter(c -> route.getRequiredColour() == null || c.matches(route.getRequiredColour()))
            .count();
        long wilds = player.getHand().stream().filter(TransportationCard::isLocomotive).count();
        String text = "Pay " + route.getLength() + "× " + colourName
            + "  ·  You hold " + handCount + " matching + " + wilds + " wild  ·  +" + pts + " pts";
        JLabel lbl = new JLabel(text);
        lbl.setFont(UiTheme.mono(10));
        lbl.setForeground(new Color(0xAA, 0xBB, 0xCC));
        return lbl;
    }

    private JPanel makeCardSelector(Route route, Player player) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        panel.setOpaque(false);

        // Group hand by colour
        Map<String, List<TransportationCard>> groups = new TreeMap<>();
        for (TransportationCard c : player.getHand()) {
            if (c.matches(route.getRequiredColour())) {
                groups.computeIfAbsent(c.getColour().displayName(), k -> new ArrayList<>()).add(c);
            }
        }

        ButtonGroup bg = new ButtonGroup();
        for (Map.Entry<String, List<TransportationCard>> entry : groups.entrySet()) {
            List<TransportationCard> group = entry.getValue();
            if (group.size() < route.getLength()) continue;
            JToggleButton btn = new JToggleButton(
                entry.getKey() + " ×" + group.size());
            btn.setFont(UiTheme.mono(10));
            btn.setForeground(Color.WHITE);
            btn.setBackground(UiTheme.PANEL_BORDER);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> {
                if (btn.isSelected()) {
                    selectedCards = group.subList(0, route.getLength());
                } else {
                    selectedCards.clear();
                }
            });
            bg.add(btn);
            panel.add(btn);
        }
        if (panel.getComponentCount() == 0) {
            JLabel none = new JLabel("Insufficient matching cards.");
            none.setFont(UiTheme.mono(10));
            none.setForeground(UiTheme.ACCENT_RED);
            panel.add(none);
        }
        return panel;
    }
}
