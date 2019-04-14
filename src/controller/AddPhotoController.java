package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.Photo;
import objects.TagTypes;
import objects.User;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 *
 * Import a photo into your photo library
 *
 */



public class AddPhotoController {
	Stage currStage;
	User currUser;
	Album currAlbum;
	ArrayList<User> currUserList;
	ArrayList<TagTypes> photoTags;
	File newPhotoFile;


	@FXML
	private Button selectImageButton;
	@FXML
	private Button enterTagButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button addPhotoButton;
	@FXML
	private StackPane imageDisplay;
	@FXML
	private TextField nameField;
	@FXML
	private TextField captionField;

	/**
	 * Setups stage for the addPhotosPage
	 * @param mainStage the current stage the user is on
	 * @param user the current user
	 * @param album the current album that with be opened
	 */


	public void start(Stage mainStage,User user, Album album) {

		mainStage.setTitle("Add A Photo");
		this.currStage = mainStage;
		this.currUser = user;
		this.currAlbum = album;
		selectImageButton.requestFocus();
	}



	/**
	 * Handles buttons
	 * @param evt When any button is pressed
	 *
	 */

	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();

		if(button == selectImageButton){
			newPhotoFile = selectImage();
			if (newPhotoFile != null){
				imageDisplay.getChildren().clear();
				createPhotoDisplay(newPhotoFile);
			}
		}
		else if(button == enterTagButton){
			enterTagPage();



		}
		else if(button == cancelButton){
			currStage.close();
		}
		else if(button == addPhotoButton){
			if(newPhotoFile==null){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("No File Selected");
				alert.showAndWait();

			}else{
				String photoName = getName();
				if(photoName!=null){
					String caption = captionField.getText();
					Photo newPhoto = null;
					if(photoTags == null){
						newPhoto = new Photo(newPhotoFile, currAlbum.getAlbumName());
					}else{
					 newPhoto = new Photo(newPhotoFile, currAlbum.getAlbumName(), photoTags);
					}
					newPhoto.setNameOfPhoto(photoName);
					newPhoto.setCaption(caption);
					currAlbum.addPhotoToAlbum(newPhoto);
					Collections.sort(currAlbum.getArrPhotos(), new DateSortComparator());
					currAlbum.updateStartDate();
					currAlbum.updateEndDate();
					currStage.close();
				}
			}


		}
	}




	/**
	 *
	 * Open the tag page for adding or deleting tags from photo
	 *
	 */

	private void enterTagPage(){
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
			tagPageController.start(tagStage, this.currUser, null);

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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	/**
	 * Creates a FileChooser that allows the user to select an image from their computer
	 * @return File the file for the selected photo
	 */


	private File selectImage(){
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilterJPG =
				new FileChooser.ExtensionFilter("JPG files (*.JPG)", "*.JPG");
		FileChooser.ExtensionFilter extFilterjpg =
				new FileChooser.ExtensionFilter("jpg files (*.jpg)", "*.jpg");
		FileChooser.ExtensionFilter extFilterPNG =
				new FileChooser.ExtensionFilter("PNG files (*.PNG)", "*.PNG");
		FileChooser.ExtensionFilter extFilterpng =
				new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
		fileChooser.getExtensionFilters()
		.addAll(extFilterJPG, extFilterjpg, extFilterPNG, extFilterpng);
		File file = fileChooser.showOpenDialog(null);

		if(file == null){
			return null;
		}
		return file;
	}



	/**
	 * Create the photo display of the selected file for the user to see
	 * @param file the selected file from the FileChooser
	 */

	private void createPhotoDisplay(File file){
		ImageView imgView = new ImageView();
		imgView.setX(1);
		imgView.setY(1);

		try {
			BufferedImage bufferedImage = ImageIO.read(file);
			Image image = SwingFXUtils.toFXImage(bufferedImage, null);
			imgView.setImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}

		imgView.setViewport(new Rectangle2D(360, 297, 0, 0));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(360);
		imgView.setFitHeight(297);
		imageDisplay.getChildren().add(imgView);
		StackPane.setAlignment(imgView, Pos.CENTER);
	}





	/**
	 * Grabs the Photo Name form the TextField
	 * @return String the name of the photo that has been inputed
	 */

	private String getName(){
		String photoName = nameField.getText();
		if(photoName.equals("")){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Please Enter a Different Photo Name");
			alert.showAndWait();
			return null;
		}

		if(nameChecker(photoName)){
			return photoName;
		}else{
			return null;
		}
	}





	/**
	 *
	 * @param photoName The new photo's name
	 * @return Valid if there are no duplicate photo names
	 */

	private boolean nameChecker(String photoName){// CHECK FOR DUPLICATE NAME

		if(photoDuplicateCheck(photoName)){ // NO DUPLICATE

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
		else{ //DUPLICATE ALBUM NAME
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Duplicate Name");
			alert.setContentText("Please Enter a Different Photo Name");
			alert.showAndWait();
			return false;

		}
	}







	/**
	 *
	 * @param name The Photo Name
	 * @return Valid if the name is already created in this album
	 */


	private boolean photoDuplicateCheck(String name){
		ArrayList<Photo> currUserPhotos = currAlbum.getArrPhotos();
		for(int i =0; i < currUserPhotos.size() ; i++){
			if(currUserPhotos.get(i).getNameOfPhoto().equalsIgnoreCase(name)){
				return false;
			}
		}
		return true;
	}
}