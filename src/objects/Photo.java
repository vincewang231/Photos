package objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Photos Object
 *
 */


@SuppressWarnings("serial")
public class Photo implements Serializable {
	private File filename;
	private String path;
	private String nameOfPhoto;
	private String caption;
	private Calendar cal;
	private Date dateOfPhoto;
	private String album;
	private ArrayList<TagTypes> arrPhotoTags;
	private ImageView image;


	/**
	 * Constructs a photo with a file and album
	 * @param file The file of the photo
	 * @param album The album this photo will belong to
	 */
	public Photo(File file, String album){
		this.filename = file;
		this.cal= Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		this.cal.setTime(new Date((long)(file.lastModified())));
		this.dateOfPhoto = this.cal.getTime();
		this.path = file.getAbsolutePath();
		this.nameOfPhoto = file.getName();
		this.nameOfPhoto = this.nameOfPhoto.substring(0, nameOfPhoto.lastIndexOf("."));
		this.album = album;
		this.caption = "";
		createImageView();
		this.arrPhotoTags = new ArrayList<TagTypes>();
	}

	/**
	 * Construct a photo object with a file, the album and a list of tags
	 * @param file The file this photo belongs to
	 * @param album The album this photo belongs to
	 * @param tags The tags that correspond to this photo
	 */


	public Photo(File file, String album, ArrayList<TagTypes> tags) {
		this.filename = file;
		this.cal= Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		this.cal.setTime(new Date((long)(file.lastModified())));
		this.dateOfPhoto = this.cal.getTime();
		this.path = file.getAbsolutePath();
		this.nameOfPhoto = file.getName();
		this.nameOfPhoto = this.nameOfPhoto.substring(0, nameOfPhoto.lastIndexOf("."));
		this.album = album;
		this.caption = "";
		createImageView();

		this.arrPhotoTags = new ArrayList<TagTypes>();
		for (TagTypes t : tags) {
			this.arrPhotoTags.add(new TagTypes(t.getTagName(), t.getTags()));
		}
	}


	/**
	 * Creates an ImageView of this photo for display
	 */

	public void createImageView(){
		image = new ImageView();
		image.fitHeightProperty().set(50);
		image.fitWidthProperty().set(50);
		try {
			BufferedImage bufferedImage = ImageIO.read(this.filename);
			Image image = SwingFXUtils.toFXImage(bufferedImage, null);
			this.image.setImage(image);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ImageView getImage() {
		return image;
	}




	public void setImage(ImageView image) {
		this.image = image;
	}

	public void clearImage(){
		this.image = null;
	}


	public Date getDateOfPhoto() {
		return dateOfPhoto;
	}






	public void setDateOfPhoto(Date dateOfPhoto) {
		this.dateOfPhoto = dateOfPhoto;
	}






	public File getFilename() {
		return filename;
	}






	public void setFilename(File filename) {
		this.filename = filename;
	}






	public String getPath() {
		return path;
	}






	public void setPath(String path) {
		this.path = path;
	}






	public String getNameOfPhoto() {
		return nameOfPhoto;
	}






	public void setNameOfPhoto(String nameOfPhoto) {
		this.nameOfPhoto = nameOfPhoto;
	}






	public String getCaption() {
		return caption;
	}






	public void setCaption(String caption) {
		this.caption = caption;
	}






	public Calendar getCal() {
		return cal;
	}






	public void setCal(Calendar cal) {
		this.cal = cal;
	}






	public String getAlbum() {
		return album;
	}






	public void setAlbum(String album) {
		this.album = album;
	}






	public ArrayList<TagTypes> getArrPhotoTags() {
		return arrPhotoTags;
	}






	public void setArrPhotoTags(ArrayList<TagTypes> arrPhotoTags) {
		this.arrPhotoTags = arrPhotoTags;
	}

	public Date getDate(){
		return this.cal.getTime();
	}

}
