package objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * Album object
 */

@SuppressWarnings("serial")
public class Album implements Serializable{


	private String albumName;
	private Date startDate,endDate;
	private ArrayList<Photo> arrPhotos;

	/**
	 * Creates an album object given the name of the album
	 * @param name Name of the album
	 */
	public Album(String name) {
		this.albumName = name;
		arrPhotos = new ArrayList<Photo>();
	}

	/**
	 * Gets the number of photos in this album
	 * @return The number of photos
	 */
	public int getNumOfPhotos() {
		return arrPhotos.size();
	}

	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ArrayList<Photo> getArrPhotos() {
		return arrPhotos;
	}

	public void setArrPhotos(ArrayList<Photo> arrPhotos) {
		this.arrPhotos = arrPhotos;
	}

	/**
	 * Adds a new photo this album
	 * @param photo The photo to be added
	 */
	public void addPhotoToAlbum(Photo photo){
		this.arrPhotos.add(photo);
	}

	/**
	 * Updates the start date
	 */
	public void updateStartDate(){
		this.startDate = this.arrPhotos.get(0).getCal().getTime();
	}

	/**
	 * Updates the end date
	 */
	public void updateEndDate(){
		this.endDate = this.arrPhotos.get(this.arrPhotos.size()-1).getCal().getTime();
	}




}
