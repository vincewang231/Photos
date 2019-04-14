package controller;

import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.WritableBooleanValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Album;
import objects.Photo;
import objects.User;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Copy a photo into multiple albums
 *
 */

public class CopyPhotoController {



	Stage currStage;
	User currUser;
	Photo currPhoto;
	Album currAlbum;

	ArrayList <CheckBox> myCheckboxes;
	@FXML
	private Button submitButton;
	@FXML
	private Button backButton;
	@FXML
	private ScrollPane checkBoxDisplay;
	@FXML
	private VBox indivCheckbox;



	/**
	 * Sets up the copy photo page
	 * @param mainStage The current stage
	 * @param user The current user
	 * @param album The current album
	 * @param photo The selected photo
	 */

	public void start(Stage mainStage, User user, Album album, Photo photo) {
		currStage = mainStage;
		this.currUser = user;
		this.currPhoto = photo;
		this.currAlbum = album;
		importAlbums();

	}


	/**
	 * imports the user's albums into a checklist
	 */

	private void importAlbums(){
		myCheckboxes = new ArrayList<CheckBox>();

		for(int i =0; i < this.currUser.getUserAlbums().size();i++){

			if(!(currUser.getUserAlbums().get(i).getAlbumName().equalsIgnoreCase(currAlbum.getAlbumName()))){
				CheckBox cb = new CheckBox();
				myCheckboxes.add(cb);
				cb.setText(currUser.getUserAlbums().get(i).getAlbumName());
				indivCheckbox.getChildren().add(cb);
			}
		}
	}

	/**
	 * Handles any button request
	 * @param evt Button event
	 */

	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == backButton){
			currStage.close();
		}else if(button == submitButton){
			for(int j =0; j <myCheckboxes.size();j++){
				if(myCheckboxes.get(j).isSelected()){
					if(myCheckboxes.get(j).isSelected()){
						CheckBox selected = myCheckboxes.get(j);
						String newAlbumName = selected.getText();
						if(!(newAlbumName.equalsIgnoreCase(currAlbum.getAlbumName()))){
							Album newAlbum = findAlbum(newAlbumName);

							if(nameChecker(currPhoto.getNameOfPhoto(),newAlbum)){
								Photo copyOfPhoto = new Photo(currPhoto.getFilename(), newAlbumName ,currPhoto.getArrPhotoTags());
								copyOfPhoto.setNameOfPhoto(currPhoto.getNameOfPhoto());
								copyOfPhoto.setCaption(currPhoto.getCaption());
								newAlbum.addPhotoToAlbum(copyOfPhoto);
								Collections.sort(newAlbum.getArrPhotos() ,new DateSortComparator());
								newAlbum.updateStartDate();
								newAlbum.updateEndDate();
								currStage.close();
							}

						}
					}

				}

			}
		}
	}

	/**
	 * Find an album with a given name, returns that album if found
	 * @param name The name of the album you are trying to find
	 * @return An album if found
	 */
	private Album findAlbum(String name){
		for(int i =0; i < currUser.getUserAlbums().size();i++){
			if(currUser.getUserAlbums().get(i).getAlbumName().equalsIgnoreCase(name)){
				return currUser.getUserAlbums().get(i);
			}
		}
		return null;
	}


	/**
	 * Checks to see if the album you want this photo to be copied to is a valid copy
	 * @param photoName The name of the photo
	 * @param album The album that you want to copy this photo to
	 * @return Valid if it can be copied over
	 */


	private boolean nameChecker(String photoName,Album album){// CHECK FOR DUPLICATE NAME


		if(photoDuplicateCheck(photoName, album)){ // NO DUPLICATE
			if(photoName.equals("")){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please Enter a Different Photo Name");
				alert.showAndWait();
				return false;
			}
			return true;

		}
		else{ //DUPLICATE PHOTO NAME
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Moving");
			alert.setHeaderText("Invalid Move");
			alert.setContentText("Can Not Move To Album With A Photo Having The Same Name - Did Not Copy To The Album - " + album.getAlbumName());
			alert.showAndWait();
			return false;

		}


	}

	/**
	 * Loops through each photo to see if any duplicates
	 * @param name The name of the photo
	 * @param album The album that you want this photo to be copied to
	 * @return Valid if no duplicates
	 */

	private boolean photoDuplicateCheck(String name , Album album){
		ArrayList<Photo> currUserPhotos = album.getArrPhotos();
		for(int i =0; i < currUserPhotos.size() ; i++){
			if(currUserPhotos.get(i).getNameOfPhoto().equalsIgnoreCase(name)){
				return false;
			}
		}
		return true;
	}





}
