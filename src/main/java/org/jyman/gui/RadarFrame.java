package org.jyman.gui;

import javax.swing.*;

public class RadarFrame extends JFrame {
    public RadarFrame() {
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("자바보다 좋은 언어는 없다.. 코틀린 빼고");
        setVisible(true);
        setIconImage(new ImageIcon("./src/main/java/org/jyman/image/yuuka.png").getImage());
    }
}
