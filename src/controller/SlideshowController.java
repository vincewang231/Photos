package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import objects.Album;
import objects.Photo;
import objects.User;

import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Creates a slideshow of the current album
 *
 */

public class SlideshowController {

    Stage currStage;
    User currUser;
    Album currAlbum;
    int currentIndex;

    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private StackPane pictureDisplay;
    @FXML
    private TextField photoName;
    @FXML
    private TextField caption;
    @FXML
    private TextField date;
    @FXML
    private TextArea tags;


    /**
     * Setups the slideshow page
     * @param mainStage The current page
     * @param userList The list of users
     * @param user The current user
     * @param currAlbum The current Album
     * @param currentIndex The index of selected photo
     */

    public void start(Stage mainStage, ArrayList<User> userList, User user, Album currAlbum, int currentIndex) {

        mainStage.setTitle("Slideshow");
        currStage = mainStage;
        this.currUser = user;
        this.currAlbum = currAlbum;
        this.currentIndex = currentIndex;
        nextButton.requestFocus();

		pictureDisplay.setStyle("-fx-border-color: transparent;");
		photoName.setStyle("-fx-border-color: transparent;");
		caption.setStyle("-fx-border-color: transparent;");
		date.setStyle("-fx-border-color: transparent;");
		tags.setStyle("-fx-border-color: transparent;");
        //importPhotosData();

        if(currAlbum.getArrPhotos().size() >0){
            createPhotoDisplay(currAlbum.getArrPhotos().get(currentIndex).getFilename());
            setupText();
        }else {
            ImageView imgView = new ImageView();
            imgView.setX(1);
            imgView.setY(1);
            Image noPhotos = new Image(getClass().getResourceAsStream("/resources/No_Photos.png"));
            imgView.setImage(noPhotos);
            imgView.setViewport(new Rectangle2D(840, 510, 0, 0));
            imgView.setPreserveRatio(true);
            imgView.setFitWidth(397);
            imgView.setFitHeight(267);
            pictureDisplay.getChildren().add(imgView);
            StackPane.setAlignment(imgView, Pos.CENTER);


        }

    }

    /**
     * Handles all button requests
     * @param evt Button requests
     */
    @FXML
    public void buttonPress(ActionEvent evt){
        Button button = (Button) evt.getSource();
        if(button == nextButton){
            nextPhoto();
        }
        else if(button == prevButton){
            prevPhoto();
        }
    }

    /**
     * Displays the previous photo
     */
    private void prevPhoto() {
        currentIndex--;
        if(currentIndex < 0){
            currentIndex++;
            return;
        }
        pictureDisplay.getChildren().clear();
        createPhotoDisplay(currAlbum.getArrPhotos().get(currentIndex).getFilename());
        setupText();
    }

    /**
     * Displays the next photo
     */
    private void nextPhoto(){
        currentIndex++;
        if(currentIndex >= currAlbum.getArrPhotos().size()){
            currentIndex--;
            return;
        }
        pictureDisplay.getChildren().clear();
        createPhotoDisplay(currAlbum.getArrPhotos().get(currentIndex).getFilename());
        setupText();

    }


    /**
     * Sets up the textfields with photo data
     */

    private void setupText(){
    	photoName.setText(currAlbum.getArrPhotos().get(currentIndex).getNameOfPhoto());
    	caption.setText(currAlbum.getArrPhotos().get(currentIndex).getCaption());
    	date.setText(currAlbum.getArrPhotos().get(currentIndex).getDate().toString());
    	setTagField(currAlbum.getArrPhotos().get(currentIndex));
    }


    /**
     * Sets up the tag fields of a photo
     * @param photo The selected photo
     */
    private void setTagField(Photo photo){
		String total = "";
		tags.clear();
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

			tags.setText(total);
		}
	}


    /**
     * Creates a display of the selected photo
     * @param file File of selected photo
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
        imgView.setViewport(new Rectangle2D(840, 510, 0, 0));
        imgView.setPreserveRatio(true);
        imgView.setFitWidth(840);
        imgView.setFitHeight(510);
        pictureDisplay.getChildren().add(imgView);
        StackPane.setAlignment(imgView, Pos.CENTER);
    }




}