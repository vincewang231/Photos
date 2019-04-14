package controller;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import objects.*;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Displays all the photos from a selected album
 *
 */

public class PhotoLibController {

	Stage currStage;
	User currUser;
	Album currAlbum;
	ArrayList<User> currUserList;
	int indexSelected;

	@FXML
	private Button addButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button copyButton;
	@FXML
	private Button moveButton;
	@FXML
	private Button editButton;
	@FXML
	private Button fullViewButton;
	@FXML
	private Button backButton;
	@FXML
	private TextArea tagField;
	@FXML
	private TextField captionField;
	@FXML
	private TextField dateField;
	@FXML
	private StackPane pictureDisplay;
	@FXML
	private TableView<Photo> photoInfoDisplay;
	@FXML
	private TableColumn<Photo, String> photoNameCol;
	@FXML
	private TableColumn<Photo, Date> photoDateCol;
	@FXML
	private TableColumn<Photo, String> photoCaptionCol;
	@FXML
	private TableColumn<Photo, ImageView> photoPreviewCol;
	@FXML
	private AnchorPane mainWindow;

	/**
	 * Setups the photo library stage
	 * @param mainStage The current stage
	 * @param userList The list of users
	 * @param user The current user
	 * @param currAlbum The selected album
	 */

	public void start(Stage mainStage, ArrayList<User> userList, User user, Album currAlbum) {

		mainStage.setTitle("Photos");
		currStage = mainStage;
		this.currUser = user;
		this.currAlbum = currAlbum;
		this.currUserList = userList;

		importPhotosData();
		if(currAlbum.getArrPhotos().size() >0){
			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);
			captionField.setText(currAlbum.getArrPhotos().get(indexSelected).getCaption());
			dateField.setText(currAlbum.getArrPhotos().get(indexSelected).getDate().toString());
			setTagField(currAlbum.getArrPhotos().get(indexSelected));
		}

