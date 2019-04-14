package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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
 * Search photos with dates and tags
 *
 */
public class SearchPhotosController {

	Stage currStage;
	User currUser;
	Album temp;
	Album temp2;
	int indexSelected;
	private DatePicker dateFromPicker;

	private DatePicker dateToPicker;

	@FXML
	private Button makeAlbum;
	@FXML
	private Button searchButton;
	@FXML
	private Button clearButton;
	@FXML
	private Button cancelButton;
	@FXML
	private VBox vbox;
	@FXML
	private VBox vbox2;
	@FXML
	private TextArea tagField;
	@FXML
	private TextField captionField;
	@FXML
	private TextField dateField;
	@FXML
	private ChoiceBox <String>TagChoice1;
	@FXML
	private ChoiceBox<String> TagChoice2;
	@FXML
	private ChoiceBox<String> TagVal1;
	@FXML
	private ChoiceBox<String> TagVal2;
	@FXML
	private ChoiceBox<String> AndOr;
	@FXML
	private StackPane pictureDisplay;
	@FXML
	private TableView<Photo> photoInfoDisplay;
	@FXML
	private TableColumn<Photo, String> photoNameCol;
	@FXML
	private TableColumn<Photo, String> photoCaptionCol;
	@FXML
	private TableColumn<Photo, ImageView> photoPreviewCol;
	@FXML
	private TableColumn <Photo, String> albumCol;

	/**
	 * Sets up the search photos pages
	 * @param mainStage The current Stage
	 * @param userList The list of users
	 * @param user The current user
	 * @param currAlbum The current album
	 */

	public void start(Stage mainStage, ArrayList<User> userList, User user, Album currAlbum) {

		mainStage.setTitle("Search Photos");
		currStage = mainStage;
		this.currUser = user;
		AndOr.getItems().addAll("And", "Or");
		TagVal2.setDisable(true);
		TagChoice2.setDisable(true);
		choiceBoxSetup();
		calendarSetup();
		listenerSetup();

	}

	/**
	 * Creates date picker objects for date searches
	 */

	private void calendarSetup() {
		vbox.setStyle("-fx-padding: 10;");

		dateFromPicker = new DatePicker();

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label checkInlabel = new Label("Date From:");
		gridPane.add(checkInlabel, 0, 0);

		GridPane.setHalignment(checkInlabel, HPos.LEFT);
		gridPane.add(dateFromPicker, 0, 1);
		vbox.getChildren().add(gridPane);

		vbox2.setStyle("-fx-padding: 10;");

		dateToPicker = new DatePicker();

		GridPane gridPane2 = new GridPane();
		gridPane2.setHgap(10);
		gridPane2.setVgap(10);

		Label checkInlabel2 = new Label("Date To:");
		gridPane2.add(checkInlabel2, 0, 0);

		GridPane.setHalignment(checkInlabel2, HPos.LEFT);
		gridPane2.add(dateToPicker, 0, 1);
		vbox2.getChildren().add(gridPane2);
	}

