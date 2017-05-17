package me.subtypezero.games.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.subtypezero.games.client.Handler;

public class Dialog extends BorderPane {
	private Handler handler;
	private Stage stage;
	private Scene scene;
	private TextField betField;

	public Dialog(Handler handler) {
		this.handler = handler;

		HBox textPane = new HBox(5);
		textPane.setAlignment(Pos.CENTER_LEFT);
		textPane.setPadding(new Insets(5));

		Label betLabel = new Label("Bet");
		betField = new TextField();
		betField.setAlignment(Pos.CENTER_RIGHT);
		betField.setPrefColumnCount(3);
		betField.setEditable(false);
		betField.setDisable(true);
		betField.setText("" + (handler.getMinBet())); // Start bet at minimum
		textPane.getChildren().addAll(betLabel, betField);

		HBox buttonPane = new HBox(5);
		buttonPane.setAlignment(Pos.CENTER_RIGHT);
		buttonPane.setPadding(new Insets(5));

		Button submit = new Button("Submit");
		submit.setOnAction(e -> setBet());
		buttonPane.getChildren().add(submit);

		HBox addPane = new HBox(5);
		addPane.setAlignment(Pos.CENTER_LEFT);
		addPane.setPadding(new Insets(0, 0, 0, 5));
		Label addLabel = new Label("Add");
		Button addOne = new Button("1");
		addOne.setOnAction(e -> add(1));
		Button addTwo = new Button("5");
		addTwo.setOnAction(e -> add(5));
		Button addThree = new Button("25");
		addThree.setOnAction(e -> add(25));
		Button addFour = new Button("100");
		addFour.setOnAction(e -> add(100));
		addPane.getChildren().addAll(addLabel, addOne, addTwo, addThree, addFour);

		HBox subPane = new HBox(5);
		subPane.setAlignment(Pos.CENTER_LEFT);
		subPane.setPadding(new Insets(0, 0, 5, 5));
		Label subLabel = new Label("Sub");
		Button subOne = new Button("1");
		subOne.setOnAction(e -> sub(1));
		Button subTwo = new Button("5");
		subTwo.setOnAction(e -> sub(5));
		Button subThree = new Button("25");
		subThree.setOnAction(e -> sub(25));
		Button subFour = new Button("100");
		subFour.setOnAction(e -> sub(100));
		subPane.getChildren().addAll(subLabel, subOne, subTwo, subThree, subFour);

		VBox optionPane = new VBox(5);
		optionPane.setPadding(new Insets(5));
		optionPane.getChildren().addAll(addPane, subPane);

		this.setLeft(textPane);
		this.setRight(buttonPane);
		this.setBottom(optionPane);

		scene = new Scene(this, 160, 90);
		scene.getStylesheets().add("style.css");

		stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Place your bet!");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL); // Make the stage pop up


	}

	private void add(int value) {
		handler.setBet(handler.getBet() + value);
		betField.setText("" + handler.getBet());
	}

	private void sub(int value) {
		handler.setBet(handler.getBet() - value);
		betField.setText("" + handler.getBet());
	}

	public void show() {
		stage.showAndWait();
	}

	private void setBet() {
		handler.setBetPlaced(true);
		stage.close();
	}
}
