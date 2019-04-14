package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import objects.User;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Creation of a new account
 *
 */
public class NewUserPageController {
	ArrayList<User> currUserList;
	Stage currStage;
	int totalUsers;


	@FXML
	private Button newUser;
	@FXML
	private TextField newUserName;


	/**
	 * Setups the new user page
	 * @param mainStage the current stage
	 * @param userList the list of users
	 */

	public void start(Stage mainStage,ArrayList <User> userList) {
		currStage = mainStage;
		this.currUserList = userList;
		newUserName.requestFocus();


	}


	/**
	 * Handles all button requests
	 * @param evt Button request
	 */


	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == newUser){
			String nameOfNewUser = newUserName.getText();
			User newUser = new User(nameOfNewUser);
			if(nameOfNewUser.equals("")){ //INVALID INPUT
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please Enter a New Username");
				alert.showAndWait();
			}
			else{
				if(alreadyExist(newUser)){ //DUPLICATE USERNAME
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Submission Error");
					alert.setHeaderText("Username Exists Already");
					alert.setContentText("Please Enter a New Username");
					alert.showAndWait();
				}
				else{ //ADD NEW USER
					this.currUserList.add(newUser);
					newUser.createStartingAccount();
					currStage.close();

				}

			}
		}
	}

	/**
	 * Loops through users to see if there are any duplicates
	 * @param newUser a newly created user object
	 * @return Valid if there is a duplicate
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
