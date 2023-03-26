package com.atm.frontend;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

public class GUIButton extends JButton {
    private Shape shape;

    public GUIButton() {
        super();
        // Set button size
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setContentAreaFilled(false);
    }

    protected void paintComponent(Graphics g) {
        // Set button color
        if (getModel().isArmed()) {
             g.setColor(Color.lightGray);
        } else {
             g.setColor(getBackground());
        }
        g.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        // Set button border color
        g.setColor(getForeground());
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
    }
    
    public boolean contains(int x, int y) {
        // Set button shape
        if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 15, 15);
        }
        return shape.contains(x, y);
    }
}
