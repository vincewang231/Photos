package controller;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import objects.Album;
import objects.User;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Allows user to logout or edit username
 *
 */
public class UserOptionsController {
	LoginController loginController;
	AlbumPageController albumPageController;
	ArrayList<User> currUserList;
	Stage currStage;
	int totalUsers;
	User currUser;
	Stage albumStage;


	@FXML
	private Button logout;
	@FXML
	private Button edit;
	@FXML
	private Button cancel;
	@FXML
	private TextField display;


	/**
	 * Sets up the user options stage
	 * @param mainStage The current stage
	 * @param currUser The current user
	 * @param userList The list of users
	 * @param albumStage The album stage
	 */
	public void start(Stage mainStage,User currUser, ArrayList <User> userList, Stage albumStage) {
		this.currStage = mainStage;
		this.currUserList = userList;
		this.currUser = currUser;
		this.albumStage = albumStage;
		setTextField();

	}

	/**
	 * Handles all button requests
	 * @param evt Button Events
	 */
	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == cancel){//GO BACK TO PREV PAGE
			currStage.close();

		}
		if(button == edit){	//EDIT USERNAME
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Change Username");
			dialog.setHeaderText("Change Username");
			dialog.setContentText("New Username:");
			dialog.showAndWait();

			String newUserName = dialog.getResult();
			if(newUserName == null){
				return;
			}
			User tempUser = new User(newUserName);

			if(newUserName.equals("")){ //INVALID INPUT
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please Enter a New Username");
				alert.showAndWait();
			}
			else{
				if(alreadyExist(tempUser)){ //DUPLICATE USERNAME
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Submission Error");
					alert.setHeaderText("Username Exists Already");
					alert.setContentText("Please Enter a New Username");
					alert.showAndWait();
				}
				else{ //CHANGE NAME
					currUser.setUsername(newUserName);
					display.setText(newUserName);

				}

			}
		}
		if(button == logout){//BACK TO LOGIN PAGE
				currUser.logout = true;
				currStage.close();
		}
	}

	/**
	 * Sets the textfield with the user name
	 */
	private void setTextField(){
		display.setText(currUser.getUsername());

	}


	/**
	 * Checks to see if the changed username already exists
	 * @param newUser A user object with the new username
	 * @return Valid if there is already one created
	 */

	private boolean alreadyExist(User newUser){
		for(int i =0; i < currUserList.size();i++){
			if(currUserList.get(i).getUsername().equals(newUser.getUsername())){
				return true;
			}
		}
		return false;
	}

}
