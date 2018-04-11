package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;


public class SceneSwitch extends Application {
    private static final int HEIGHT = 680;
    private static final int WIDTH = 400;
    private static final int PADDING = 10;
    private static final int INPUT_CHAT_HEIGHT = 50;
    private static final int NUM_OF_CHATS = 5;
    private Stage window;

    private ArrayList<String> messagesFirst, messagesSecond, messagesThird, messagesFourth, messagesGlobal;

    private Button[] goToButtons = new Button[NUM_OF_CHATS];
    private Button[] sendButtons = new Button[NUM_OF_CHATS];
    private TextArea[] chatAreas = new TextArea[NUM_OF_CHATS];
    private TextArea[] chatDisplays = new TextArea[NUM_OF_CHATS];

    private Scene firstChatScene, secondChatScene, thirdChatScene, fourthChatScene, globalChatScene;
    private Label[] chatLabels = new Label[NUM_OF_CHATS];
    private Label[] chatLogLabels = new Label[NUM_OF_CHATS];

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeLabels();
        initializeChatAreas();
        initializeMessages();
        initializeChatDisplay();
        initializeButtons();
        window = primaryStage;

        // First chat window
        VBox firstChatLayout = new VBox(PADDING);
        firstChatLayout.getChildren().addAll(chatLabels[0], goToButtons[1], goToButtons[2],
                goToButtons[3], goToButtons[4], new Label("Send a message"),  chatAreas[0], sendButtons[0], chatLogLabels[0], chatDisplays[0]);
        firstChatScene = new Scene(firstChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Second chat window
        VBox secondChatLayout = new VBox(PADDING);
        secondChatLayout.getChildren().addAll(chatLabels[1], goToButtons[0], goToButtons[2],
                goToButtons[3], goToButtons[4], new Label("Send a message"), chatAreas[1], sendButtons[1], chatLogLabels[1], chatDisplays[1]);
        secondChatScene = new Scene(secondChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Third chat window
        VBox thirdChatLayout = new VBox(PADDING);
        thirdChatLayout.getChildren().addAll(chatLabels[2], goToButtons[0], goToButtons[1],
                goToButtons[3], goToButtons[4], new Label("Send a message"), chatAreas[2], sendButtons[2], chatLogLabels[2], chatDisplays[2]);
        thirdChatScene = new Scene(thirdChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Fourth chat window
        VBox fourthChatLayout = new VBox(PADDING);
        fourthChatLayout.getChildren().addAll(chatLabels[3], goToButtons[0], goToButtons[1],
                goToButtons[2], goToButtons[4], new Label("Send a message"), chatAreas[3], sendButtons[3], chatLogLabels[3], chatDisplays[3]);
        fourthChatScene = new Scene(fourthChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Global chat window
        VBox globalChatLayout = new VBox(PADDING);
        globalChatLayout.getChildren().addAll(chatLabels[4], goToButtons[0], goToButtons[1],
                goToButtons[2], goToButtons[3], new Label("Send a message"), chatAreas[4], sendButtons[4], chatLogLabels[4], chatDisplays[4]);
        globalChatScene = new Scene(globalChatLayout, WIDTH, HEIGHT);

        // Refresh labels and buttons
        initializeButtons();
        initializeLabels();

        // Set the beginning scene
        window.setScene(firstChatScene);
        window.setTitle("Best chatting application");
        window.show();
    }

    /*
        Initializes the Chat Displays (which display the chat log).
     */
    private void initializeChatDisplay() {
        for (int i = 0; i < chatDisplays.length; i++) {
            chatDisplays[i] = new TextArea();
        }
    }

    /*
        Initializes ArrayList<Strings> for keeping track of the messages that have been sent.
    */
    private void initializeMessages() {
        messagesFirst = new ArrayList<>();
        messagesFirst.add("test");
        messagesFirst.add("this is a nice conversation");
        messagesSecond = new ArrayList<>();
        messagesThird = new ArrayList<>();
        messagesFourth = new ArrayList<>();
        messagesGlobal = new ArrayList<>();
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
            chatLabels[i] = new Label("You are chatting with person " + (i + 1));
            chatLogLabels[i] = new Label("Chat log with person " + (i + 1));
        }
        chatLabels[4].setText("Global Chat");
        chatLabels[4].setText("Chat log on Global Chat");
    }

    /*
        Initializes the buttons for going to another scene and the sending button.
    */
    private void initializeButtons() {
        for (int i = 0; i < goToButtons.length; i++) {
            goToButtons[i] = new Button("Go to chat " + (i + 1));

            Button sendTemp = new Button("Send");
            sendTemp.setOnAction(buttonHandler);
            sendButtons[i] = sendTemp;
        }

        goToButtons[0].setOnAction(event -> window.setScene(firstChatScene));
        goToButtons[1].setOnAction(event -> window.setScene(secondChatScene));
        goToButtons[2].setOnAction(event -> window.setScene(thirdChatScene));
        goToButtons[3].setOnAction(event -> window.setScene(fourthChatScene));
        goToButtons[4].setOnAction(event -> window.setScene(globalChatScene));
        goToButtons[4].setText("Go to global chat");
    }

    /*
        This function gets called when the send button is clicked. The text that the user typed in an
        TextArea is used here.
     */
    private EventHandler<ActionEvent> buttonHandler = event -> {
        for (int i = 0; i < chatAreas.length; i++) {
            System.out.println(event.getSource());

            System.out.println(chatAreas[i].getText());


            chatAreas[i].setText("");
        }
        event.consume();
    };

    /*
        Run the program.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /*
        Fills the chat logg with the messages that were sent
     */
    public void fillChatDisplays(ArrayList<String> messages, int chat) {

    }
}