	/**
	 * Handles all button requests
	 * @param evt button requests
	 */
	@FXML
	public void buttonPress(ActionEvent evt) {
		Button button = (Button) evt.getSource();

		if(button == searchButton){
			captionField.clear();
			dateField.clear();
			tagField.clear();
			pictureDisplay.getChildren().clear();
			photoInfoDisplay.getItems().clear();
			temp =  new Album("temp");
			temp2 = new Album("temp2");
			dateFromSearch();
			dateToSearch();

			if(AndOr.getValue() == null){
				if(firstTagSearch() == false){
					return;
				}
			}else{
				if(twoTagSearch() == false){
					return;
				}
			}


			if(temp.getArrPhotos().size()>0){
				photoInfoDisplay.getItems().clear();
				for(int i =0; i < temp.getArrPhotos().size();i++){
					temp.getArrPhotos().get(i).createImageView();
				}
				photoNameCol.setCellValueFactory(new PropertyValueFactory<Photo, String>("nameOfPhoto"));
				photoCaptionCol.setCellValueFactory(new PropertyValueFactory<Photo, String>("caption"));
				photoPreviewCol.setCellValueFactory(new PropertyValueFactory<Photo, ImageView>("image"));
				albumCol.setCellValueFactory(new PropertyValueFactory<Photo, String>("album"));


				photoInfoDisplay.setItems(getPhotos());
				createPhotoDisplay(temp.getArrPhotos().get(0).getFilename());
				photoInfoDisplay.getSelectionModel().clearSelection();
				photoInfoDisplay.requestFocus();
				photoInfoDisplay.getSelectionModel().selectFirst();
				photoInfoDisplay.getFocusModel().focus(0);
				captionField.setText(temp.getArrPhotos().get(indexSelected).getCaption());
				dateField.setText(temp.getArrPhotos().get(indexSelected).getDate().toString());
				setTagField(temp.getArrPhotos().get(indexSelected));
			}

		}else if(button == clearButton){
			photoInfoDisplay.getItems().clear();
			captionField.clear();
			dateField.clear();
			tagField.clear();
			pictureDisplay.getChildren().clear();
			dateFromPicker.setValue(null);
			dateToPicker.setValue(null);
			TagChoice1.getItems().clear();
			TagChoice2.getItems().clear();
			TagVal1.getItems().clear();
			TagVal2.getItems().clear();
			AndOr.getItems().clear();
			AndOr.getItems().addAll("And", "Or");
			choiceBoxSetup();
			listenerSetup();
			TagVal2.setDisable(true);
			TagChoice2.setDisable(true);

		}else if(button == cancelButton){
			currStage.close();
		}
		else if(button == makeAlbum){
			boolean duplicate = false;

			if(temp==null){
				return;
			}
			for(int i =0; i < temp.getArrPhotos().size();i++){
				Photo temp1 = temp.getArrPhotos().get(i);
				for(int j =i+1; j < temp.getArrPhotos().size();j++){
					Photo temp2 = temp.getArrPhotos().get(j);
					if(temp1.getNameOfPhoto().equalsIgnoreCase(temp2.getNameOfPhoto())){
						duplicate = true;
					}
				}
			}
			if(duplicate == true){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Album Creation Error");
				alert.setHeaderText("Duplicate Name In Search Result");
				alert.setContentText("Please Refine Results Or Rename Duplicate Photos");
				alert.showAndWait();
			}
			else{
				addAlbum();

			}
		}
	}

	/**
	 * Gets photos that match the date from input
	 */


	private void dateFromSearch(){
		if(dateFromPicker.getValue() != null){


			for(int i =0; i < currUser.getUserAlbums().size();i++){
				Album currAlbum = currUser.getUserAlbums().get(i);
				for(int j =0; j <currAlbum.getArrPhotos().size();j++){
					Photo currPhoto = currAlbum.getArrPhotos().get(j);

					Date dateFrom= java.sql.Date.valueOf( dateFromPicker.getValue());
					Date dateOfPhoto = currPhoto.getDate();
					if(dateFrom.before(dateOfPhoto)){
						temp.addPhotoToAlbum(currPhoto);
					}



				}
			}

		}
	}

	/**
	 * Gets photos from the date to input
	 */

	private void dateToSearch(){
		if(dateToPicker.getValue() != null){

			if(temp.getArrPhotos().size() > 0){
				for(int i = 0; i < temp.getArrPhotos().size(); i++){
					Photo currPhoto = temp.getArrPhotos().get(i);
					Date dateTo= java.sql.Date.valueOf( dateToPicker.getValue());
					Date dateOfPhoto = currPhoto.getDate();
					if(!(dateTo.after(dateOfPhoto))){
						temp.getArrPhotos().remove(i);
						i--;
					}

				}

			}


			else{
				for(int i =0; i < currUser.getUserAlbums().size();i++){
					Album currAlbum = currUser.getUserAlbums().get(i);
					for(int j =0; j <currAlbum.getArrPhotos().size();j++){
						Photo currPhoto = currAlbum.getArrPhotos().get(j);

						Date dateTo= java.sql.Date.valueOf( dateToPicker.getValue());
						Date dateOfPhoto = currPhoto.getDate();
						if(dateTo.after(dateOfPhoto)){
							temp.addPhotoToAlbum(currPhoto);
						}
					}
				}
			}
		}
	}

	/**
	 * Gets all photos that match the first tag
	 * @return Valid if a list was created
	 */

