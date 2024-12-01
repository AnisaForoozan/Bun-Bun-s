package com.cs2212.bunbun.system;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.*;
import javax.swing.*;

import static javax.swing.SwingConstants.CENTER;

public class CustomListDemo extends JPanel {
    ImageIcon[] images;
    String[] petStrings = {"applepie", "burger", "cookies", "frenchfries", "garlicbread", "macncheese", "pancakes", "salmon", "steak", "strawberrycake"};
    /*
     * Despite its use of EmptyBorder, this panel makes a fine content
     * pane because the empty border just increases the panel's size
     * and is "painted" on top of the panel's normal background.  In
     * other words, the JPanel fills its entire background if it's
     * opaque (which it is by default); adding a border doesn't change
     * that.
     */
    public CustomListDemo() {
        super(new BorderLayout());

        //Load the pet images and create an array of indexes.
        images = new ImageIcon[petStrings.length];
        Integer[] intArray = new Integer[petStrings.length];
        for (int i = 0; i < petStrings.length; i++) {
            intArray[i] = new Integer(i);
            images[i] = createImageIcon("/images/fooditems/" + petStrings[i] + ".png");
            if (images[i] != null) {
                images[i].setDescription(petStrings[i]);
            }
        }

        //Create the combo box.
        JList petList = new JList(intArray);

        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, 30));
        //petList.setRenderer(renderer);
        petList.setCellRenderer(renderer);
        petList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // Set horizontal layout
        petList.setVisibleRowCount(1); // Show all items in a single row


        petList.setPreferredSize(new Dimension(550, 70)); // adjust based on desired height

        JScrollPane scrollPane = new JScrollPane(petList);
        setBounds(20, 100, 750, 70);
        add(scrollPane, BorderLayout.PAGE_START);

        //Lay out the demo.
        renderer.setPreferredSize(new Dimension(50, 70));  // Set a fixed size for each list cell
        add(petList, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setBounds(20, 100, 750, 70);
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = CustomListDemo.class.getResource(path);

        //System.out.println("Image URL: " + imgURL);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("CustomListDemo");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new CustomListDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    class ComboBoxRenderer extends JPanel implements ListCellRenderer {
        private Font uhOhFont;
        private JLabel label;
        private JLabel iconLabel;

        public ComboBoxRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));  // Vertical layout
            iconLabel = new JLabel();
            label = new JLabel();
            label.setHorizontalAlignment(CENTER);
            label.setVerticalAlignment(CENTER);
            add(iconLabel);
            add(label);
            setOpaque(true);
        }

        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            int selectedIndex = ((Integer)value).intValue();

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            // Get the icon and text
            ImageIcon icon = images[selectedIndex];
            String pet = petStrings[selectedIndex];

            // Resize the image to fit within the cell's bounds (adjust width/height as necessary)
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);  // Resize image
            iconLabel.setIcon(new ImageIcon(scaledImg));

            label.setText(pet);

            return this;
        }

        // Set the font and text when no image was found.
        protected void setUhOhText(String uhOhText, Font normalFont) {
            if (uhOhFont == null) { // lazily create this font
                uhOhFont = normalFont.deriveFont(Font.ITALIC);
            }
            label.setFont(uhOhFont);
            label.setText(uhOhText);
        }
    }
}