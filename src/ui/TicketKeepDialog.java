package ui;

import model.cards.DestinationTicketCard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal shown after drawing destination tickets.
 * Player sees all drawn tickets, checks at least {@code minKeep} to keep.
 */
public class TicketKeepDialog extends JDialog {

    /**
     * @param parent     parent frame
     * @param drawn      the tickets that were drawn
     * @param minKeep    minimum number that must be kept
     * @param onConfirm  called with the kept tickets list
     */
    public TicketKeepDialog(Frame parent, List<DestinationTicketCard> drawn,
                            int minKeep, Consumer<List<DestinationTicketCard>> onConfirm) {
        super(parent, "Choose Destination Tickets", true);
        getContentPane().setBackground(UiTheme.PANEL_BG);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Keep at least " + minKeep + " ticket(s)");
        title.setFont(UiTheme.display(14));
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        JPanel ticketsPanel = new JPanel();
        ticketsPanel.setOpaque(false);
        ticketsPanel.setLayout(new BoxLayout(ticketsPanel, BoxLayout.Y_AXIS));

        List<JCheckBox> boxes = new ArrayList<>();
        for (DestinationTicketCard t : drawn) {
            JCheckBox cb = new JCheckBox(t.getCityA() + " → " + t.getCityB()
                + "  (" + t.getPointValue() + " pts)");
            cb.setFont(UiTheme.body(12));
            cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setSelected(true);
            boxes.add(cb);
            ticketsPanel.add(cb);
        }
        add(ticketsPanel, BorderLayout.CENTER);

        JButton confirm = new JButton("Confirm");
        confirm.setFont(UiTheme.display(13));
        confirm.setBackground(UiTheme.GOLD);
        confirm.setForeground(UiTheme.INK);
        confirm.addActionListener(e -> {
            List<DestinationTicketCard> kept = new ArrayList<>();
            for (int i = 0; i < boxes.size(); i++) {
                if (boxes.get(i).isSelected()) kept.add(drawn.get(i));
            }
            if (kept.size() < minKeep) {
                JOptionPane.showMessageDialog(this,
                    "You must keep at least " + minKeep + " ticket(s).",
                    "Too few selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            onConfirm.accept(kept);
            dispose();
        });
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setOpaque(false);
        btns.add(confirm);
        add(btns, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }
}
