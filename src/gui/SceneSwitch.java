package gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.MasterMind;
import java.util.ArrayList;

/*
    GUI Class for the chatting application
 */

public class SceneSwitch extends Application {
    private static final int HEIGHT = 600;
    private static final int WIDTH = 400;
    private static final int PADDING = 10;
    private static final int INPUT_CHAT_HEIGHT = 50;
    private static final int NUM_OF_CHATS = 5;
    private MasterMind masterMind;
    private Stage window;

    private Button[] goToButtons = new Button[NUM_OF_CHATS];
    private Button[] sendButtons = new Button[NUM_OF_CHATS];
    private TextArea[] chatAreas = new TextArea[NUM_OF_CHATS];
    private TextArea[] chatDisplays = new TextArea[NUM_OF_CHATS];
    private ArrayList<ArrayList<String>> messageCollections = new ArrayList<>();

    private Scene firstChatScene, secondChatScene, thirdChatScene, fourthChatScene, globalChatScene;
    private Label[] chatLabels = new Label[NUM_OF_CHATS];
    private Label[] chatLogLabels = new Label[NUM_OF_CHATS];

    public static final String OFFLINE = "Not online";

    @Override
    public void start(Stage primaryStage) throws Exception {
        String SOURCE = getParameters().getUnnamed().get(0);
        initializeLabels();
        initializeChatAreas();
        initializeMessages();
        initializeChatDisplay();
        initializeButtons();
        window = primaryStage;

        // Global chat window
        VBox globalChatLayout = new VBox(PADDING);
        globalChatLayout.getChildren().addAll(chatLabels[0], goToButtons[1], goToButtons[2],
                goToButtons[3], goToButtons[4], new Label("Send a message"),  chatAreas[0], sendButtons[0], chatLogLabels[0], chatDisplays[0]);
        globalChatScene = new Scene(globalChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // First chat window
        VBox firstChatLayout = new VBox(PADDING);
        firstChatLayout.getChildren().addAll(chatLabels[1], goToButtons[0], goToButtons[2],
                goToButtons[3], goToButtons[4], new Label("Send a message"), chatAreas[1], sendButtons[1], chatLogLabels[1], chatDisplays[1]);
        firstChatScene = new Scene(firstChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Second chat window
        VBox secondChatLayout = new VBox(PADDING);
        secondChatLayout.getChildren().addAll(chatLabels[2], goToButtons[0], goToButtons[1],
                goToButtons[3], goToButtons[4], new Label("Send a message"), chatAreas[2], sendButtons[2], chatLogLabels[2], chatDisplays[2]);
        secondChatScene = new Scene(secondChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Third chat window
        VBox thirdChatLayout = new VBox(PADDING);
        thirdChatLayout.getChildren().addAll(chatLabels[3], goToButtons[0], goToButtons[1],
                goToButtons[2], goToButtons[4], new Label("Send a message"), chatAreas[3],
                sendButtons[3], chatLogLabels[3], chatDisplays[3]);
        thirdChatScene = new Scene(thirdChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Fourth chat window
        VBox fourthChatLayout = new VBox(PADDING);
        fourthChatLayout.getChildren().addAll(chatLabels[4], goToButtons[0], goToButtons[1],
                goToButtons[2], goToButtons[3], new Label("Send a message"), chatAreas[4],
                sendButtons[4], chatLogLabels[4], chatDisplays[4]);
        fourthChatScene = new Scene(fourthChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Set the beginning scene
        window.setScene(firstChatScene);
        window.setTitle("Best chatting application, user: " + SOURCE);
        window.show();

        masterMind = new MasterMind(SOURCE, this);
        Thread masternode = new Thread(masterMind);
        masternode.start();
    }


    /*
        Initializes the Chat Displays (which display the chat log).
     */
    private void initializeChatDisplay() {
        for (int i = 0; i < chatDisplays.length; i++) {
            TextArea temp  = new TextArea();
            temp.setEditable(false);

            // implements auto scroll to the bottom
            temp.textProperty().addListener((ChangeListener<Object>)
                    (observable, oldValue, newValue) -> temp.setScrollTop(Double.MAX_VALUE));

            chatDisplays[i] = temp;
        }
    }

    /*
        Initializes ArrayList<Strings> for keeping track of the messages that have been sent.
    */
    private void initializeMessages() {
        for (int i = 0; i < 5; i++) {
            messageCollections.add(new ArrayList<>());
        }
    }

    /*
        Initializes the input field for the chat ares.
     */
    private void initializeChatAreas() {
        for (int i = 0; i < chatAreas.length; i++) {
            chatAreas[i] = new TextArea();
            chatAreas[i].setPrefHeight(INPUT_CHAT_HEIGHT);
            chatAreas[i].setText("");
        }
    }

    /*
        Initializes top label which indicates with whom you are chatting. And the label that indicates the chat log.
    */
    private void initializeLabels() {
        for (int i = 0; i < chatLabels.length; i++) {
            chatLabels[i] = new Label("You are chatting with person " + i);
            chatLogLabels[i] = new Label("Chat log with person " + i);
        }
        chatLabels[0].setText("Global Chat");
        chatLabels[0].setText("Chat log on Global Chat");
    }

    /*
        Initializes the buttons for going to another scene and the sending button.
    */
    private void initializeButtons() {
        for (int i = 0; i < goToButtons.length; i++) {
            goToButtons[i] = new Button("Go to chat " + i);

            Button sendTemp = new Button("Send");
            sendTemp.setId(i + "");
            sendTemp.setOnAction(buttonHandler);
            sendButtons[i] = sendTemp;
        }

        goToButtons[0].setOnAction(event -> window.setScene(globalChatScene));
        goToButtons[0].setText("Go to global chat");
        goToButtons[1].setOnAction(event -> window.setScene(firstChatScene));
        goToButtons[2].setOnAction(event -> window.setScene(secondChatScene));
        goToButtons[3].setOnAction(event -> window.setScene(thirdChatScene));
        goToButtons[4].setOnAction(event -> window.setScene(fourthChatScene));
    }

    /*
        This function gets called when the send button is clicked. The text that the user typed in an
        TextArea is used here.
     */
    private EventHandler<ActionEvent> buttonHandler = event -> {
        int windowId = Integer.valueOf(((Button) event.getSource()).getId());
        //System.out.println("SEND TO " + windowId + " FROM " + SOURCE);
        String text = chatAreas[windowId].getText();

        // if text is empty don't send.
        if (text.isEmpty()) {
            return;
        }

        //System.out.println(text);

        String outMessage = String.valueOf(windowId) + text;
        //System.out.println(outMessage);

        masterMind.sendMessage(outMessage);

        // add it to chat log
        addOwnMessageToChat(text, windowId);

        chatAreas[windowId].setText("");
        event.consume();
    };

    /*
        Run the program.
     */
    public static void main(String[] args) {
        //Application.launch(SceneSwitch.class, new String[]{});
        launch(args);

    }

    /*
        Fills the chat log with the messages that were sent
     */
    private void fillChatDisplays(ArrayList<String> messages, int chat) {
        chatDisplays[chat].setText("");
        for (String s : messages) {
            chatDisplays[chat].setText(chatDisplays[chat].getText() + " - " + s + "\n\n");
        }
        chatDisplays[chat].appendText("");
    }

    /*
        This function has to be called when a message is received for a certain person's chat.
        For example "Hello", 2 would add Hello to the chat of person 2.
     */
    public void onMessageReceived(String message, int window) {
        ArrayList<String> chatLog = messageCollections.get(window);
        if (message.equals(OFFLINE)) {
            chatLog.add(OFFLINE);
        } else {
            chatLog.add(window + ": " + message);
        }
        fillChatDisplays(chatLog, window);
    }

    public void onGlobalMessageReceived(String message, int source) {
        ArrayList<String> chatLog = messageCollections.get(0);
        chatLog.add(source + ": " + message);
        fillChatDisplays(chatLog, 0);
    }

    private void addOwnMessageToChat(String message, int window) {
        ArrayList<String> chatLog = messageCollections.get(window);
        chatLog.add("ME: " + message);
        fillChatDisplays(chatLog, window);
    }
}
