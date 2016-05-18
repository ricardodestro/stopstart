import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class StopStart extends Application {

	public static void main(String[] args) {

		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Window label
		Label label = new Label("Control");
		label.setTextFill(Color.DARKBLUE);
		label.setFont(Font.font("Calibri", FontWeight.BOLD, 24));

		HBox labelHb = new HBox();
		labelHb.setAlignment(Pos.CENTER);
		labelHb.getChildren().add(label);

		// Jetty Button
		Button jettyBtn = new Button("Jetty OFF");
		jettyBtn.setOnAction(new ChoiceButtonListener("/usr/local/jetty/bin/jetty.sh"));

		// Sonar Button
		Button sonarBtn = new Button("Sonar OFF");
		sonarBtn.setOnAction(new ChoiceButtonListener("/usr/local/sonarqube-5.1.2/bin/linux-x86-64/sonar.sh"));

		HBox buttonHb = new HBox(10);
		buttonHb.setAlignment(Pos.CENTER);
		buttonHb.getChildren().add(jettyBtn);
		buttonHb.getChildren().add(sonarBtn);

		// Vbox
		VBox vbox = new VBox(30);
		vbox.setPadding(new Insets(25, 25, 25, 25));
		vbox.getChildren().addAll(labelHb, buttonHb);

		// Scene
		Scene scene = new Scene(vbox, 300, 200); // w x h
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void runChoice(String baseCommand, String command) throws Exception {
		Process p = null;

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setHeaderText("Message");

		p = Runtime.getRuntime().exec(baseCommand + " " + command);

		if (p != null) {
			p.waitFor();

			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = b.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}

			alert.setContentText(sb.toString());
		}

		alert.showAndWait();
	}

	private class ChoiceButtonListener implements EventHandler<ActionEvent> {

		private String baseCommand;

		public ChoiceButtonListener(String baseCommand) {
			this.baseCommand = baseCommand;
		}

		@Override
		public void handle(ActionEvent e) {
			try {
				Button btn = (Button) e.getSource();

				String command = null;

				if (btn.getText().indexOf("OFF") != -1) {
					command = "start";
					btn.setText(btn.getText().replace("OFF", "WAITING"));
				} else if (btn.getText().indexOf("ON") != -1) {
					command = "stop";
					btn.setText(btn.getText().replace("ON", "WAITING"));
				} else {
					return;
				}

				btn.setDisable(true);
				runChoice(baseCommand, command);

				switch (command) {
				case "start":
					btn.setText(btn.getText().replace("WAITING", "ON"));
					break;
				case "stop":
					btn.setText(btn.getText().replace("WAITING", "OFF"));
					break;
				}

				btn.setDisable(false);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}
}
