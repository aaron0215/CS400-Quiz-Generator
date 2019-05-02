/**
 * Filename: UserInterface.java Project: Quiz Generator Authors: Aaron Zhang, Aurora Shen, Tyler Gu,
 * Yixing Tu Group: A-Team 68
 * 
 * UserInterface class is the main GUI class for this project.
 * 
 */

package application;

import javafx.scene.text.Font;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import org.json.simple.parser.ParseException;

/**
 * The main GUI class
 * 
 * @author Authors: Aaron Zhang, Aurora Shen, Tyler Gu, Yixing Tu
 */
public class Main extends Application {
  QuizGenerator quizGenerator = new QuizGenerator(); // Instance of generator
  boolean needQuit = false; // boolean variable to detect if user wants to quit
  Stage stage; // Primary stage
  private HashMap<String, BorderPane> screenMap = new HashMap<>(); // Map stores all screens
  private Scene main; // Scene to display different panes
  BorderPane root; // the main menu
  Insets insets = new Insets(10); // Insets used for formatting
  String inputFileName = null; // File we read from
  int count; // Count how many questions have been answered
  List<String> filesOpened = new ArrayList<>(); // Record files have been read already
  boolean saved = true; // boolean variable to detect if all changes have been saved properly

  /**
   * Driver
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    try {
      this.stage = primaryStage;
      primaryStage.setTitle("Quiz Generator");
      root = new BorderPane();
      main = new Scene(root, 600, 600);

      Text title = new Text("Quiz Generator");
      title.setFont(Font.font("Verdana", FontWeight.BOLD, 50));

      root.setTop(title);

      Insets inset = new Insets(30);
      root.setMargin(root.getTop(), inset);
      root.setAlignment(title, Pos.CENTER);
      root.setCenter(this.setUpRootScreen());

      Button exitButton = new Button("Exit");
      exitButton.setPrefSize(150, 60);

      HBox hbox = new HBox();
      hbox.getChildren().add(exitButton);
      hbox.setAlignment(Pos.TOP_CENTER);
      hbox.setPadding(new Insets(30));
      root.setBottom(hbox);
      root.setAlignment(exitButton, Pos.CENTER);

      /**
       * Set action on exit button. Other buttons will be set in separate method
       */
      exitButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
          activate("exit"); // call activate method to set scene
          setupScreens("exit");
          System.out.println("Exit warning page");
        }
      });

      // Initialize all screens
      initializeScreens();

      main.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      primaryStage.setScene(main);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * Launcher
   * 
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * This method sets up the root screen
   * 
   * @return a hbox of all components needed in root screen
   */
  public HBox setUpRootScreen() {
    HBox hbox = new HBox();
    Button addButton = new Button("Add Question"); // add button for "add question" screen
    Button loadButton = new Button("Load Question"); // button for "load question" screen
    Button saveButton = new Button("Save"); // button for "save" screen
    // set preferred size
    addButton.setPrefSize(150, 60);
    loadButton.setPrefSize(150, 60);
    saveButton.setPrefSize(150, 60);
    // add buttons to hbox
    hbox.getChildren().addAll(addButton, loadButton, saveButton);
    hbox.setAlignment(Pos.CENTER);
    hbox.setSpacing(10);

    // EventHandlers
    addButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        activate("add"); // Switch to adding screen
        setupScreens("add"); // Set up
        System.out.println("add new question");
      }
    });

    loadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        activate("beforeLoading"); // Switch to the screen where user enters file to read from
        setupScreens("beforeLoading");
      }
    });

    saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        activate("save"); // Switch to save screen
        setupScreens("save"); // Set up
      }
    });

    return hbox;
  }

  /**
   * This method initializes all screens that we need
   */
  public void initializeScreens() {
    String[] screenNames = {"add", "load1", "load2", "next", "save", "exit", "beforeLoading"};
    for (String name : screenNames) {
      this.addScreen(name);
    }
  }

  /**
   * Generate a border pane and add it into map
   * 
   * @param name
   */
  public void addScreen(String name) {
    BorderPane pane = new BorderPane();
    screenMap.put(name, pane);
  }

  /**
   * This method changes scene to desired pane
   * 
   * @param name
   */
  protected void activate(String name) {
    main.setRoot(screenMap.get(name));
  }

  /**
   * Generate a quiz with given topic and the amount of questions
   * 
   * @param topic  is the topic user chooses
   * @param amount is the amount of questions user wants
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ParseException
   */
  public void generateQuiz(String topic, int amount)
      throws FileNotFoundException, IOException, ParseException {
    quizGenerator.generateQuiz(topic, amount);
  }

  /**
   * This method sets up the add screen when Add button is clicked
   * 
   * @param pane is the pane we are setting up
   */
  public void setUpAddScreen(BorderPane pane) {
    saved = false; // update new change so that user will be asked to save
    VBox vbox = new VBox();
    TextField textField; // common textField reference to use in this method

    // Set the text at the top
    Text text = new Text("Add new question");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    pane.setTop(text);

    // initialize a HBox for text of the question
    // and add to the vbox
    HBox hbox = new HBox();
    TextField question = new TextField(); // text field for question
    question.setPromptText("Type in question");
    hbox.getChildren().addAll(new Text("Text: "), question);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER); // align to the center
    // initialize a new HBox for topic of the question
    hbox = new HBox();
    TextField topic = new TextField(); // text field for topic
    topic.setPromptText("Type in topic");
    hbox.getChildren().addAll(new Text("Topic: "), topic);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    // initialize a new HBox for Image file name of the question
    hbox = new HBox();
    TextField image = new TextField();
    image.setPromptText("Type in image name"); // text field for image path
    hbox.getChildren().addAll(new Text("Image: "), image);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    // initialize a new HBox for texts
    hbox = new HBox();
    hbox.getChildren().add(new Text("Choices: "));
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);

    // toggle group of radio buttons so that only one selection can be chosen
    ToggleGroup group = new ToggleGroup();
    // five radio buttons corresponding to five text fields
    RadioButton button1 = new RadioButton();
    RadioButton button2 = new RadioButton();
    RadioButton button3 = new RadioButton();
    RadioButton button4 = new RadioButton();
    RadioButton button5 = new RadioButton();
    // add buttons into togglegroup
    button1.setToggleGroup(group);
    button2.setToggleGroup(group);
    button3.setToggleGroup(group);
    button4.setToggleGroup(group);
    button5.setToggleGroup(group);
    button1.setSelected(true);

    // set up the text fields for user input
    TextField choice1 = new TextField();
    TextField choice2 = new TextField();
    TextField choice3 = new TextField();
    TextField choice4 = new TextField();
    TextField choice5 = new TextField();
    choice1.setPromptText("Choice 1");
    hbox = new HBox();
    hbox.getChildren().setAll(button1, choice1);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    choice2.setPromptText("Choice 2");
    hbox = new HBox();
    hbox.getChildren().setAll(button2, choice2);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    choice3.setPromptText("Choice 3");
    hbox = new HBox();
    hbox.getChildren().setAll(button3, choice3);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    choice4.setPromptText("Choice 4");
    hbox = new HBox();
    hbox.getChildren().setAll(button4, choice4);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    choice5.setPromptText("Choice 5");
    hbox = new HBox();
    hbox.getChildren().setAll(button5, choice5);
    vbox.getChildren().add(hbox);
    hbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(10);

    // set up the buttons and their functionality
    Button saveButton = new Button("Add");
    Button cancelButton = new Button("Cancel");
    hbox = new HBox();
    hbox.getChildren().addAll(saveButton, cancelButton);
    hbox.setSpacing(10);
    hbox.setAlignment(Pos.CENTER_RIGHT);

    // when save button is clicked, check if any required field is empty
    // create a new question and save to question bank
    saveButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root);
        String[] choiceArray = new String[5];
        String answer;
        choiceArray[0] = choice1.getText();
        choiceArray[1] = choice2.getText();
        choiceArray[2] = choice3.getText();
        choiceArray[3] = choice4.getText();
        choiceArray[4] = choice5.getText();

        // if question text field or topic field is empty
        // show alert
        if (question.getText().isEmpty() || topic.getText().isEmpty()) {
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("Empty Text Field!");
          alert.setContentText("Question Text can not be empty");
          activate("add");
          setupScreens("add");
          alert.showAndWait();
          return;
        }
        // image field is not required, if empty, set to "none"
        if (image.getText().isEmpty()) {
          image.setText("none");
        }

        // find the correct choice
        TextField rightTF;
        if (group.getSelectedToggle().equals(button1)) {
          rightTF = choice1;
        } else if (group.getSelectedToggle().equals(button2)) {
          rightTF = choice2;
        } else if (group.getSelectedToggle().equals(button3)) {
          rightTF = choice3;
        } else if (group.getSelectedToggle().equals(button4)) {
          rightTF = choice4;
        } else {
          rightTF = choice5;
        }

        // if correct choice is empty, display alert
        // else, set the correct answer
        if (rightTF.getText().isEmpty()) {
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("Empty Right Choice Selected!");
          alert.setContentText("Empty choice cannot be selected as correct");
          activate("add");
          setupScreens("add");
          alert.showAndWait();
          return;
        } else {
          answer = rightTF.getText();
        }

        Question newQuestion =
            new Question(question.getText(), choiceArray, image.getText(), topic.getText(), answer);
        quizGenerator.addNewQuestion(newQuestion);
      }
    });

    // if cancel button is clicked, go back to root
    cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root);
        System.out.println("Go back to root");
      }
    });

    pane.setBottom(hbox);
    pane.setAlignment(saveButton, Pos.CENTER_RIGHT);
    pane.setCenter(vbox);
    pane.setAlignment(vbox, Pos.CENTER);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
    pane.setMargin(pane.getBottom(), insets);
  }

  /**
   * This screen is in charge of reading file before loading questions
   * 
   * @param pane is the pane we are setting up
   */
  public void setUpBeforeLoadingScreen(BorderPane pane) {
    BorderPane currScreen = pane;
    Text text = new Text("Load question");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    currScreen.setTop(text); // Set top title of this screen

    HBox hbox = new HBox();
    TextField fileName = new TextField(); // Textfiled used to read file name from user
    fileName.setPromptText("e.g. questions_001.json");
    hbox.getChildren().addAll(new Text("Question file: "), fileName);
    hbox.setAlignment(Pos.CENTER);
    currScreen.setCenter(hbox);

    HBox buttons = new HBox();

    // Button used to load questions from file
    Button loadButton = new Button("Load");
    loadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        inputFileName = fileName.getText();
        try {
          if (!filesOpened.contains(inputFileName)) { // Don't read files have been read in
            // Add questions to question bank in generator
            quizGenerator.addQuestionFromFile(inputFileName);
            filesOpened.add(inputFileName);
            setupScreens("load1");
            activate("load1");
            saved = false;
          } else { // Warn user that the file entered has been read in already
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("The file has been read");
            alert.setContentText("Please enter a different file");
            alert.showAndWait();
            fileName.clear();
          }
        } catch (IOException | ParseException e) { // Handle exception
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("Cannot open or read file");
          alert.setContentText("Please check the file name you entered");
          alert.showAndWait();
          fileName.clear();
        }
      }
    });

    // Cancel reading file and go back to root
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        System.out.println("go back");
        main.setRoot(root);
      }
    });


    buttons.getChildren().addAll(loadButton, cancelButton);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.setSpacing(10);
    currScreen.setBottom(buttons);
    currScreen.setAlignment(buttons, Pos.CENTER_RIGHT);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
    pane.setMargin(pane.getBottom(), insets);
  }

  /**
   * Load1 screen asks user the desired topic and amount of questions
   * 
   * @param pane is the pane we are setting up
   */
  public void setUpLoad1Screen(BorderPane pane) {
    VBox vbox = new VBox();
    BorderPane currScreen = pane;
    Text text = new Text("Load question");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    currScreen.setTop(text);

    // Read all topics as an array
    Object[] topicArray = quizGenerator.getTopicList().toArray();
    Arrays.sort(topicArray); // Sort topics by alphabetical order
    // Number of all questions with any topic
    int totalNum = quizGenerator.getQuestionBank().size();

    // Push array into observable list which is easier to use in combo box
    ObservableList<String> topics = FXCollections.observableArrayList((String) topicArray[0]);
    if (topicArray.length > 1) {
      for (int i = 1; i < topicArray.length; i++) {
        topics.add((String) topicArray[i]);
      }
    }

    // Combo box lists all topics
    final ComboBox topicComboBox = new ComboBox(topics);
    topicComboBox.getSelectionModel().selectFirst();
    HBox hbox = new HBox(); // hbox for topic prompt
    hbox.getChildren().addAll(new Text("Topic: "), topicComboBox);
    HBox numberQuestionHBox = new HBox(); // hbox for number of question prompt
    TextField questionNum = new TextField();
    numberQuestionHBox.getChildren().addAll(new Text("# of Questions: "), questionNum);
    hbox.setAlignment(Pos.CENTER);
    numberQuestionHBox.setAlignment(Pos.CENTER);

    vbox.getChildren()
        .add(new Text("Total number of questions with all topics avaliable: " + totalNum));
    vbox.getChildren().add(hbox);
    vbox.getChildren().add(numberQuestionHBox);


    vbox.setSpacing(10);
    vbox.setAlignment(Pos.CENTER);
    currScreen.setCenter(vbox);

    HBox buttons = new HBox(); // Buttons
    Button backButton = new Button("Cancel");
    backButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root); // Cancel button brings user back to root screen
      }
    });

    Button loadButton = new Button("Start"); // Start the quiz
    loadButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        int amountLimit =
            quizGenerator.getNumberOfQuestionsInTopic((String) topicComboBox.getValue());
        try {
          if (questionNum.getText().isEmpty()) { // catch empty input
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("You must enter an amount of questions");
            alert.showAndWait();
          } else if (Integer.parseInt(questionNum.getText()) <= 0) { // catch negative input
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Invalid Number");
            alert.setHeaderText("Number of questions must be a positive number!");
            alert.showAndWait();
            questionNum.clear();
          } else {
            int requestNum = Integer.parseInt(questionNum.getText());
            if (requestNum > amountLimit) { // Requested an amount is greater than the capacity
              // Warn user and still give questions with maximum capacity
              Alert alert = new Alert(AlertType.INFORMATION);
              alert.setTitle("Notice");
              alert.setHeaderText("Excessive question amount");
              alert.setContentText("You entered an amount is more than questions we have,\n"
                  + "you will only be able to take " + amountLimit + " question(s) this time");
              alert.showAndWait();
              requestNum = amountLimit;
            }
            try { // Catch exception occurs in combo box
              generateQuiz((String) topicComboBox.getValue(), requestNum);
            } catch (IOException | ParseException e) {
              Alert alert = new Alert(AlertType.INFORMATION);
              alert.setTitle("Alert");
              alert.setHeaderText("Unexcepted exception occured");
              alert.showAndWait();
            }
            // Quiz is ready to go
            setupScreens("load2");
            activate("load2");
            count = 0; // Set count of answered questions to be 0
          }
        } catch (NumberFormatException e) { // Catch invalid input (non-digit)
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("You must enter a number");
          alert.showAndWait();
          questionNum.clear();
        }
      }
    });

    buttons.getChildren().addAll(loadButton, backButton);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.setSpacing(10);
    currScreen.setBottom(buttons);
    currScreen.setAlignment(buttons, Pos.CENTER_RIGHT);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
    pane.setMargin(pane.getBottom(), insets);
  }

  /**
   * Load2 screen is where user does the quiz questions
   * 
   * @param pane is the pane we are setting up
   */
  public void setUpLoad2Screen(BorderPane pane) {
    count = 0;
    
    // List of selected questions
    List<Question> quizQuestion = quizGenerator.getQuiz().getQuizQuestion();

    // Toggle group used to detect user's choice
    ToggleGroup answergroup = showQuestion(pane, quizQuestion);

    HBox hbox = new HBox();

    Button submit = new Button("Submit"); // Finish quiz immediately
    Button next = new Button("Next"); // Go to next question
    hbox.getChildren().addAll(submit, next);
    hbox.setAlignment(Pos.CENTER_RIGHT);
    pane.setBottom(hbox);
    hbox.setSpacing(10);

    // Submit and next question button doesn't apply if user didn't choose anything
    
    // Finish quiz and jump to result page
    submit.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        RadioButton selectedRadioButton = (RadioButton) answergroup.getSelectedToggle();
        if (selectedRadioButton != null) {  // Make sure user chose one answer
          String toogleGroupValue = selectedRadioButton.getText();
          grade(toogleGroupValue, quizQuestion.get(count));
          setupScreens("next");
          activate("next");
        } else { // Warn user that it's required to select one choice
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("You must select one choice before move on");
          alert.showAndWait();
        }
      }
    });

    // Move on to next questions. Most detections are similar with submit button
    // Once all questions have been answered it jumps to result page
    next.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        RadioButton selectedRadioButton = (RadioButton) answergroup.getSelectedToggle();
        if (selectedRadioButton != null) {
          String toogleGroupValue = selectedRadioButton.getText();
          grade(toogleGroupValue, quizQuestion.get(count));
          if (count == quizQuestion.size() - 1) {
            setupScreens("next");
            activate("next");
          } else {
            count++;
            showQuestion(pane, quizQuestion);
          }
        } else {
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("You must select one choice before move on");
          alert.showAndWait();
        }
      }
    });

    pane.setMargin(pane.getBottom(), insets);

  }

  /**
   * Private helper to grade each question
   * @param choice is the choice user chose
   * @param question is the current question
   */
  private void grade(String choice, Question question) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Result");
    if (choice.equals(question.getCorrect())) {
      alert.setHeaderText("Correct!"); // Shows user if the current choice is correct
      quizGenerator.getQuiz().pointIncrement();
    } else {
      alert.setHeaderText("Incorrect!");
    }
    alert.setContentText("Click on the button below to move on when you are ready!");
    alert.showAndWait();
  }

  /**
   * Next screen means the result right after the quiz
   * @param pane is the pane we are working on
   */
  public void setUpNextScreen(BorderPane pane) {
    Text text = new Text("Result");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    pane.setTop(text);
    VBox vbox = new VBox();
    // Add number of correctly answered questions
    vbox.getChildren().add(new Text("Correct: " + quizGenerator.getQuiz().getCorrect()));
    // Add number of total answered questions
    vbox.getChildren().add(new Text("Questions Answered: " + (count + 1)));
    // Show score in percentage
    vbox.getChildren()
        .addAll(new Text("Score:  " + Double.toString(quizGenerator.getQuiz().getScore()) + " %"));
    pane.setCenter(vbox);
    pane.setAlignment(vbox, Pos.CENTER);
    pane.setMargin(vbox, insets);

    // Choices
    HBox resultChoice = new HBox();
    // Change topic and numbers
    Button changeSetting = new Button("Change setting");
    // Try the same quiz again
    Button tryAgain = new Button("Try Again");
    // Go back to root screen
    Button quit = new Button("Finish");
    resultChoice.getChildren().addAll(changeSetting, tryAgain, quit);

    changeSetting.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        setupScreens("load1");
        activate("load1");
      }
    });

    tryAgain.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        setupScreens("load2");
        activate("load2");
      }
    });

    quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root);
      }
    });

    resultChoice.setAlignment(Pos.CENTER_RIGHT);
    pane.setBottom(resultChoice);
    resultChoice.setSpacing(10);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
    pane.setMargin(pane.getBottom(), insets);
  }

  /**
   * The screen where user saves question bank
   * @param pane is the pane we are working on
   */
  public void setUpSaveScreen(BorderPane pane) {
    HBox hbox = new HBox();
    Button save = new Button("Save");
    Button cancel = new Button("Cancel");
    hbox.getChildren().addAll(save, cancel);
    hbox.setAlignment(Pos.CENTER_RIGHT);
    hbox.setSpacing(10);

    pane.setBottom(hbox);

    VBox vbox = new VBox();
    TextField fileName = new TextField();
    // Prompt for a file name
    fileName.setPromptText("Enter a valid file name");
    vbox.getChildren().addAll(new Text("Filename:"), fileName);
    Text text = new Text("Save");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    pane.setTop(text);
    pane.setCenter(vbox);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
    pane.setMargin(pane.getBottom(), insets);

    // Saving action
    save.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        if (fileName.getText().isEmpty()) {
          Alert alert = new Alert(AlertType.INFORMATION);
          alert.setTitle("Alert");
          alert.setHeaderText("You must enter a file name");
          alert.showAndWait();
        } else {
          try {
            quizGenerator.save(fileName.getText());
            saved = true;
          } catch (Exception e) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Alert");
            alert.setHeaderText("Please enter a valid JSON file name.");
            alert.showAndWait();
            fileName.clear();
          }
          main.setRoot(root);
          if (needQuit) { // If user enters saving page after clicking on exit, quit.
            stage.close();
          }
        }
      }
    });

    cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root); // Go back to root without any action
      }
    });
  }

  /**
   * Exit screen
   * @param pane
   */
  public void setUpExitScreen(BorderPane pane) {
    VBox vbox = new VBox();
    HBox hbox = new HBox();
    Text text = new Text("Quit");
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    pane.setTop(text);
    Text exitMessage = new Text("Would you like to save questions?");
    exitMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    Button saveQuit = new Button("Save");
    Button noSaveQuit = new Button("Don't save");
    Button cancelQuit = new Button("Cancel");
    hbox.getChildren().addAll(saveQuit, noSaveQuit, cancelQuit);
    hbox.setAlignment(Pos.CENTER);
    hbox.setSpacing(10);
    vbox.getChildren().addAll(exitMessage, hbox);
    vbox.setSpacing(20);
    vbox.setAlignment(Pos.CENTER);

    // Detects if all changes have been saved.
    // This boolean variable is always changing properly according to user's action
    if (saved) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Note");
      alert.setHeaderText("There is no change not been saved. Click on button below to close.");
      alert.showAndWait();
      stage.close();
    }

    // Call save action
    saveQuit.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        setupScreens("save");
        activate("save");
        needQuit = true;
      }
    });

    // Close directly
    noSaveQuit.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        stage.close();
      }
    });

    // Cancel exit action
    cancelQuit.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        main.setRoot(root);
      }
    });
    pane.setCenter(vbox);
    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);
  }

  /**
   * Set up screens according to user input
   * @param name is the name of screen which user is calling
   */
  public void setupScreens(String name) {
    switch (name) {
      case "add":
        this.setUpAddScreen(screenMap.get(name));
        break;
      case "load1":
        this.setUpLoad1Screen(screenMap.get(name));
        break;
      case "load2":
        this.setUpLoad2Screen(screenMap.get(name));
        break;
      case "next":
        this.setUpNextScreen(screenMap.get(name));
        break;
      case "save":
        this.setUpSaveScreen(screenMap.get(name));
        break;
      case "exit":
        this.setUpExitScreen(screenMap.get(name));
        break;
      case "beforeLoading":
        this.setUpBeforeLoadingScreen(screenMap.get(name));
        break;
    }

  }

  /**
   * Method to show question such that we can generate new questions easier.
   * @param pane
   * @param quizQuestion
   * @return toggle group of answers
   */
  public ToggleGroup showQuestion(BorderPane pane, List<Question> quizQuestion) {

    BorderPane currentScreen = pane;
    Text text = new Text("Quiz Question #" + (count + 1));
    text.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
    
    // Record quiz progress
    VBox recordBox = new VBox();
    recordBox.getChildren().add(text);
    recordBox.getChildren().add(new Text("Answered: " + count));
    recordBox.getChildren().add(new Text("Total: " + quizQuestion.size()));
    recordBox.setAlignment(Pos.CENTER_LEFT);
    currentScreen.setTop(recordBox);
    
    HBox hbox = new HBox();
    Text questionText = new Text(10, 20, quizQuestion.get(count).getQuestion());
    questionText.setWrappingWidth(500);
    
    VBox vbox = new VBox();
    vbox.getChildren().add(questionText);
    String[] choices = quizQuestion.get(count).getChoices();

    // Image part
    String imagePath = quizQuestion.get(count).getImage();
    ImageView image;
    try {
      if (!imagePath.equals("none")) {
        image = new ImageView(imagePath);
      } else { // leave an empty frame
        image = new ImageView();
      }
    } catch (IllegalArgumentException e) { // Cannot open image. Inform user and shows blank
      image = new ImageView("invalidImage.jpg");
    }
    image.setFitHeight(200);
    image.setFitWidth(200);
    vbox.getChildren().add(image);

    ToggleGroup answergroup = new ToggleGroup();
    RadioButton answerbutton = new RadioButton();
    answerbutton.setToggleGroup(answergroup);

    hbox.setAlignment(Pos.CENTER); // Generate choices
    for (int i = 0; i < choices.length; i++) {
      hbox = new HBox();
      RadioButton button = new RadioButton(choices[i]);
      button.setToggleGroup(answergroup);
      vbox.getChildren().add(button);
    }
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(10);

    currentScreen.setCenter(vbox);

    pane.setMargin(pane.getTop(), insets);
    pane.setMargin(pane.getCenter(), insets);

    return answergroup;
  }



}
