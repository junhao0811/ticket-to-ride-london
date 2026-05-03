package ui;

import model.board.Route;
import model.cards.TransportationCard;
import model.cards.TransportationTypes;
import model.player.Player;
import strategy.RouteScoreStrategy;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

        JButton confirm = new JButton("Claim");
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

        // Count non-wild matching cards
        long matching = player.getHand().stream()
            .filter(c -> !c.isLocomotive() && (route.getRequiredColour() == null || c.getColour() == route.getRequiredColour()))
            .count();
        long wilds = player.getHand().stream().filter(TransportationCard::isLocomotive).count();

        String text = "Pay " + route.getLength() + "× " + colourName
            + "  ·  You hold " + matching + " matching + " + wilds + " wild  ·  +" + pts + " pts";
        JLabel lbl = new JLabel(text);
        lbl.setFont(UiTheme.mono(10));
        lbl.setForeground(new Color(0xAA, 0xBB, 0xCC));
        return lbl;
    }

    private JPanel makeCardSelector(Route route, Player player) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        panel.setOpaque(false);

        List<TransportationCard> hand = player.getHand();
        List<TransportationCard> locomotives = hand.stream()
            .filter(TransportationCard::isLocomotive)
            .collect(Collectors.toList());

        // Determine candidate colors that could satisfy this route
        Set<TransportationTypes> candidateColors = new LinkedHashSet<>();
        if (route.getRequiredColour() != null) {
            candidateColors.add(route.getRequiredColour());
        } else {
            // For gray routes, any color present in hand is a candidate
            hand.stream()
                .filter(c -> !c.isLocomotive())
                .map(TransportationCard::getColour)
                .forEach(candidateColors::add);
        }

        ButtonGroup bg = new ButtonGroup();

        for (TransportationTypes color : candidateColors) {
            List<TransportationCard> colorCards = hand.stream()
                .filter(c -> !c.isLocomotive() && c.getColour() == color)
                .collect(Collectors.toList());

            if (colorCards.size() + locomotives.size() >= route.getLength()) {
                // Build selection: use as many color cards as possible, then fill with locomotives
                List<TransportationCard> selection = new ArrayList<>(colorCards);
                if (selection.size() > route.getLength()) {
                    selection = new ArrayList<>(selection.subList(0, route.getLength()));
                } else if (selection.size() < route.getLength()) {
                    int needed = route.getLength() - selection.size();
                    selection.addAll(locomotives.subList(0, needed));
                }

                addOptionButton(panel, bg, color.displayName(), colorCards.size() + locomotives.size(), selection);
            }
        }

        // Also allow "All Locomotives" if enough to cover the route independently
        if (locomotives.size() >= route.getLength()) {
            // Check if we already added a "Locomotive" option (can happen if color == LOCOMOTIVE which shouldn't happen)
            boolean alreadyHasLoco = false;
            for (Component c : panel.getComponents()) {
                if (c instanceof JToggleButton b && b.getText().startsWith("Locomotive")) {
                    alreadyHasLoco = true;
                    break;
                }
            }
            if (!alreadyHasLoco) {
                List<TransportationCard> selection = new ArrayList<>(locomotives.subList(0, route.getLength()));
                addOptionButton(panel, bg, "Locomotive", locomotives.size(), selection);
            }
        }

        if (panel.getComponentCount() == 0) {
            JLabel none = new JLabel("Insufficient matching cards.");
            none.setFont(UiTheme.mono(10));
            none.setForeground(UiTheme.ACCENT_RED);
            panel.add(none);
        }
        return panel;
    }

    private void addOptionButton(JPanel panel, ButtonGroup bg, String label, int count, List<TransportationCard> selection) {
        JToggleButton btn = new JToggleButton(label + " ×" + count);
        btn.setFont(UiTheme.mono(10));
        btn.setForeground(Color.WHITE);
        btn.setBackground(UiTheme.PANEL_BORDER);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            if (btn.isSelected()) {
                selectedCards = selection;
            } else {
                selectedCards = new ArrayList<>();
            }
        });
        bg.add(btn);
        panel.add(btn);
    }
}
