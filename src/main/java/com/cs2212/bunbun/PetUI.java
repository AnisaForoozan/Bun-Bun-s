package com.cs2212.bunbun;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.swing.JOptionPane;

public class PetUI extends javax.swing.JFrame {
    // Array of pet state image file names
    private String pet_img_files[] = {
        "normal.png", 
        "angery.png", 
        "sleeping.png", 
        "dead.png", 
        "hungry.png"
    };
    
    // Array of ImageIcons that store the loaded pet state images.
    private ImageIcon pet_imgs[];
    

    // Array of references to JButtons that control the pet states.
    private JButton state_buttons[];
    
    // Constants that define the different pet states.
    private static final int PET_STATE_NORMAL = 0;
    private static final int PET_STATE_ANGERY = 1;
    private static final int PET_STATE_SLEEPING = 2;
    private static final int PET_STATE_DEAD = 3;
    private static final int PET_STATE_HUNGRY = 4;
    
    // The current pet state.
    private int pet_state = PET_STATE_NORMAL;
    
    // Timer that triggers events;
    private Timer timer;
    
    private int health = 1000;
    
    
    /**
     * Creates new form PetUI
     */
    public PetUI() {
        this.setTitle("CS2212 GUI Demo");
        
        pet_imgs = new ImageIcon[pet_img_files.length];
        
        // Loop through the pet state image files
        for (int i = 0; i < pet_img_files.length; i++) {
            // Load each image from the src/main/resources/images/ directory
            /** 
             * Important note: it is recommended to add the following to pom.xml
             * if you are using the same directory layout:
             * <build>
             *   <resources>
             *     <resource>
             *        <directory>src/main/resources</directory>
             *     </resource>
             *   </resources>
             * </build>
             * 
             * see the pom.xml file in this project for an example.
             **/
            URL imageUrl = getClass().getClassLoader().getResource("images/" + pet_img_files[i]);
            
            if (imageUrl != null) {
                // Create an ImageIcon from the image URL and store it in the array
                pet_imgs[i] = new ImageIcon(imageUrl);
            } else {
                // Could not find the image file!
                System.out.println("Image not found: " + pet_img_files[i]);
                pet_imgs[i] = null;  // Store null if the image wasn't found
            }
        }
        
        initComponents();
        
        // Make an array of the state buttons to make it easy to enable/disable them
        state_buttons = new JButton[pet_img_files.length];
        state_buttons[PET_STATE_NORMAL] = normalButton;
        state_buttons[PET_STATE_ANGERY] = angeryButton;
        state_buttons[PET_STATE_SLEEPING] = sleepingButton;
        state_buttons[PET_STATE_DEAD] = deadButton;
        state_buttons[PET_STATE_HUNGRY] = hungryButton;
        
        // Update the pet image
        setPetState(PET_STATE_NORMAL);
        
        healthBar.setMaximum(1000);
        healthBar.setValue(health);
        healthText.setText("Health: " + health);
        
        
        // Create a Timer that triggers every 100 milliseconds (0.1 seconds) and then calls the tick function.
        timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        
        // Start the timer
        timer.start();
    }
    
    // This function is called every 100 milliseconds and lowers the pet's health
    // It also has a 5 in 100 chance to flip the image.
    public void tick() {
        if (health > 0) {
            health--;
            healthBar.setValue(health);
            healthText.setText("Health: " + health);
        }
        
        int randomNumber = new java.util.Random().nextInt(100) + 1;
        if(randomNumber > 95) {
            flipPet();
        }
    }
    
    // Flip the current image of the pet
    // This is not a very efficient way to do this, would be better to store the
    // flipped versions then make a new one each time.
    public void flipPet() {
        petLabel.setIcon(flipImageHorizontally((ImageIcon) petLabel.getIcon()));
    }
    
    // Method to flip an ImageIcon horizontally
    public static ImageIcon flipImageHorizontally(ImageIcon icon) {
        // Extract the Image from the ImageIcon
        Image img = icon.getImage();

        // Convert the Image to a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the original image into the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        // Create an AffineTransform to flip the image horizontally
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-bufferedImage.getWidth(), 0);

