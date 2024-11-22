package com.cs2212.bunbun.system;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

public class CustomSliderUI extends BasicSliderUI {

    public CustomSliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        g2.setColor(Color.GRAY);
        g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
        g2.dispose();
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cy = trackRect.y + (trackRect.height / 2) - 2;
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRoundRect(trackRect.x, cy, trackRect.width, 4, 10, 10);

        int filled = (int) (((float) slider.getValue() / (float) slider.getMaximum()) * trackRect.width);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(trackRect.x, cy, filled, 4, 10, 10);

        g2.dispose();
    }
}
