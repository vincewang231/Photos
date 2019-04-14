package controller;

import java.util.ArrayList;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import objects.Album;
import objects.Photo;
import objects.TagTypes;
import objects.User;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Displays the tags and allows for adding and deleting tags
 */
public class TagPageController {

	Stage currStage;
	User currUser;
	Photo currPhoto;
	ArrayList<User> currUserList;
	int indexSelected;
	ArrayList<TagTypes> userTags;
	private ObservableList<String> obsList;


	@FXML
	private Button addButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button submitButton;
	@FXML
	private ListView<String> tagDisplay;


	/**
	 * Sets up the tags page
	 * @param mainStage The current stage
	 * @param user The current user
	 * @param photo The current photo
	 */
	public void start(Stage mainStage, User user, Photo photo) {

		mainStage.setTitle("Photos");
		this.currStage = mainStage;
		this.currUser = user;
		this.currPhoto = photo;
		addButton.requestFocus();
		userTags = new ArrayList<TagTypes>();
		obsList = FXCollections.observableArrayList();

		if(photo != null){
			for(int i=0; i < photo.getArrPhotoTags().size();i++){
				userTags.add(photo.getArrPhotoTags().get(i));
			}
			createListView();
		}
		else{

		}
	}

	/**
	 * Handles all button requests
	 * @param evt Button request
	 */

	@FXML
	public void buttonPress(ActionEvent evt){
		Button button = (Button) evt.getSource();

		if(button == addButton){
			addTag();
		}
		else if(button == deleteButton){
			deleteTag();

		}
		else if(button == submitButton){
			currStage.close();

		}
	}

	/**
	 * Adds a tag
	 */


	private void addTag(){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add To Tag");
		dialog.setHeaderText("Create New Tag or Add To Existing");
		dialog.setContentText("What Tag Would You Like To Add?:");
		dialog.showAndWait();

		String tagName = dialog.getResult();
		if(tagName == null){
			return;
		}
		if(tagName.equals("")){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Submission Error");
			alert.setHeaderText("Invalid Input");
			alert.setContentText("Nothing Added");
			alert.showAndWait();
			addTag();
		}
		TagTypes temp = newOrExistingTag(tagName.toLowerCase());
		String tagValue = "";


		if(temp == null){ //BRAND NEW TAG
			while(true){
				dialog = new TextInputDialog();
				dialog.setTitle("Add Tag Value");
				dialog.setHeaderText("What Value Would You Like To Add To " + tagName);
				dialog.setContentText("Value To Be Added:");
				dialog.showAndWait();
				tagValue = dialog.getResult();

				if(tagValue == null){
					break;
				}else{
					if(tagValue.equals("")){
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Submission Error");
						alert.setHeaderText("Invalid Input");
						alert.setContentText("Nothing Added");
						alert.showAndWait();
						continue;
					}
					break;
				}
			}

			if(tagValue == null){//EXITED OUT OF TAG VALUE
				return;
			}else{
				String finalTagName = tagName.substring(0, 1).toUpperCase() + tagName.substring(1);
				TagTypes newTagType = new TagTypes(finalTagName);
				String finalTagValue= tagValue.substring(0,1).toUpperCase() + tagValue.substring(1);
				newTagType.addTag(finalTagValue);
				userTags.add(newTagType);
				createListView();
			}
		}
		else{ //OLDTAG
			while(true){
				dialog = new TextInputDialog();
				dialog.setTitle("Add Tag Value");
				dialog.setHeaderText("What Value Would You Like To Add To " + tagName);
				dialog.setContentText("Value To Be Added:");
				dialog.showAndWait();
				tagValue = dialog.getResult();

				if(tagValue == null){
					break;
				}else{
					if(tagValue.equals("")){
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Submission Error");
						alert.setHeaderText("Invalid Input");
						alert.setContentText("Nothing Added");
						alert.showAndWait();
						continue;
					}
					break;
				}
			}
			if(tagValue == null){//EXITED OUT OF TAG VALUE
				return;
			}else{
				String finalTagValue= tagValue.substring(0,1).toUpperCase() + tagValue.substring(1);
				if(findTagVal(temp,finalTagValue)==-1){
					temp.addTag(finalTagValue);
				}
				else{
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Submission Error");
					alert.setHeaderText("Duplicate");
					alert.setContentText("Duplicate Tag Value");
					alert.showAndWait();
				}
				createListView();
			}



		}
	}

	/**
	 * Deletes a selected tag
	 */

	private void deleteTag(){

		int index = tagDisplay.getSelectionModel().getSelectedIndex();
		if(index == -1){
			return;
		}
		String tagType = obsList.get(index);
		String toDelete = obsList.get(index);
		toDelete = toDelete.substring(toDelete.lastIndexOf("-")+2);
		tagType = tagType.substring(0,tagType.lastIndexOf("-")-1);
		TagTypes tagOfVal = newOrExistingTag(tagType);
		int indexOfValue = findTagVal(tagOfVal, toDelete);
		tagOfVal.getTags().remove(indexOfValue);
		createListView();
	}

	/**
	 * @return Returns all tags of current photo
	 */
	public ArrayList<TagTypes> getTags(){
		return userTags;
	}

	/**
	 * Finds a tag value to see if it exists
	 * @param tagType The tag the user wants to delete
	 * @param toDelete The value the user wants to delete
	 * @return The index where this value exists
	 */

	private int findTagVal(TagTypes tagType, String toDelete){
		for(int i = 0; i < tagType.getTags().size();i++){
			if(tagType.getTags().get(i).equalsIgnoreCase(toDelete)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds if a tag has been made before or need to create a new one
	 * @param tagName The name of the tag
	 * @return A TagType of the given input
	 */

	private TagTypes newOrExistingTag(String tagName){
		for(int i =0; i < userTags.size();i++){
			if(tagName.equalsIgnoreCase(userTags.get(i).getTagName())){
				return userTags.get(i);
			}
		}
		return null;
	}


	/**
	 * creates a list view of tags
	 */
	private void createListView(){
		tagDisplay.getItems().clear();
		for(int i =0; i < userTags.size(); i++){
			for(int j =0; j < userTags.get(i).getTags().size();j++){
				obsList.add(userTags.get(i).getTagName() + " - " + userTags.get(i).getTags().get(j));
			}
		}
		tagDisplay.setItems(obsList);

	}


}
