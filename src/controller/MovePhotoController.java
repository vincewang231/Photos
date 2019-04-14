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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Album;
import objects.Photo;
import objects.User;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Moves a photo from the source to a specified album
 *
 */
public class MovePhotoController {


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
	 * Setups the move photo page
	 * @param mainStage The current stage
	 * @param user The current user
	 * @param album The current album
	 * @param photo The selected album
	 */
	public void start(Stage mainStage, User user, Album album, Photo photo) {
		currStage = mainStage;
		this.currUser = user;
		this.currPhoto = photo;
		this.currAlbum = album;
		importAlbums();

	}

	/**
	 * Imports all the users album to be put on a checkbox
	 */

	private void importAlbums(){
		myCheckboxes = new ArrayList<CheckBox>();
		int maxSel = 1;

		for(int i =0; i < this.currUser.getUserAlbums().size();i++){

			if(!(currUser.getUserAlbums().get(i).getAlbumName().equalsIgnoreCase(currAlbum.getAlbumName()))){
				CheckBox cb = new CheckBox();
				myCheckboxes.add(cb);
				cb.setText(currUser.getUserAlbums().get(i).getAlbumName());
				indivCheckbox.getChildren().add(cb);
			}
		}
		for (int i = 0 ; i < myCheckboxes.size();i++)
			myCheckboxes.get(i).selectedProperty().addListener( (o, oldV, newV) -> {
				if(newV) {
					int sel = 0;
					for(CheckBox cb : myCheckboxes)
						if(cb.isSelected())
							sel++;

					((WritableBooleanValue) o).set(sel <= maxSel);
				}
			});


	}

	/**
	 * Handles button request
	 * @param evt A button request
	 */


	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();
		if(button == backButton){
			currStage.close();
		}else if(button == submitButton){
			for(int j =0; j <myCheckboxes.size();j++){
				if(myCheckboxes.get(j).isSelected()){
					CheckBox selected = myCheckboxes.get(j);
					String newAlbumName = selected.getText();
					if(!(newAlbumName.equalsIgnoreCase(currAlbum.getAlbumName()))){
						Album newAlbum = findAlbum(newAlbumName);
						if(nameChecker(currPhoto.getNameOfPhoto(),newAlbum)){
							currAlbum.getArrPhotos().remove(currPhoto);
							newAlbum.addPhotoToAlbum(currPhoto);
							if(currAlbum.getArrPhotos().size() > 0){
								Collections.sort(currAlbum.getArrPhotos(), new DateSortComparator());
								currAlbum.updateStartDate();
								currAlbum.updateEndDate();
							}
							Collections.sort(newAlbum.getArrPhotos() ,new DateSortComparator());
							newAlbum.updateStartDate();
							newAlbum.updateEndDate();
							currStage.close();
						}



					}else{//ERROR
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Moving");
						alert.setHeaderText("Invalid Input");
						alert.setContentText("Can Not Move To Same Album");
						alert.showAndWait();
					}
				}
			}
		}
	}

	/**
	 * Finds an album from a given name
	 * @param name The album name
	 * @return Returns an album if one exists
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
	 * Check to see if the photo can be moved to that specified album
	 * @param photoName The name of the photo
	 * @param album The name of the album
	 * @return Valid if the photo can be moved
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
			alert.setContentText("Can Not Move To Album With A Photo Having The Same Name - Did Not Move To The Album - " + album.getAlbumName());
			alert.showAndWait();
			return false;

		}
	}

	/**
	 * Loops through photos an album to see if there are duplicates
	 * @param name The name of the photo
	 * @param album The album of photo
	 * @return Valid if there are no duplicates
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
