package me.subtypezero.games.client.gui;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import me.subtypezero.games.client.entity.Player;

public class ResultPane extends TitledPane {
	Pane pane;

	public ResultPane(String title) {
		super(title, new Pane());

		pane = new Pane();
		pane.setPrefSize(91, 320);
		setContent(pane);
		setCollapsible(false);
	}

	public void addResult(Player player, String result) {

	}

	public void clearResults() {

	}
}