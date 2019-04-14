package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.Glow;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import objects.Album;
import objects.User;


/**
*
* @author Tyler Falgiano and Vincent Wang
*
*
* Opens the album's page and displays all of the albums that the user owns
*
*/

public class AlbumPageController {
	Stage currStage;
	User currUser;
	Album selectedAlbum;
	ArrayList<User> currUsersList;

	@FXML
	Button addAlbumButton;
	@FXML
	Button openAlbumButton;
	@FXML
	Button renameAlbumButton;
	@FXML
	Button searchForPhotoButton;
	@FXML
	Button removeAlbumButton;
	@FXML
	Button user;
	@FXML
	ScrollPane mainWindow;
	@FXML
	VBox vbox;
	@FXML
	private AnchorPane selectedPane;



	/**
	 * Creates the album page
	 * @param mainStage the current stage the user is on
	 * @param user the current user
	 * @param currUserList a list of all the users
	 */


	public void start(Stage mainStage,User user, ArrayList<User> currUserList) {

		mainStage.setTitle("Albums");
		currStage = mainStage;
		this.currUser = user;
		this.currUsersList = currUserList;
		mainWindow.setStyle("-fx-focus-color: transparent;");

		importUserAlbumData();

	}


	/**
	 * Handles all button actions
	 * @param evt button event
	 */

	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();