	private boolean firstTagSearch(){
		boolean found = false;
		if (TagChoice1.getValue()!= null && TagVal1.getValue() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Search");
			alert.setContentText("Please Enter A Tag Value For The First Tag Search");
			alert.showAndWait();
			return false;
		}
		if(TagChoice1.getValue()!= null && TagVal1.getValue() != null){
			if(temp.getArrPhotos().size() > 0){
				for(int i = 0; i < temp.getArrPhotos().size(); i++){
					Photo currPhoto = temp.getArrPhotos().get(i);
					for(int j = 0; j < currPhoto.getArrPhotoTags().size(); j++){
						if(currPhoto.getArrPhotoTags().get(j).getTagName().equalsIgnoreCase(TagChoice1.getValue())){
							for(int k = 0; k < currPhoto.getArrPhotoTags().get(j).getTags().size();k++){
								if(currPhoto.getArrPhotoTags().get(j).getTags().get(k).equalsIgnoreCase(TagVal1.getValue())){
									found = true;
								}
							}
						}
					}
					if(found == false){
						temp.getArrPhotos().remove(i);
						i--;
					}else{
						found = false;
					}
				}
			}
			else{ //NEW TEMP
				createTempAlbum();
				for(int i = 0; i < temp.getArrPhotos().size(); i++){
					Photo currPhoto = temp.getArrPhotos().get(i);
					for(int j = 0; j < currPhoto.getArrPhotoTags().size(); j++){
						if(currPhoto.getArrPhotoTags().get(j).getTagName().equalsIgnoreCase(TagChoice1.getValue())){
							for(int k = 0; k < currPhoto.getArrPhotoTags().get(j).getTags().size();k++){
								if(currPhoto.getArrPhotoTags().get(j).getTags().get(k).equalsIgnoreCase(TagVal1.getValue())){
									found = true;
								}
							}
						}
					}
					if(found == false){
						temp.getArrPhotos().remove(i);
						i--;
					}else{
						found = false;
					}
				}


			}
		}
		return true;
	}

	/**
	 * Gets all photos that match the second tag
	 * @return Valid if a list was created
	 */
	private boolean secondTagSearch(){
		boolean found = false;
		if (TagChoice2.getValue()!= null && TagVal2.getValue() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Search");
			alert.setContentText("Please Enter A Tag Value For The Second Tag Search");
			alert.showAndWait();
			return false;
		}
		if(TagChoice2.getValue() == null){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Search");
			alert.setContentText("Please Select a Tag For The Second Tag Search");
			alert.showAndWait();
			return false;
		}
		if(TagChoice2.getValue()!= null || TagVal2.getValue() != null){
			if(temp2.getArrPhotos().size() > 0){
				for(int i = 0; i < temp2.getArrPhotos().size(); i++){
					Photo currPhoto = temp2.getArrPhotos().get(i);
					for(int j = 0; j < currPhoto.getArrPhotoTags().size(); j++){
						if(currPhoto.getArrPhotoTags().get(j).getTagName().equalsIgnoreCase(TagChoice2.getValue())){
							for(int k = 0; k < currPhoto.getArrPhotoTags().get(j).getTags().size();k++){
								if(currPhoto.getArrPhotoTags().get(j).getTags().get(k).equalsIgnoreCase(TagVal2.getValue())){
									found = true;
								}
							}
						}
					}
					if(found == false){
						temp2.getArrPhotos().remove(i);
						i--;
					}else{
						found = false;
					}
				}
			}
			else{ //NEW TEMP
				createTempAlbum();
				for(int i = 0; i < temp2.getArrPhotos().size(); i++){
					Photo currPhoto = temp2.getArrPhotos().get(i);
					for(int j = 0; j < currPhoto.getArrPhotoTags().size(); j++){
						if(currPhoto.getArrPhotoTags().get(j).getTagName().equalsIgnoreCase(TagChoice2.getValue())){
							for(int k = 0; k < currPhoto.getArrPhotoTags().get(j).getTags().size();k++){
								if(currPhoto.getArrPhotoTags().get(j).getTags().get(k).equalsIgnoreCase(TagVal2.getValue())){
									found = true;
								}
							}
						}
					}
					if(found == false){
						temp2.getArrPhotos().remove(i);
						i--;
					}else{
						found = false;
					}
				}


			}
		}
		return true;
	}

	/**
	 * Get a list from two tag searches
	 * @return Valid if a list was made
	 */

