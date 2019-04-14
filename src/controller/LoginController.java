package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import application.Photos;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Handles all login requests and user creation
 *
 */
public class LoginController {
	Stage currStage;
	ArrayList<User> userList;
	int totalUsers;
	User currUser;

	@FXML
	private Button loginButton;
	@FXML
	private Button newUserButton;
	@FXML
	private TextField username;


	/**
	 * Setups the login page
	 * @param mainStage The current Stage
	 */

	public void start(Stage mainStage) {
		currStage = mainStage;
		mainStage.setTitle("Login");
		username.requestFocus();
		File usersFile = new File("userData.txt");
		File appData = new File("appData.txt");
		if(usersFile.length()==0 || appData.length()==0){

			userList = new ArrayList <User>();
			User admin = new User("admin");
			User stock = new User("stock");
			userList.add(admin);
			userList.add(stock);
			stock.createStartingAccount();
			totalUsers=2;
		}else{
			//IMPORT USERS
			userList = new ArrayList <User>();
			importUserData();
		}
	}

	/**
	 * Handles all button requests
	 * @param evt A button request
	 */


	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == loginButton){ //First will find if the user is valid
			String enteredUsername = username.getText();
			if(enteredUsername.equals(" ")){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please Enter a Different Username");
				alert.showAndWait();
			}else{
				if(alreadyExist(enteredUsername)){ //VALID USERNAME

					for(int i =0; i < userList.size();i++){
						if(userList.get(i).getUsername().equals(enteredUsername)){
							this.currUser = userList.get(i);
						}
					}

					if(enteredUsername.equals("admin")){ //MANAGE USERS
						openManageUserPage();
					}else{
						openAlbumPage();
					}
				}
				else{ //USER DOES NOT EXIST
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Submission Error");
					alert.setHeaderText("No Valid Acount");
					alert.setContentText("Please Enter a Different Username");
					alert.showAndWait();
				}
			}
		}
		else if (button == newUserButton){ //Creates a new user
			openNewUserPage();

		}
	}



	/**
	 * Opens the new user page for account creation
	 */


	private void openNewUserPage(){
		try {
			FXMLLoader newUserPageLoader = new FXMLLoader();
			newUserPageLoader.setLocation(getClass().getResource("/fxml/NewUserPage.fxml"));
			AnchorPane root = (AnchorPane) newUserPageLoader.load();
			Stage newUserStage = new Stage();
			newUserStage.setTitle("New User");
			newUserStage.setResizable(false);
			newUserStage.initModality(Modality.WINDOW_MODAL);
			newUserStage.initOwner(currStage);
			Scene scene = new Scene(root);
			newUserStage.setScene(scene);

			NewUserPageController newUserPageController = newUserPageLoader.getController();
			newUserPageController.start(newUserStage , userList);

			currStage.hide();
			newUserStage.showAndWait();
			currStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * Opens the albums page if the user name is valid
	 */
	private void openAlbumPage(){
		try{
			FXMLLoader albumLoader = new FXMLLoader();
			albumLoader.setLocation(getClass().getResource("/fxml/AlbumPage.fxml"));
			AnchorPane root = (AnchorPane) albumLoader.load();

			Stage albumStage = new Stage();
			albumStage.setTitle("Albums");
			albumStage.setResizable(false);
			albumStage.initModality(Modality.WINDOW_MODAL);
			albumStage.initOwner(currStage);
			Scene scene = new Scene(root);
			albumStage.setScene(scene);

			AlbumPageController albumPageController = albumLoader.getController();
			albumPageController.start(albumStage,this.currUser,this.userList );

			albumStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.exit();
				}
			});

			currStage.close();
			albumStage.showAndWait();
			currStage.show();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Opens the manage users page for admins
	 */
	private void openManageUserPage(){
		try{
			FXMLLoader mangeUserLoader = new FXMLLoader();
			mangeUserLoader.setLocation(getClass().getResource("/fxml/ManageUsers.fxml"));
			AnchorPane root = (AnchorPane) mangeUserLoader.load();

			Stage mangeUsersStage = new Stage();
			mangeUsersStage.setTitle("Manage Users");
			mangeUsersStage.setResizable(false);
			mangeUsersStage.initModality(Modality.WINDOW_MODAL);
			mangeUsersStage.initOwner(currStage);
			Scene scene = new Scene(root);
			mangeUsersStage.setScene(scene);

			ManageUsersController manageUsersController = mangeUserLoader.getController();
			manageUsersController.start(mangeUsersStage,this.userList );

			mangeUsersStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.exit();
				}
			});

			currStage.close();
			mangeUsersStage.showAndWait();
			currStage.show();

		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loops through all users to see if there are duplicates
	 * @param username The username that is passed
	 * @return Valid if there is already a user with that name
	 */
	private boolean alreadyExist(String username){
		for(int i =0; i < userList.size();i++){
			if(userList.get(i).getUsername().equals(username)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Imports all the user data upon a successful login. Used to keep all data saved after logout
	 *
	 */

	public void importUserData(){
		try {
			FileReader appDataReader = new FileReader("appData.txt");
			BufferedReader appDataReadBuffer = new BufferedReader(appDataReader);

			totalUsers = Integer.parseInt(appDataReadBuffer.readLine());
			appDataReadBuffer.close();

			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userData.txt"));
			for(int i =0; i < totalUsers;i++){
				try {
					User u = (User)ois.readObject();
					userList.add(u);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			ois.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 * Saves all current data when user logouts or exits app
	 */
	public void exportUserData(){
		try {
			FileWriter appData = new FileWriter("appData.txt");
			BufferedWriter appDataWriter = new BufferedWriter(appData);
			String numOfUsers = this.userList.size() + "\n";

			appDataWriter.write(numOfUsers);

			appDataWriter.close();
			appData.close();

			File usersFile = new File("userData.txt"); //KEEPS TRACK OF SPECIFIC USER DATA
			FileOutputStream userOut = new FileOutputStream(usersFile);
			ObjectOutputStream oout = new ObjectOutputStream(userOut);


			for(int i =0; i < userList.size();i++){
				for(int j =0 ; j<userList.get(i).getUserAlbums().size();j++){
					for(int k =0; k<userList.get(i).getUserAlbums().get(j).getArrPhotos().size(); k++){
						userList.get(i).getUserAlbums().get(j).getArrPhotos().get(k).clearImage();
					}
				}
			}



			for(int i =0; i < userList.size(); i++){
				User u = userList.get(i);
				oout.writeObject(u);
			}
			oout.close();


		} catch (IOException e) {
			e.printStackTrace();
		}

	}



}
