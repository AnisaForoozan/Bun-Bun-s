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

/**
 * The {@code CustomGiftList} class is a JPanel that displays a list of gift items,
 * allows the user to select one gift, and provides a custom renderer for displaying
 * both images and text in the list.
 * <p>
 * This panel includes a horizontal list of gift items represented by images and their
 * names. The user can select a gift from the list, and the selection will be cleared
 * if a new item is selected. It also integrates with a food list, clearing its selection
 * when a gift is selected.
 */
public class CustomListDemo extends JPanel {
    ImageIcon[] images;
    String[] petStrings = {"applepie", "burger", "cookies", "frenchfries", "garlicbread", "macncheese", "pancakes", "salmon", "steak", "strawberrycake"};
    JList petList;

    private JList giftList; // Reference to the gift list

    /**
     * Constructs a {@code CustomGiftList} panel, initializes the list of gift items,
     * sets up the JList with custom renderer, and loads the corresponding images.
     * This constructor also configures the layout and initializes the mouse listener
     * for item selection behavior.
     */
    public CustomListDemo() {
        super(new BorderLayout());

        //Load the pet images and create an array of indexes.
        images = new ImageIcon[petStrings.length];
        Integer[] intArray = new Integer[petStrings.length];
        for (int i = 0; i < petStrings.length; i++) {
            intArray[i] = i;
            images[i] = createImageIcon("/images/fooditems/" + petStrings[i] + ".png");
            if (images[i] != null) {
                images[i].setDescription(petStrings[i]);
            }
        }

        //Create the combo box.
        petList = new JList(intArray);

        ComboBoxRenderer renderer= new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, 30));
        //petList.setRenderer(renderer);
        petList.setCellRenderer(renderer);
        petList.setBackground(new Color(193, 154, 107));
        petList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        petList.setLayoutOrientation(JList.HORIZONTAL_WRAP); // Set horizontal layout
        petList.setVisibleRowCount(1); // Show all items in a single row


        petList.setPreferredSize(new Dimension(550, 70)); // adjust based on desired height

        JScrollPane scrollPane = new JScrollPane(petList);
        scrollPane.setForeground(new Color(193, 154, 107));
        setBounds(20, 100, 750, 70);
        add(scrollPane, BorderLayout.PAGE_START);

        //Lay out the demo.
        renderer.setPreferredSize(new Dimension(70, 70));  // Set a fixed size for each list cell
        add(petList, BorderLayout.PAGE_START);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setBounds(20, 100, 750, 30);

        petList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && petList.getSelectedIndex() != -1 && giftList != null) {
                giftList.clearSelection();
            }
        });
    }

    /**
     * Sets the gift list. This list's selection will be cleared when a gift is selected.
     *
     * @param giftList the JList representing the food items
     */
    public void setGiftList(JList giftList) {
        this.giftList = giftList;
    }

    /**
     * Returns the selected gift as a string. If no item is selected, returns {@code null}.
     *
     * @return the string representation of the selected gift, or {@code null} if no gift is selected
     */
    public String isSelected() {
        Object selectedValue = petList.getSelectedValue();
        if (selectedValue != null) {
            int index = (Integer) selectedValue; // The index of the selected item
            return petStrings[index];            // The string representation of the selected food item
        } else {
            return null; // No item selected
        }
    }


    /**
     * Creates an {@code ImageIcon} from the specified path. If the image is not found, logs an error.
     *
     * @param path the path to the image file
     * @return the created {@code ImageIcon}, or {@code null} if the image could not be loaded
     */
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
     * This method creates and shows the GUI for the {@code CustomGiftList}. It should be invoked from
     * the event-dispatching thread for thread safety.
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

    /**
     * Main method to run the GUI.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }


    /**
     * Custom cell renderer for the pet list. This renderer displays both an image and text
     * for each item in the list.
     */
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

        /**
         * Renders a list cell with an image and text.
         *
         * @param list         the {@code JList} being rendered
         * @param value        the value of the list item
         * @param index        the index of the item
         * @param isSelected   whether the item is selected
         * @param cellHasFocus whether the item has focus
         * @return the component to render
         */
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
            label.setFont(new Font("Arial", Font.PLAIN, 10)); //customize text

            return this;
        }

    }

}