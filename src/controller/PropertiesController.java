package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.WindowEvent;
import objects.Album;
import objects.Photo;
import objects.TagTypes;
import objects.User;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Able to edit the photos data including the name, caption and tags
 *
 */
public class PropertiesController {
    Stage currStage;
    User currUser;
    Album currAlbum;
    Photo currPhoto;
    String finalChange;
	ArrayList<TagTypes> photoTags;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button tagButton;
    @FXML
    private Button editName;
    @FXML
    private Button editCaption;
    @FXML
    private TextField photoName;
    @FXML
    private TextArea caption;
    @FXML
    private TextArea date;
    @FXML
    private TextArea tagDisplay;

    /**
     * Sets up the properties page
     * @param mainStage The current stage
     * @param user The current user
     * @param currAlbum The current album
     * @param photo The selected photo
     */

    public void start(Stage mainStage, User user, Album currAlbum, Photo photo) {

        mainStage.setTitle("Properties");
        currStage = mainStage;
        this.currUser = user;
        this.currAlbum = currAlbum;
        this.currPhoto = photo;
        photoTags = photo.getArrPhotoTags();
        setupText();
    }

    /**
     * Handles all button requests
     * @param evt Button Requests
     */

    @FXML
    public void buttonPress(ActionEvent evt){
        Button button = (Button) evt.getSource();
        if(button == saveButton){
            saveEdits();
        }
        else if(button == cancelButton){
            cancelEdits();
        }
        else if(button == editName){
        	TextInputDialog dialog = new TextInputDialog();
    		dialog.setTitle("Edit Name");
    		dialog.setHeaderText("Change This Photo's Name");
    		dialog.setContentText("New Photo Name:");
    		dialog.showAndWait();

    		String newPhotoName = dialog.getResult();
    		if(newPhotoName == null){
    			return;
    		}
    		if(nameChecker(newPhotoName)){ //VALID CHANGE
    			finalChange = newPhotoName;
    			photoName.setText(finalChange);
    		}
        }
        else if(button == editCaption){
        	TextInputDialog dialog = new TextInputDialog();
    		dialog.setTitle("Edit Caption");
    		dialog.setHeaderText("Change This Photo's Caption");
    		dialog.setContentText("New Photo Caption:");
    		dialog.showAndWait();

    		String newPhotoCaption = dialog.getResult();
    		if(newPhotoCaption == null){
    			return;
    		}
    		caption.setText(newPhotoCaption);
        }
        else if(button == tagButton){
        	try {
    			FXMLLoader tagPageLoader = new FXMLLoader();
    			tagPageLoader.setLocation(getClass().getResource("/fxml/TagPage.fxml"));
    			AnchorPane root;

    			root = (AnchorPane) tagPageLoader.load();


    			Stage tagStage = new Stage();
    			tagStage.setTitle("Photo Library");
    			tagStage.setResizable(false);
    			tagStage.initModality(Modality.WINDOW_MODAL);
    			tagStage.initOwner(currStage);
    			Scene scene = new Scene(root);
    			tagStage.setScene(scene);

    			TagPageController tagPageController = tagPageLoader.getController();
    			tagPageController.start(tagStage, this.currUser, currPhoto);

    			tagStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

    				@Override
    				public void handle(WindowEvent event) {
    					Platform.runLater(new Runnable(){

    						@Override
    						public void run() {


    						}});
    				}
    			});

    			tagStage.showAndWait();
    			photoTags = tagPageController.getTags();
    			setTagField(photoTags);

        	}catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}




        }
    }

    /**
     * Saves the edits
     */

    private void saveEdits() {
    	currPhoto.setNameOfPhoto(photoName.getText());
    	currPhoto.setCaption(caption.getText());
    	currPhoto.setArrPhotoTags(photoTags);
    	currStage.close();

    }

    /**
     * Cancel any edits or leave the page
     */
    private void cancelEdits(){
    	currStage.close();

    }

    /**
     * Dispalys all data from photo within the text fields
     */
    private void setupText(){
    	photoName.setText(currPhoto.getNameOfPhoto());
    	caption.setText(currPhoto.getCaption());
    	date.setText(currPhoto.getDate().toString());
    	setTagField(currPhoto.getArrPhotoTags());
    }

    /**
     * Sets up the tag display
     * @param tags The list of tags from a photo
     */
    private void setTagField(ArrayList<TagTypes> tags){
    	tagDisplay.clear();
		String total = "";
		for(int i =0; i < tags.size();i++){
			if(tags.get(i).getTags().size() != 0){
				total += tags.get(i).getTagName();
				total += ": ";
			}
			for(int j =0;j<tags.get(i).getTags().size();j++ ){
				total+=tags.get(i).getTags().get(j);
				total+= ", ";
			}
			total += "\n";

			tagDisplay.setText(total);
		}
	}


    /**
     * Checks to see if the edits are valid
     * @param photoName The name of the photo
     * @return Valid if its a valid edit
     */
    private boolean nameChecker(String photoName){// CHECK FOR DUPLICATE NAME


		if(photoDuplicateCheck(photoName, currAlbum)){ // NO DUPLICATE
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
			alert.setHeaderText("Invalid Edit");
			alert.setContentText("Another Photo Has This Name");
			alert.showAndWait();
			return false;

		}
	}


    /**
     * Loops through all photos to find duplicates
     * @param name New name of photo
     * @param album The current album
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
