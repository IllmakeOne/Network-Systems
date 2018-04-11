import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class SceneSwitch extends Application {
    private static final int HEIGHT = 680;
    private static final int WIDTH = 400;
    private static final int PADDING = 35;

    private Stage window;
    private Scene firstChatScene, secondChatScene, thirdChatScene, fourthChatScene, globalChatScene;
    private Button firstChatBtn, secondChatBtn, thirdChatBtn, fourthChatBtn, globalChatBtn;
    private Label firstChatLabel, secondChatLabel, thirdChatLabel, fourthChatLabel, globalChatLabel;
    private TextArea generalTextArea;

    @Override
    public void start(Stage primaryStage) throws Exception {
        initializeButtons();
        initializeLabels();
        window = primaryStage;
        generalTextArea = new TextArea();

        VBox firstChatLayout = new VBox(PADDING);
        System.out.println(thirdChatBtn.toString() + " " + fourthChatBtn.toString());
        firstChatLayout.getChildren().addAll(firstChatLabel, secondChatBtn, thirdChatBtn,
                fourthChatBtn, globalChatBtn, generalTextArea);
        firstChatScene = new Scene(firstChatLayout, WIDTH, HEIGHT);
        initializeButtons();
        initializeLabels();

        VBox secondChatLayout = new VBox(34);
        secondChatLayout.getChildren().addAll(secondChatLabel, firstChatBtn, thirdChatBtn,
                fourthChatBtn, globalChatBtn, generalTextArea);
        secondChatScene = new Scene(secondChatLayout, WIDTH, HEIGHT);
        initializeButtons();
        initializeLabels();

        VBox thirdChatLayout = new VBox(33);
        thirdChatLayout.getChildren().addAll(thirdChatLabel, firstChatBtn, secondChatBtn,
                fourthChatBtn, globalChatBtn);
        thirdChatScene = new Scene(thirdChatLayout, WIDTH, HEIGHT);
        initializeButtons();
        initializeLabels();

        VBox fourthChatLayout = new VBox(32);
        fourthChatLayout.getChildren().addAll(fourthChatLabel, firstChatBtn, secondChatBtn,
                thirdChatBtn, globalChatBtn);
        fourthChatScene = new Scene(fourthChatLayout, WIDTH, HEIGHT);
        initializeButtons();
        initializeLabels();

        VBox globalChatLayout = new VBox(31);
        globalChatLayout.getChildren().addAll(globalChatLabel, firstChatBtn, secondChatBtn,
                thirdChatBtn, fourthChatBtn);
        globalChatScene = new Scene(globalChatLayout, WIDTH, HEIGHT);
        initializeButtons();
        initializeLabels();

        window.setScene(globalChatScene);
        window.setTitle("Best chatting application");
        window.show();
    }

    private void initializeLabels() {
        firstChatLabel = new Label("You are chatting with person 1");
        secondChatLabel = new Label("You are chatting with person 2");
        thirdChatLabel = new Label("You are chatting with person 3");
        fourthChatLabel = new Label("You are chatting with person 4");
        globalChatLabel = new Label("You are chatting with every person that is reachable");
    }

    private void initializeButtons() {
        firstChatBtn = new Button("Go to chat 1");
        secondChatBtn = new Button("Go to chat 2");
        thirdChatBtn = new Button("Go to chat 3");
        fourthChatBtn = new Button("Go to chat 4");
        globalChatBtn = new Button("Go to global chat");

        firstChatBtn.setOnAction(event -> window.setScene(firstChatScene));
        secondChatBtn.setOnAction(event -> window.setScene(secondChatScene));
        thirdChatBtn.setOnAction(event -> window.setScene(thirdChatScene));
        fourthChatBtn.setOnAction(event -> window.setScene(fourthChatScene));
        globalChatBtn.setOnAction(event -> window.setScene(globalChatScene));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