		if(currAlbum.getArrPhotos().size() >0){
			createPhotoDisplay(currAlbum.getArrPhotos().get(0).getFilename());
		}else {
			ImageView imgView = new ImageView();
			imgView.setX(1);
			imgView.setY(1);
			Image noPhotos = new Image(getClass().getResourceAsStream("/resources/No_Photos.png"));
			imgView.setImage(noPhotos);
			imgView.setViewport(new Rectangle2D(275, 225, 0, 0));
			imgView.setPreserveRatio(true);
			imgView.setFitWidth(275);
			imgView.setFitHeight(225);
			pictureDisplay.getChildren().add(imgView);
			StackPane.setAlignment(imgView, Pos.CENTER);


		}

	}

	/**
	 * Handles button requests
	 * @param evt Button request
	 */

	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();

		if(button == addButton){
			addPhoto();
		}
		else if(button == deleteButton){
			deletePhoto();
		}
		else if(button == copyButton){
			copyPhoto();
		}
		else if(button == moveButton){
			movePhoto();
		}
		else if(button == editButton){
			editPhoto();
		}
		else if(button == backButton){
			currStage.close();
		}
		else if(button == fullViewButton){
			openFullWindow();
		}

	}


	/**
	 * Adds a photo to the library, opens up the add photo page
	 */

	private void addPhoto(){

		try {
			FXMLLoader addPhotoPageLoader = new FXMLLoader();
			addPhotoPageLoader.setLocation(getClass().getResource("/fxml/AddPhotoPage.fxml"));
			AnchorPane root;

			root = (AnchorPane) addPhotoPageLoader.load();


			Stage addPhotoStage = new Stage();
			addPhotoStage.setTitle("Photo Library");
			addPhotoStage.setResizable(false);
			addPhotoStage.initModality(Modality.WINDOW_MODAL);
			addPhotoStage.initOwner(currStage);
			Scene scene = new Scene(root);
			addPhotoStage.setScene(scene);

			AddPhotoController addPhotoController = addPhotoPageLoader.getController();
			addPhotoController.start(addPhotoStage, this.currUser, this.currAlbum);

			addPhotoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			addPhotoStage.showAndWait();
			importPhotosData();

			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);
			selectPhotoAfterUpdate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	/**
	 * Copies a selected photo to any desired album, opens the copy photo page
	 */


	private void copyPhoto(){


		try {
			FXMLLoader copyPhotoLoader = new FXMLLoader();
			copyPhotoLoader.setLocation(getClass().getResource("/fxml/CopyPhotoPage.fxml"));
			AnchorPane root;

			root = (AnchorPane) copyPhotoLoader.load();


			Stage copyPhotoStage = new Stage();
			copyPhotoStage.setTitle("Copy Photo");
			copyPhotoStage.setResizable(false);
			copyPhotoStage.initModality(Modality.WINDOW_MODAL);
			copyPhotoStage.initOwner(currStage);
			Scene scene = new Scene(root);
			copyPhotoStage.setScene(scene);

			CopyPhotoController copyPhotoController = copyPhotoLoader.getController();
			copyPhotoController.start(copyPhotoStage, this.currUser, currAlbum,currAlbum.getArrPhotos().get(indexSelected));

			copyPhotoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			copyPhotoStage.showAndWait();
			importPhotosData();

			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);

			selectPhotoAfterUpdate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Deletes a selected photo
	 */

	private void deletePhoto(){

		Alert delete = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete " + currAlbum.getArrPhotos().get(indexSelected).getNameOfPhoto(), ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
		delete.showAndWait();

		if(delete.getResult() == ButtonType.YES){
			currAlbum.getArrPhotos().remove(indexSelected);
			Collections.sort(currAlbum.getArrPhotos(), new DateSortComparator());
			if(currAlbum.getArrPhotos().size() > 0){
				currAlbum.updateStartDate();
				currAlbum.updateEndDate();
			}
			else{
				currAlbum.setStartDate(null);
				currAlbum.setEndDate(null);
				pictureDisplay.getChildren().clear();
				ImageView imgView = new ImageView();
				imgView.setX(1);
				imgView.setY(1);
				Image noPhotos = new Image(getClass().getResourceAsStream("/resources/No_Photos.png"));
				imgView.setImage(noPhotos);
				imgView.setViewport(new Rectangle2D(275, 225, 0, 0));
				imgView.setPreserveRatio(true);
				imgView.setFitWidth(275);
				imgView.setFitHeight(225);
				pictureDisplay.getChildren().add(imgView);
				StackPane.setAlignment(imgView, Pos.CENTER);

			}
			importPhotosData();

			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);

			selectPhotoAfterUpdate();

		}


	}


	/**
	 * Edit a photo, opens up the edit photos page
	 */

	private void editPhoto(){// OPENS NEW WINDOW

		try {
			FXMLLoader propertiesLoader = new FXMLLoader();
			propertiesLoader.setLocation(getClass().getResource("/fxml/Properties.fxml"));
			AnchorPane root;

			root = (AnchorPane) propertiesLoader.load();


			Stage propertyStage = new Stage();
			propertyStage.setTitle("Copy Photo");
			propertyStage.setResizable(false);
			propertyStage.initModality(Modality.WINDOW_MODAL);
			propertyStage.initOwner(currStage);
			Scene scene = new Scene(root);
			propertyStage.setScene(scene);

			PropertiesController propController = propertiesLoader.getController();
			propController.start(propertyStage, this.currUser, currAlbum,currAlbum.getArrPhotos().get(indexSelected));

			propertyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			propertyStage.showAndWait();
			importPhotosData();

			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);
			selectPhotoAfterUpdate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Moves a photo from the current album to a selected album, opens the move photo page
	 */

	private void movePhoto(){

		try {
			FXMLLoader movePhotoLoader = new FXMLLoader();
			movePhotoLoader.setLocation(getClass().getResource("/fxml/MovePhotoPage.fxml"));
			AnchorPane root;

			root = (AnchorPane) movePhotoLoader.load();


			Stage movePhotoStage = new Stage();
			movePhotoStage.setTitle("Move Photo");
			movePhotoStage.setResizable(false);
			movePhotoStage.initModality(Modality.WINDOW_MODAL);
			movePhotoStage.initOwner(currStage);
			Scene scene = new Scene(root);
			movePhotoStage.setScene(scene);

			MovePhotoController movePhotoController = movePhotoLoader.getController();
			movePhotoController.start(movePhotoStage, this.currUser, currAlbum,currAlbum.getArrPhotos().get(indexSelected));

			movePhotoStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){

						@Override
						public void run() {


						}});
				}
			});

			movePhotoStage.showAndWait();
			importPhotosData();

			photoInfoDisplay.getSelectionModel().clearSelection();
			photoInfoDisplay.requestFocus();
			photoInfoDisplay.getSelectionModel().selectFirst();
			photoInfoDisplay.getFocusModel().focus(0);
			selectPhotoAfterUpdate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Opens up the slideshow page
	 */

	private void openFullWindow(){

		try {
			FXMLLoader fullPageLoader = new FXMLLoader();
			fullPageLoader.setLocation(getClass().getResource("/fxml/Slideshow.fxml"));
			AnchorPane root = (AnchorPane) fullPageLoader.load();


			Stage fullViewStage = new Stage();
			fullViewStage.setTitle("Slideshow");
			fullViewStage.setResizable(false);
			fullViewStage.initModality(Modality.WINDOW_MODAL);
			fullViewStage.initOwner(currStage);
			Scene scene = new Scene(root);
			fullViewStage.setScene(scene);

			SlideshowController slideshowController = fullPageLoader.getController();
			slideshowController.start(fullViewStage, this.currUserList, this.currUser, this.currAlbum, this.indexSelected);

			fullViewStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Platform.runLater(new Runnable(){
						@Override
						public void run() {

						}});
				}
			});
			fullViewStage.showAndWait();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * Imports the users photos from a selected album
	 */

	private void importPhotosData() {
		photoInfoDisplay.getItems().clear();
		for(int i =0; i < currAlbum.getArrPhotos().size();i++){
			currAlbum.getArrPhotos().get(i).createImageView();
		}
		photoNameCol.setCellValueFactory(new PropertyValueFactory<Photo, String>("nameOfPhoto"));
		photoCaptionCol.setCellValueFactory(new PropertyValueFactory<Photo, String>("caption"));
		photoDateCol.setCellValueFactory(new PropertyValueFactory<Photo, Date>("dateOfPhoto"));
		photoPreviewCol.setCellValueFactory(new PropertyValueFactory<Photo, ImageView>("image"));




		photoInfoDisplay.setItems(getPhotos());



	}

	/**
	 * Adds all photos into a observable list
	 * @return Returns an observable list of photos for the tableview
	 */
	private ObservableList<Photo> getPhotos(){
		ObservableList <Photo> photos = FXCollections.observableArrayList();
		for(int i =0; i < currAlbum.getNumOfPhotos();i++){
			photos.add(currAlbum.getArrPhotos().get(i));
		}
		return photos;
	}


	/**
	 * Creates an image of a selected photo
	 * @param file The file that corresponds to the selected photo
	 */
	private void createPhotoDisplay(File file){
		ImageView imgView = new ImageView();
		imgView.setX(1);
		imgView.setY(1);

		if(currAlbum.getArrPhotos().size()>0){
			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				Image image = SwingFXUtils.toFXImage(bufferedImage, null);
				imgView.setImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Image noPhotos = new Image(getClass().getResourceAsStream("/resources/No_Photos.png"));
			imgView.setImage(noPhotos);
		}


		imgView.setViewport(new Rectangle2D(275, 225, 0, 0));
		imgView.setPreserveRatio(true);
		imgView.setFitWidth(275);
		imgView.setFitHeight(225);
		pictureDisplay.getChildren().add(imgView);
		StackPane.setAlignment(imgView, Pos.CENTER);
	}

	/**
	 * Displays all tags of a selected photo
	 * @param photo The selected photo
	 */
	private void setTagField(Photo photo){
		String total = "";
		tagField.clear();
		for(int i =0; i < photo.getArrPhotoTags().size();i++){
			if(photo.getArrPhotoTags().get(i).getTags().size() != 0){
				total += photo.getArrPhotoTags().get(i).getTagName();
				total += ": ";
			}
			for(int j =0;j<photo.getArrPhotoTags().get(i).getTags().size();j++ ){
				total+=photo.getArrPhotoTags().get(i).getTags().get(j);
				total+= ", ";
			}
			total += "\n";

			tagField.setText(total);
		}
	}


	/**
	 * Displays all data of photo when selected
	 * @param event MouseEvent
	 */
	@FXML
	private void selectPhoto(MouseEvent event) {
		indexSelected = photoInfoDisplay.getSelectionModel().getSelectedIndex();
		if(indexSelected == -1){
			return;
		}

		pictureDisplay.getChildren().clear();
		createPhotoDisplay(currAlbum.getArrPhotos().get(indexSelected).getFilename());
		captionField.setText(currAlbum.getArrPhotos().get(indexSelected).getCaption());
		dateField.setText(currAlbum.getArrPhotos().get(indexSelected).getDate().toString());
		setTagField(currAlbum.getArrPhotos().get(indexSelected));
	}


	/**
	 * Updates page when there is any update to the library
	 */

	private void selectPhotoAfterUpdate() {
		captionField.clear();
		dateField.clear();
		tagField.clear();
		indexSelected = photoInfoDisplay.getSelectionModel().getSelectedIndex();
		if(indexSelected == -1){
			return;
		}

		pictureDisplay.getChildren().clear();
		createPhotoDisplay(currAlbum.getArrPhotos().get(indexSelected).getFilename());
		captionField.setText(currAlbum.getArrPhotos().get(indexSelected).getCaption());
		dateField.setText(currAlbum.getArrPhotos().get(indexSelected).getDate().toString());
		setTagField(currAlbum.getArrPhotos().get(indexSelected));
	}


	/**
	 * Displays all photo data when selected by keyboard
	 * @param event KeyEvent
	 */
	@FXML
	private void selectPhotoWithKeyboard(KeyEvent event) {
		indexSelected = photoInfoDisplay.getSelectionModel().getSelectedIndex();
		if(indexSelected == -1){
			return;
		}

		pictureDisplay.getChildren().clear();
		createPhotoDisplay(currAlbum.getArrPhotos().get(indexSelected).getFilename());
		captionField.setText(currAlbum.getArrPhotos().get(indexSelected).getCaption());
		dateField.setText(currAlbum.getArrPhotos().get(indexSelected).getDate().toString());
		setTagField(currAlbum.getArrPhotos().get(indexSelected));
	}



}
