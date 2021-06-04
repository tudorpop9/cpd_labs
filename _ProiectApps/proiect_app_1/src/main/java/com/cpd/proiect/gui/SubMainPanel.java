package com.cpd.proiect.gui;

import com.cpd.proiect.control.FirstPublishConfig;
import com.cpd.proiect.control.SecondPublishConfig;
import com.cpd.proiect.control.TokenManager;
import com.cpd.proiect.subscribe.SubscriptionsListener;
import org.springframework.web.reactive.function.client.WebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;

public class SubMainPanel extends JPanel {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;

    public static String sub1TextAreaContent = "";
    public static String sub2TextAreaContent = "";

    private JScrollPane subScroll1;
    private JScrollPane subScroll2;

    private JPanel mainPanel;
    private JPanel labelPanel;
    private JPanel textAreaPanel;

    private JLabel sub1NotificationLabel;
    private JLabel sub2NotificationLabel;

    private JTextArea sub1TextArea;
    private JTextArea sub2TextArea;

    public SubMainPanel() throws HeadlessException {

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2,1));

        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1,2));
        labelPanel.setSize(WIDTH, (int)(0.2 * HEIGHT));


        textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new GridLayout(1,2));
        textAreaPanel.setSize(WIDTH, (int)(0.8 * HEIGHT));



        sub1NotificationLabel = new JLabel();
        sub1NotificationLabel.setText("Listening to Topic: " + SubscriptionsListener.SUBSCRIPTION_QUEUE_1);
        sub1NotificationLabel.setPreferredSize(new Dimension(150,150));
        sub2NotificationLabel = new JLabel();
        sub2NotificationLabel.setText("Listening to Topic: " + SubscriptionsListener.SUBSCRIPTION_QUEUE_2);
        sub2NotificationLabel.setPreferredSize(new Dimension(150,150));


        sub1TextArea = new JTextArea();
        sub1TextArea.setText(sub1TextAreaContent);
        sub1TextArea.setEditable(false);
        sub1TextArea.setSize(180, 400);
        subScroll1 = new JScrollPane(sub1TextArea);

        sub2TextArea = new JTextArea();
        sub2TextArea.setText(sub2TextAreaContent);
        sub2TextArea.setEditable(false);
        sub2TextArea.setSize(180, 400);
        subScroll2 = new JScrollPane(sub2TextArea);



        labelPanel.add(sub1NotificationLabel);
        labelPanel.add(sub2NotificationLabel);
        textAreaPanel.add(subScroll1);
        textAreaPanel.add(subScroll2);

        mainPanel.add(labelPanel);
        mainPanel.add(textAreaPanel);

        this.add(mainPanel);
        this.setSize(WIDTH, HEIGHT);


        // update sub text areas 2 times per second
        Timer textAreaTimer = new Timer();
        textAreaTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateSubTextArea1();
                updateSubTextArea2();
            }
        }, 500L, 500L);
    }

    // Static methods to be used by queue listeners
    public static synchronized void updateSubContent1(String newMsg){
        String newContent = sub1TextAreaContent.concat("\n").concat(newMsg);
        sub1TextAreaContent = newContent;
    }


    public static synchronized void updateSubContent2(String newMsg){
        String newContent = sub2TextAreaContent.concat("\n").concat(newMsg);
        sub2TextAreaContent = newContent;
    }

    //updating methods called by a timer
    public synchronized void updateSubTextArea1(){
        this.sub1TextArea.setText(sub1TextAreaContent);
    }

    public synchronized void updateSubTextArea2(){
        this.sub2TextArea.setText(sub2TextAreaContent);
    }



}