        // Apply the transformation to the BufferedImage
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);

        // Return the flipped image as an ImageIcon
        return new ImageIcon(bufferedImage);
    }
    
    // Sets the current state of the pet and updates the buttons and images.
    public void setPetState(int state) {
        pet_state = state;
        updateStateButtons();
        updatePetImage();
    }
    
    // Update the enabled/disabled state of the buttons based on the current pet state.
    public void updateStateButtons(){
        for(JButton b:state_buttons) {
            b.setEnabled(true);
        }
        state_buttons[pet_state].setEnabled(false);
    }
    
    // Update the image icon in petLabel to the image for the current pet state.
    public void updatePetImage() {
        petLabel.setText("");
        petLabel.setIcon(pet_imgs[pet_state]);
    }

    /**
     * This method is code generated by Netbeans's GUI Builder.
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        normalButton = new javax.swing.JButton();
        angeryButton = new javax.swing.JButton();
        sleepingButton = new javax.swing.JButton();
        deadButton = new javax.swing.JButton();
        hungryButton = new javax.swing.JButton();
        healthBar = new javax.swing.JProgressBar();
        healthText = new javax.swing.JLabel();
        Exit = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        petLabel = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        normalButton.setText("Normal");
        normalButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalButtonActionPerformed(evt);
            }
        });

        angeryButton.setText("Angery");
        angeryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                angeryButtonActionPerformed(evt);
            }
        });

        sleepingButton.setText("Sleeping");
        sleepingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sleepingButtonActionPerformed(evt);
            }
        });

        deadButton.setText("Dead");
        deadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deadButtonActionPerformed(evt);
            }
        });

        hungryButton.setText("Hungry");
        hungryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hungryButtonActionPerformed(evt);
            }
        });

        healthText.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        healthText.setText("Health: ");

        Exit.setText("Exit");
        Exit.setMaximumSize(new java.awt.Dimension(75, 33));
        Exit.setMinimumSize(new java.awt.Dimension(75, 33));
        Exit.setPreferredSize(new java.awt.Dimension(75, 33));
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });

        petLabel.setText("petLabel");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(petLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(petLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Pop Up");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(deadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(506, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(normalButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(122, 122, 122)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(healthText)
                                    .addComponent(healthBar, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(angeryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sleepingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hungryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Exit, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(normalButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Exit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addComponent(angeryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sleepingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(hungryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(deadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(healthBar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(healthText)
                .addContainerGap(73, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   // The following methods ending in "ActionPerformed" are called when a button is pressed.
    
    private void normalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalButtonActionPerformed
        setPetState(PET_STATE_NORMAL);
    }//GEN-LAST:event_normalButtonActionPerformed

    private void angeryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_angeryButtonActionPerformed
        setPetState(PET_STATE_ANGERY);
    }//GEN-LAST:event_angeryButtonActionPerformed

    private void sleepingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sleepingButtonActionPerformed
        setPetState(PET_STATE_SLEEPING);
    }//GEN-LAST:event_sleepingButtonActionPerformed

    private void hungryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hungryButtonActionPerformed
        setPetState(PET_STATE_HUNGRY);
    }//GEN-LAST:event_hungryButtonActionPerformed

    private void deadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deadButtonActionPerformed
        setPetState(PET_STATE_DEAD);
    }//GEN-LAST:event_deadButtonActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
         int response = JOptionPane.showConfirmDialog(
            null, 
            "Do you want to exit?", 
            "Exit?", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE
        );
        if(response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_ExitActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JOptionPane.showMessageDialog(null, "This is an example of a popup!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * This is the main method.
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PetUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PetUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PetUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PetUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PetUI().setVisible(true);
            }
        });
    }

    
    // These variables were generated by the NetBeans's GUI builder.
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Exit;
    private javax.swing.JButton angeryButton;
    private javax.swing.JButton deadButton;
    private javax.swing.JProgressBar healthBar;
    private javax.swing.JLabel healthText;
    private javax.swing.JButton hungryButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton normalButton;
    private javax.swing.JLabel petLabel;
    private javax.swing.JButton sleepingButton;
    // End of variables declaration//GEN-END:variables
}
