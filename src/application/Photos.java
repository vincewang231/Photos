package application;

import controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Starts application
 */
public class Photos extends Application {

	LoginController loginController;

	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader loginLoader = new FXMLLoader();
			loginLoader.setLocation(getClass().getResource("/fxml/LoginPage.fxml"));
			AnchorPane root = (AnchorPane) loginLoader.load();
			loginController = loginLoader.getController();

			loginController.start(primaryStage);
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


	/**
	 * Exports the date when logging out or exiting
	 */

	@Override
	public void stop(){
		loginController.exportUserData();
		Platform.exit();
	}


}
