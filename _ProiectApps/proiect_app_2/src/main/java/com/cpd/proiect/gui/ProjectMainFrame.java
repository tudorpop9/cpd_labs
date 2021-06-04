package com.cpd.proiect.gui;

import javax.swing.*;
import javax.websocket.OnClose;
import java.awt.*;

public class ProjectMainFrame extends JFrame {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 500;

    private SubMainPanel subMainPanel;
    private PubMainPanel pubMainPanel;

    public ProjectMainFrame(String title, SubMainPanel subMainPanel, PubMainPanel pubMainPanel) throws HeadlessException {
        super(title);
        this.subMainPanel = subMainPanel;
        this.pubMainPanel = pubMainPanel;

        this.setLayout(new GridLayout(1,2));
        this.add(pubMainPanel);
        this.add(subMainPanel);

        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