		if(button == addAlbumButton){
			addAlbum();
		}
		else if(button == renameAlbumButton){
			renameAlbum();
		}
		else if(button == removeAlbumButton){
			deleteAlbum();
		}
		else if(button == openAlbumButton){
			openAlbum();
		}
		else if(button == user){
			userOptionsPage();
		}
		else if(button == searchForPhotoButton){
			searchPhotoPage();
		}
	}


	/**
	 * Creates a new album for the user
	 */
	private void addAlbum(){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add Album");
		dialog.setHeaderText("Add A New Album To Your Library");
		dialog.setContentText("Album Name:");
		dialog.showAndWait();

		String albumName = dialog.getResult();

		if(albumName == null){
			return;
		}

		if(nameChecker(albumName)){
			Album newAlbum = new Album(albumName);
			this.currUser.addNewAlbum(newAlbum);
			createAlbumTile(newAlbum);
		}else{
			addAlbum();
		}
	}

	/**
	 * Renames a selected album
	 */
	private void renameAlbum(){
		if(selectedAlbum==null){
			return;
		}
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Rename Selected Album");
		dialog.setHeaderText("Rename An Existing Album");
		dialog.setContentText("New Album Name:");
		dialog.showAndWait();

		String albumName = dialog.getResult();
		if(albumName == null){
			return;
		}

		if(nameChecker(albumName)){
			selectedAlbum.setAlbumName(albumName);
			Label newAlbumName = labelCreator(selectedAlbum.getAlbumName(),155, 5, "Arial");
			selectedPane.getChildren().remove(1);
			selectedPane.getChildren().add(1, newAlbumName);
		}else{
			renameAlbum();
		}
	}

	/**
	 * Deletes a selected Album
	 */
	private void deleteAlbum(){
		if(selectedAlbum==null){
			return;
		}
		Alert delete = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete the " + selectedAlbum.getAlbumName() + " album?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		delete.showAndWait();

		if(delete.getResult() == ButtonType.YES){
			currUser.getUserAlbums().remove(vbox.getChildren().indexOf(selectedPane));
			vbox.getChildren().remove(selectedPane);
			selectedPane = null;
		}
	}


	/**
	 * Opens up the User Options Page
	 */

	private void userOptionsPage(){

		try {
			FXMLLoader userOptionsLoader = new FXMLLoader();
			userOptionsLoader.setLocation(getClass().getResource("/fxml/UserOptions.fxml"));
			AnchorPane root = (AnchorPane) userOptionsLoader.load();
			Stage userOptionsStage = new Stage();
			userOptionsStage.setTitle("User Options");
			userOptionsStage.setResizable(false);
			userOptionsStage.initModality(Modality.WINDOW_MODAL);
			userOptionsStage.initOwner(currStage);
			Scene scene = new Scene(root);
			userOptionsStage.setScene(scene);

			UserOptionsController userOptionsController = userOptionsLoader.getController();
			userOptionsController.start(userOptionsStage , currUser, currUsersList, currStage);

			userOptionsStage.showAndWait();

			if(currUser.logout == true){
				currStage.close();
				currUser.logout = false;
			}



		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens up the selected album and opens up the photo library page
	 */


	private void openAlbum(){
		if(selectedAlbum==null){
			return;
		}

		try {
			FXMLLoader photoPageLoader = new FXMLLoader();
			photoPageLoader.setLocation(getClass().getResource("/fxml/PhotoLib.fxml"));
			AnchorPane root;

			root = (AnchorPane) photoPageLoader.load();


			Stage photoLibraryStage = new Stage();
			photoLibraryStage.setTitle("Photo Library");
			photoLibraryStage.setResizable(false);
			photoLibraryStage.initModality(Modality.WINDOW_MODAL);
			photoLibraryStage.initOwner(currStage);
			Scene scene = new Scene(root);
			photoLibraryStage.setScene(scene);

			PhotoLibController photoLibController = photoPageLoader.getController();
			photoLibController.start(photoLibraryStage, this.currUsersList, this.currUser, selectedAlbum);

			photoLibraryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			photoLibraryStage.showAndWait();
			vbox.getChildren().clear();
			importUserAlbumData();
			selectedAlbum = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens up the Search Photos Page
	 */

	private void searchPhotoPage(){
		try {
			FXMLLoader searchPageLoader = new FXMLLoader();
			searchPageLoader.setLocation(getClass().getResource("/fxml/SearchPhotosPage.fxml"));
			AnchorPane root;

			root = (AnchorPane) searchPageLoader.load();


			Stage searchPhotoStage = new Stage();
			searchPhotoStage.setTitle("Photo Search");
			searchPhotoStage.setResizable(false);
			searchPhotoStage.initModality(Modality.WINDOW_MODAL);
			searchPhotoStage.initOwner(currStage);
			Scene scene = new Scene(root);
			searchPhotoStage.setScene(scene);

			SearchPhotosController searchPhotosController = searchPageLoader.getController();
			searchPhotosController.start(searchPhotoStage, this.currUsersList, this.currUser, selectedAlbum);

			searchPhotoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			searchPhotoStage.showAndWait();
			vbox.getChildren().clear();
			importUserAlbumData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Checks if this new album name is valid or not
	 * @param albumName the name of the new album to be added
	 * @return Valid if there are no other albums named that
	 */

	private boolean nameChecker(String albumName){

		// CHECK FOR DUPLICATE NAME

		if(albumDuplicateCheck(albumName)){ // NO DUPLICATE
			if(albumName.equals("")){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Submission Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please Enter a Different Username");
				alert.showAndWait();
				return false;
			}
			return true;

		}
		else{ //DUPLICATE ALBUM NAME
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Please Enter a Different Username");
			alert.showAndWait();
			return false;

		}


	}


	/**
	 * Loops through the users album list to see if this new album name is valid
	 * @param name Name of new album
	 * @return Valid if this user does not have any albums named the same as this new album
	 */
	private boolean albumDuplicateCheck(String name){
		ArrayList<Album> currUserAlbum = this.currUser.getUserAlbums();
		for(int i =0; i < currUserAlbum.size() ; i++){
			if(currUserAlbum.get(i).getAlbumName().equalsIgnoreCase(name)){
				return false;
			}
		}
		return true;
	}


	/**
	 * imports the users data into the scene
	 */
	private void importUserAlbumData(){
		vbox.getChildren().clear();
		ArrayList <Album> currUserAlbums = currUser.getUserAlbums();
		for(int i=0; i<currUserAlbums.size();i++){
			Album album = currUserAlbums.get(i);
			createAlbumTile(album);
		}
	}

	/**
	 * Displays the first image next to the details of a photo
	 * @param album an album of photos
	 */

	private void createAlbumTile(Album album){
		AnchorPane anchTile = new AnchorPane();
		anchTile.setMinWidth(840);
		anchTile.setMaxWidth(840);
		anchTile.setMinHeight(150);
		anchTile.setMaxHeight(150);


		StackPane stackPane = new StackPane();
		stackPane.setLayoutX(1);
		stackPane.setLayoutY(1);
		stackPane.setMinWidth(148);
		stackPane.setMaxWidth(148);
		stackPane.setMinHeight(148);
		stackPane.setMaxHeight(148);


		ImageView imgView = new ImageView();
		imgView.setX(1);
		imgView.setY(1);

		if(album.getArrPhotos().size()>0){
			try {
				BufferedImage bufferedImage = ImageIO.read(album.getArrPhotos().get(0).getFilename());
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
				imgView.setImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Image noPhotos = new Image(getClass().getResourceAsStream("/resources/No_Photos.png"));
			imgView.setImage(noPhotos);
		}


		imgView.setViewport(new Rectangle2D(148, 148, 0, 0));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(148);
		imgView.setFitHeight(148);
		stackPane.getChildren().add(imgView);
		StackPane.setAlignment(imgView, Pos.CENTER);

		anchTile.getChildren().add(stackPane);
		Label albumName = labelCreator(album.getAlbumName(),155, 5, "Arial");
		Label numOfPhotos = labelCreator("Number of Photos: " + String.valueOf(album.getArrPhotos().size()),155,40,"Arial");
		Label startDate = labelCreator("Earliest: " + String.valueOf(album.getStartDate()),155, 75, "Arial");
		Label endDate = labelCreator("Latest: " + String.valueOf(album.getEndDate()),155, 110, "Arial");

		anchTile.getChildren().add(albumName);
		anchTile.getChildren().add(numOfPhotos);
		anchTile.getChildren().add(startDate);
		anchTile.getChildren().add(endDate);

		anchTile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent event) {
				if(event.getClickCount() == 2){
					selectAlbum(event,anchTile);
					openAlbum();
				}else{
					selectAlbum(event, anchTile);
				}
			}

		});

		vbox.getChildren().add(anchTile);


	}

	/**
	 * Highlights the selected album
	 * @param event when the user clicks a album
	 * @param anchTile The clicked anchor pane
	 */
	private void selectAlbum(MouseEvent event, AnchorPane anchTile) {
		AnchorPane prevPane = selectedPane;

		selectedPane = anchTile;
		if(prevPane == selectedPane){
			return;
		}
		anchTile.setEffect(new Glow(0.5));
		anchTile.setStyle("-fx-border-color: black;");
		if(prevPane != null){
			prevPane.setEffect((new Glow(0.0)));
			prevPane.setStyle("-fx-border-color: transparent;");
		}
		selectedAlbum = currUser.getUserAlbums().get(vbox.getChildren().indexOf(selectedPane));
	}


	/**
	 * Creates labels for an album
	 * @param text Name of album
	 * @param xLayout the xlayout
	 * @param yLayout the ylayout
	 * @param desiredFont the font
	 * @return A label to be display
	 */

	private Label labelCreator(String text, int xLayout, int yLayout, String desiredFont){
		Label label = new Label(text);
		label.setLayoutX(xLayout);
		label.setLayoutY(yLayout);
		label.setFont(new Font(desiredFont, 25));
		return label;
	}



}
