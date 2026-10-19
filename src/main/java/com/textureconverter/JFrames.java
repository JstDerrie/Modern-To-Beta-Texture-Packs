package com.textureconverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditorSupport;
import java.io.File;

// Driver Class
public class JFrames {
    private static File selectedFolderIn;
    private static File selectedFolderOut;

    public static void buildButton(JPanel panel, JFrame frame, JComboBox<Integer> res, JCheckBox version) {

        ImageIcon bIcon = new ImageIcon("src/textures/Buttone1.png");
        ImageIcon original = new ImageIcon("src/textures/button.png");
        Image scaled = original.getImage().getScaledInstance(
                200, 200, Image.SCALE_SMOOTH
        );
        bIcon = new ImageIcon(scaled);



        JButton button = new JButton("Build", bIcon);
        // Button parameters
        button.setBounds(400,300,150,150);
        frame.setLocationRelativeTo(null);
        panel.add(button);


        //Button looks
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);



        button.addActionListener(e -> {
            if (selectedFolderIn == null) {
                JOptionPane.showMessageDialog(frame, "Select a folder first.");
                return;
            }

            int resolution = (Integer) res.getSelectedItem();
            boolean ver = version.isSelected();
            Main.main(selectedFolderIn.toPath(), resolution, ver, selectedFolderOut.toPath());
        });



    }

    public static void fileButtonIn(JPanel panel, JFrame frame) {
        ImageIcon bIcon = new ImageIcon("src/input/hardTextures/gui/sprites/widget/button.png");

        Image scaled = bIcon.getImage().getScaledInstance(
                300, 30, Image.SCALE_SMOOTH
        );
        bIcon = new ImageIcon(scaled);
        JButton button = new JButton("Select texture folder", bIcon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBounds(10, 50, 400, 40);
        button.setForeground(Color.white);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        panel.add(button);


       /* ImageIcon original = new ImageIcon("src/input/block/dirt.png");
        Image scaled = original.getImage().getScaledInstance(
                200, 200, Image.SCALE_SMOOTH
        );*/

        button.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFolderIn = chooser.getSelectedFile();
                JOptionPane.showMessageDialog(
                        frame,
                        "Selected folder: " + selectedFolderIn.getAbsolutePath()
                );

            }
        });
    }

    public static void fileButtonOut(JPanel panel, JFrame frame) {
        ImageIcon bIcon = new ImageIcon("src/input/hardTextures/gui/sprites/widget/button.png");

        Image scaled = bIcon.getImage().getScaledInstance(
                300, 30, Image.SCALE_SMOOTH
        );
        bIcon = new ImageIcon(scaled);
        JButton button1 = new JButton("Select output folder", bIcon);


        button1.setHorizontalTextPosition(SwingConstants.CENTER);
        button1.setVerticalTextPosition(SwingConstants.CENTER);
        button1.setFont(new Font("Arial", Font.BOLD, 20));
        button1.setBounds(10, 100, 400, 40);
        button1.setForeground(Color.white);
        button1.setBorderPainted(false);
        button1.setContentAreaFilled(false);
        button1.setFocusPainted(false);

        panel.add(button1);

        button1.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFolderOut = chooser.getSelectedFile();

                JOptionPane.showMessageDialog(
                        frame,
                        "Selected folder: " + selectedFolderOut.getAbsolutePath()
                );

            }
        });
    }

    public static JComboBox<Integer> comboButton(JPanel panel) {

        ImageIcon bIcon = new ImageIcon("src/textures/button.png");
        ImageIcon original = new ImageIcon("src/textures/button.png");
        Image scaled = original.getImage().getScaledInstance(
                200, 200, Image.SCALE_SMOOTH
        );
        bIcon = new ImageIcon(scaled);

        JComboBox<Integer> resolution = new JComboBox<>(new Integer[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512});

        resolution.setBounds(400, 50, 150, 30);
        panel.add(resolution);

        return resolution;
    }

    public static JCheckBox checkbox(JPanel panel) {
        JCheckBox version = new JCheckBox("Version 1.14+");
        version.setBounds(400, 250, 150, 30);
        version.setContentAreaFilled(false);
        panel.add(version);
        return version;
    }

    // main function
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("MBTC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setResizable(false);

        ImageIcon icon = new ImageIcon("src/textures/pfp.png");
        frame.setIconImage(icon.getImage());

        JPanel panel = new JPanel(null);

        frame.add(panel);

        JComboBox<Integer> resolution = comboButton(panel);

        fileButtonIn(panel, frame);

        fileButtonOut(panel, frame);

        JCheckBox version = checkbox(panel);

        buildButton(panel, frame, resolution, version);

        frame.setVisible(true);


    }

}
