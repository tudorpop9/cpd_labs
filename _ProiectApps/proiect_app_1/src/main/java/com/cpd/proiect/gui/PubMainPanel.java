package com.cpd.proiect.gui;

import com.cpd.proiect.control.FirstPublishConfig;
import com.cpd.proiect.control.SecondPublishConfig;
import com.cpd.proiect.control.TokenManager;
import com.cpd.proiect.publish.FirstPublisher;
import org.springframework.web.reactive.function.client.WebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PubMainPanel extends JPanel {

    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;
    private WebClient.Builder requestBuilder;


    private JPanel mainPanel;
    private JPanel labelPanel;
    private JPanel textAreaPanel;

    private JLabel pub1NotificationLabel;
    private JLabel pub2NotificationLabel;

    private JScrollPane pub1Scroll;
    private JScrollPane pub2Scroll;
    private JTextArea pub1TextArea;
    private JTextArea pub2TextArea;

    public PubMainPanel(WebClient.Builder requestBuilder) throws HeadlessException {
        this.requestBuilder = requestBuilder;



        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2,1));

        labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1,2));
        labelPanel.setSize(WIDTH, (int)(0.2 * HEIGHT));

        textAreaPanel = new JPanel();
        textAreaPanel.setLayout(new GridLayout(1,2));
        textAreaPanel.setSize(WIDTH, (int)(0.8 * HEIGHT));

        pub1NotificationLabel = new JLabel();
        pub1NotificationLabel.setText("Topic: " + FirstPublishConfig.PUB_QUEUE_1);
        pub1NotificationLabel.setSize(new Dimension(150, 80));
        pub1NotificationLabel.setOpaque(true);

        pub2NotificationLabel = new JLabel();
        pub2NotificationLabel.setText("Topic: " + SecondPublishConfig.PUB_QUEUE_2);
        pub2NotificationLabel.setSize(new Dimension(150, 80));
        pub2NotificationLabel.setOpaque(true);

        // La apasarea tastei enter intr-un textarea fac un api call pt trimiterea unui mesaj
        pub1TextArea = new JTextArea();
        pub1TextArea.setText("");
        pub1TextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && TokenManager.hasToken()
                        && pub1TextArea.hasFocus() && !pub1TextArea.getText().equals("")){
                    // send msg to queue
                    String resp1 = requestBuilder.build()
                            .post()
                            .uri("http://localhost:8081/publisher1/" + pub1TextArea.getText())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    pub1TextArea.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        pub1TextArea.setPreferredSize(new Dimension(180, 200));
        pub1Scroll = new JScrollPane(pub1TextArea);

        // La apasarea tastei enter intr-un textarea fac un api call pt trimiterea unui mesaj
        pub2TextArea = new JTextArea();
        pub2TextArea.setText("");
        pub2TextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && TokenManager.hasToken()
                        && pub2TextArea.hasFocus() && !pub2TextArea.getText().equals("")){
                    // send msg to queue
                    String resp1 = requestBuilder.build()
                            .post()
                            .uri("http://localhost:8081/publisher2/" + pub2TextArea.getText())
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    pub2TextArea.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        pub2TextArea.setPreferredSize(new Dimension(180, 200));
//        pub2Scroll.add(pub2TextArea);
        pub2Scroll = new JScrollPane(pub2TextArea);

        labelPanel.add(pub1NotificationLabel);
        labelPanel.add(pub2NotificationLabel);
        textAreaPanel.add(pub1Scroll);
        textAreaPanel.add(pub2Scroll);


        mainPanel.add(labelPanel);
        mainPanel.add(textAreaPanel);

        this.add(mainPanel);
        this.setSize(WIDTH, HEIGHT);
//        this.setBackground(Color.gray);
    }

    // Controale care influenteaza gui-ul, folosite la plecarea si sosirea token-ului
    public synchronized void setTextArea1Editable(boolean value){
        this.pub1TextArea.setEditable(value);
        if(value == true){
            this.pub1TextArea.setBackground(Color.white);
        }else{
            this.pub1TextArea.setBackground(Color.lightGray);
        }
    }

    public synchronized void setTextArea2Editable(boolean value){
        this.pub2TextArea.setEditable(value);
        if(value == true){
            this.pub2TextArea.setBackground(Color.white);
        }else{
            this.pub2TextArea.setBackground(Color.lightGray);
        }
    }

    public synchronized void setLabel1Green(){
//        this.pub1NotificationLabel.setBackground(Color.GREEN);
    }

    public synchronized void setLabel2Green(){
//        this.pub2NotificationLabel.setBackground(Color.GREEN);
    }

    public synchronized void setLabel1Red(){
//        this.pub1NotificationLabel.setBackground(Color.RED);
    }

    public synchronized void setLabel2Red(){
//        this.pub2NotificationLabel.setBackground(Color.RED);
    }
}