	private boolean twoTagSearch(){
		Album finalList = new Album("");
		boolean found = false;
		if(temp.getArrPhotos().size() == 0){
			createTempAlbum();
			createTemp2Album();
			if(firstTagSearch()==false){
				return false;
			}
			if(secondTagSearch()==false){
				return false;
			}

		}
		else{
			for (int i = 0 ; i < temp.getArrPhotos().size() ; i++){
				temp2.addPhotoToAlbum(temp.getArrPhotos().get(i));
			}
			if(firstTagSearch()==false){
				return false;
			}
			if(secondTagSearch()==false){
				return false;
			}
		}
		if(AndOr.getValue().equals("And")){
			for(int i =0; i < temp.getArrPhotos().size(); i++){
				Photo tempPhoto1 = temp.getArrPhotos().get(i);
				for(int j = 0; j < temp2.getArrPhotos().size();j++){
					Photo tempPhoto2 = temp2.getArrPhotos().get(j);
					if(tempPhoto1.equals(tempPhoto2)){
						finalList.addPhotoToAlbum(tempPhoto1);
					}
				}
			}
			temp.getArrPhotos().clear();
			for(Photo photo : finalList.getArrPhotos()){
				temp.addPhotoToAlbum(photo);
			}
		}
		else if(AndOr.getValue().equals("Or")){
			for(int i =0; i < temp2.getArrPhotos().size();i++){
				Photo tempPhoto2 = temp2.getArrPhotos().get(i);
				for(int j =0; j < temp.getArrPhotos().size(); j++){
					Photo tempPhoto1 = temp.getArrPhotos().get(j);
					if(tempPhoto1.equals(tempPhoto2)){
						found = true;
					}
				}
				if(found == false){
					temp.addPhotoToAlbum(tempPhoto2);
				}
				else{
					found = true;
				}
			}
		}
		return true;
	}

	/**
	 * Puts all photos that match search criteria into an observable list to be displayed on table view
	 * @return An observable list of photos
	 */

	private ObservableList<Photo> getPhotos(){
		ObservableList <Photo> photos = FXCollections.observableArrayList();
		for(int i =0; i < temp.getNumOfPhotos();i++){
			photos.add(temp.getArrPhotos().get(i));
		}
		return photos;
	}

	/**
	 * Creates an image of the selected photo
	 * @param file File of the selected photo
	 */

