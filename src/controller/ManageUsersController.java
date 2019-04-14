package controller;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import objects.User;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Opens the manage users page for the admin only
 *
 */
public class ManageUsersController {

	ArrayList<User> currUserList;
	Stage currStage;
	private ObservableList<String> users;

	@FXML
	private Button add;
	@FXML
	private Button edit;
	@FXML
	private Button logout;
	@FXML
	private Button delete;
	@FXML
	private ListView<String> userList;


	/**
	 * Setups the manage users page
	 * @param mainStage The current stage
	 * @param userList The list of all users
	 */

	public void start(Stage mainStage,ArrayList <User> userList) {
		this.currStage = mainStage;
		this.currUserList = userList;
		add.requestFocus();


		importUserList();


	}


	/**
	 * Handles all button requests
	 * @param evt Button request
	 */
	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == add){
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Add A New User");
			dialog.setHeaderText("Adding A New User");
			dialog.setContentText("Name Of New Username:");
			dialog.showAndWait();

			String newUserName = dialog.getResult();
			if(newUserName == null){
				return;
			}

			User newUser = new User(newUserName);
			if(newUserName.equals("")){ //INVALID INPUT
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
					newUser.createStartingAccount();
					currUserList.add(newUser);
					users.add(newUser.getUsername());
					userList.setItems(users);

				}

			}
		}
		else if(button == edit){
			int index = userList.getSelectionModel().getSelectedIndex();


			if(index == -1){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Selection Error");
				alert.setHeaderText("No Selection");
				alert.showAndWait();
				return;
			}


			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Change Username");
			dialog.setHeaderText("Change Selected Username");
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
					User temp = currUserList.get(index);
					temp.setUsername(newUserName);
					importUserList();



				}

			}
		}
		else if(button == delete){
			int index = userList.getSelectionModel().getSelectedIndex();
			if(index == -1){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Selection Error");
				alert.setHeaderText("No Selection");
				alert.showAndWait();
				return;
			}

			Alert delete = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + userList.getSelectionModel().getSelectedItem(), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			delete.showAndWait();

			if(delete.getResult() == ButtonType.YES){
				currUserList.remove(index);
				users.remove(index);
				userList.setItems(users);
			}


		}
		else if(button == logout){
			currStage.close();
		}


	}

	/**
	 * Loops through all users to see if there are duplicates
	 * @param newUser The username that is passed
	 * @return Valid if there is already a user with that name
	 */

	private boolean alreadyExist(User newUser){
		for(int i =0; i < currUserList.size();i++){
			if(currUserList.get(i).getUsername().equals(newUser.getUsername())){
				return true;
			}
		}
		return false;
	}


	/**
	 * Imports all the users onto a ListView
	 */

	private void importUserList(){
		users = FXCollections.observableArrayList();
		for(int i =0; i < currUserList.size(); i++){
			users.add(currUserList.get(i).getUsername());

		}
		userList.setItems(users);

	}


}
