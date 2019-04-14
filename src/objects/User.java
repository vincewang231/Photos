package objects;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import application.Photos;
import controller.DateSortComparator;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * User object
 */
@SuppressWarnings("serial")
public class User implements Serializable {
	String username;
	ArrayList <Album> userAlbums;
	ArrayList <TagTypes> userTags;
	public boolean logout;

	/**
	 * Constructs a user object with just a username
	 * @param username The username of the user
	 */
	public User(String username) {
		this.username = username;
		this.userAlbums = new ArrayList<Album>();
		this.userTags = new ArrayList<TagTypes>();
		this.userTags.add(new TagTypes("Person"));
		this.userTags.add(new TagTypes("Location"));

		this.logout= false;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public ArrayList<Album> getUserAlbums() {
		return userAlbums;
	}


	public void setUserAlbums(ArrayList<Album> userAlbums) {
		this.userAlbums = userAlbums;
	}


	public ArrayList<TagTypes> getUserTags() {
		return userTags;
	}


	public void setUserTags(ArrayList<TagTypes> userTags) {
		this.userTags = userTags;
	}

	public void addNewAlbum(Album album){
		this.userAlbums.add(album);
	}

	/**
	 * Creates a stock photo album when a user creates an account
	 */
	public void createStartingAccount() {
		addNewAlbum(new Album("Stock"));
		this.userAlbums.get(0).addPhotoToAlbum(new Photo(new File("stockAccount/Doggy.jpg"), "Stock",this.getUserTags()));
		this.userAlbums.get(0).getArrPhotos().get(0).setCaption("First Picture");
		this.userAlbums.get(0).getArrPhotos().get(0).getArrPhotoTags().get(0).addTag("Dog");
		this.userAlbums.get(0).getArrPhotos().get(0).getArrPhotoTags().get(1).addTag("New Jersey");
		this.userAlbums.get(0).addPhotoToAlbum(new Photo(new File("stockAccount/Eagles.jpg"), "Stock", this.getUserTags()));
		this.userAlbums.get(0).getArrPhotos().get(1).setCaption("Second Picture");
		this.userAlbums.get(0).getArrPhotos().get(1).getArrPhotoTags().get(0).addTag("Eagle");
		this.userAlbums.get(0).getArrPhotos().get(1).getArrPhotoTags().get(1).addTag("Philadelphia");
		this.userAlbums.get(0).addPhotoToAlbum(new Photo(new File("stockAccount/Post Malone.jpg"), "Stock", this.getUserTags()));
		this.userAlbums.get(0).getArrPhotos().get(2).setCaption("Third Picture");
		this.userAlbums.get(0).getArrPhotos().get(2).getArrPhotoTags().get(0).addTag("Post Malone");
		this.userAlbums.get(0).getArrPhotos().get(2).getArrPhotoTags().get(1).addTag("Dallas");
		this.userAlbums.get(0).addPhotoToAlbum(new Photo(new File("stockAccount/South Park.jpg"), "Stock", this.getUserTags()));
		this.userAlbums.get(0).getArrPhotos().get(3).setCaption("Fourth Picture");
		this.userAlbums.get(0).getArrPhotos().get(3).getArrPhotoTags().get(0).addTag("Stan");
		this.userAlbums.get(0).getArrPhotos().get(3).getArrPhotoTags().get(0).addTag("Randy");
		this.userAlbums.get(0).getArrPhotos().get(3).getArrPhotoTags().get(0).addTag("Kyle");
		this.userAlbums.get(0).getArrPhotos().get(3).getArrPhotoTags().get(1).addTag("Colorado");
		this.userAlbums.get(0).addPhotoToAlbum(new Photo(new File("stockAccount/Coca-Cola.JPG"), "Stock", this.getUserTags()));
		this.userAlbums.get(0).getArrPhotos().get(4).setCaption("Fifth Picture");


		Collections.sort(this.userAlbums.get(0).getArrPhotos(), new DateSortComparator());
		this.userAlbums.get(0).setStartDate(this.userAlbums.get(0).getArrPhotos().get(0).getCal().getTime());
		this.userAlbums.get(0).setEndDate(this.userAlbums.get(0).getArrPhotos().get(this.userAlbums.get(0).getArrPhotos().size()-1).getCal().getTime());



	}

}