	private void createPhotoDisplay(File file){
		ImageView imgView = new ImageView();
		imgView.setX(1);
		imgView.setY(1);

		if(temp.getArrPhotos().size()>0){
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
	 * Sets up the tag field a of a selected photo
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
	 * Sets up the choice boxes
	 */

	private void choiceBoxSetup(){
		ArrayList<String> allTags = new ArrayList<String>();
		for(int i =0; i < currUser.getUserAlbums().size(); i++){
			for(int j =0; j < currUser.getUserAlbums().get(i).getArrPhotos().size();j++){
				for(int k =0; k<currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().size();k++){
					String currTag = currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTagName();
					if(!(allTags.contains(currTag))){
						allTags.add(currTag);
					}
				}
			}
		}
		TagChoice1.getItems().addAll(allTags);
		TagChoice2.getItems().addAll(allTags);
	}

	/**
	 * Gets selected photo with mouse click
	 * @param event MouseClick event
	 */

	@FXML
	private void selectPhoto(MouseEvent event) {
		indexSelected = photoInfoDisplay.getSelectionModel().getSelectedIndex();
		if(indexSelected == -1){
			return;
		}

		pictureDisplay.getChildren().clear();
		createPhotoDisplay(temp.getArrPhotos().get(indexSelected).getFilename());
		captionField.setText(temp.getArrPhotos().get(indexSelected).getCaption());
		dateField.setText(temp.getArrPhotos().get(indexSelected).getDate().toString());
		setTagField(temp.getArrPhotos().get(indexSelected));
	}

	/**
	 * Gets selected photo with up and down keys
	 * @param event KeyEvent event
	 */
	@FXML
	private void selectPhotoWithKeyboard(KeyEvent event) {
		indexSelected = photoInfoDisplay.getSelectionModel().getSelectedIndex();
		if(indexSelected == -1){
			return;
		}

		pictureDisplay.getChildren().clear();
		createPhotoDisplay(temp.getArrPhotos().get(indexSelected).getFilename());
		captionField.setText(temp.getArrPhotos().get(indexSelected).getCaption());
		dateField.setText(temp.getArrPhotos().get(indexSelected).getDate().toString());
		setTagField(temp.getArrPhotos().get(indexSelected));
	}

	/**
	 * Imports the tag values after the user has selected a tag
	 * @param tagFromBox1 The tag that the user selected
	 */

	private void tagValueImport1(String tagFromBox1){

		ArrayList<String> tagVals = new ArrayList<String>();
		TagVal1.getItems().clear();
		for(int i =0; i < currUser.getUserAlbums().size(); i++){
			for(int j =0; j < currUser.getUserAlbums().get(i).getArrPhotos().size();j++){
				for(int k =0; k<currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().size();k++){
					String currTag = currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTagName();
					if(currTag.equals(tagFromBox1)){
						for(int m =0; m < currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTags().size();m++ ){
							String currVal = currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTags().get(m);
							if(!(tagVals.contains(currVal))){
								tagVals.add(currVal);
							}
						}

					}
				}
			}
		}
		TagVal1.getItems().addAll(tagVals);

	}

	/**
	 *Imports the tag value after the user has selected a tag
	 * @param tagFromBox2 the tag that user selected
	 */

	private void tagValueImport2(String tagFromBox2){

		ArrayList<String> tagVals = new ArrayList<String>();
		TagVal2.getItems().clear();
		for(int i =0; i < currUser.getUserAlbums().size(); i++){
			for(int j =0; j < currUser.getUserAlbums().get(i).getArrPhotos().size();j++){
				for(int k =0; k<currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().size();k++){
					String currTag = currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTagName();
					if(currTag.equals(tagFromBox2)){
						for(int m =0; m < currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTags().size();m++ ){
							String currVal = currUser.getUserAlbums().get(i).getArrPhotos().get(j).getArrPhotoTags().get(k).getTags().get(m);
							if(!(tagVals.contains(currVal))){
								tagVals.add(currVal);
							}
						}

					}
				}
			}
		}
		TagVal2.getItems().addAll(tagVals);

	}

	/**
	 * Sets up a listener that imports tag values after a choice was made for the tag
	 */
	private void listenerSetup(){
		TagChoice1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				if(TagChoice1.getItems().size() > 0){
					tagValueImport1(TagChoice1.getItems().get((Integer) number2));
				}
			}
		});
		TagChoice2.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				if(TagChoice2.getItems().size() >0){
					tagValueImport2(TagChoice2.getItems().get((Integer) number2));
				}
			}
		});
		AndOr.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				TagChoice2.setDisable(false);
				TagVal2.setDisable(false);
			}
		});


	}

	/**
	 * Creates a temporary album
	 */
	private void createTempAlbum(){
		for(int i =0; i < currUser.getUserAlbums().size(); i++){
			for(int j =0; j < currUser.getUserAlbums().get(i).getArrPhotos().size();j++){
				Photo photo = currUser.getUserAlbums().get(i).getArrPhotos().get(j);
				temp.addPhotoToAlbum(photo);
			}
		}
	}
	/**
	 * Creates a temporary album
	 */
	private void createTemp2Album(){
		for(int i =0; i < currUser.getUserAlbums().size(); i++){
			for(int j =0; j < currUser.getUserAlbums().get(i).getArrPhotos().size();j++){
				Photo photo = currUser.getUserAlbums().get(i).getArrPhotos().get(j);
				temp2.addPhotoToAlbum(photo);
			}
		}
	}


	/**
	 * Creates album from search results
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
			Album finalAlbum = new Album(albumName);
			for(Photo t: temp.getArrPhotos()){
				Photo copyOfPhoto = new Photo(t.getFilename(), albumName,t.getArrPhotoTags());
				copyOfPhoto.setNameOfPhoto(t.getNameOfPhoto());;
				copyOfPhoto.setCaption(t.getCaption());
				finalAlbum.addPhotoToAlbum(copyOfPhoto);
			}
			finalAlbum.updateEndDate();
			finalAlbum.updateStartDate();
			this.currUser.addNewAlbum(finalAlbum);
			currStage.close();

		}else{
			addAlbum();
		}


	}


	/**
	 * Checks if there is a duplicate album name
	 * @param albumName The name of the new album
	 * @return Valid if you are able to create the album
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
	 * Loops through user album to see if there are any duplicates
	 * @param name Name of new album
	 * @return Valid if there are no duplicates
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





}
