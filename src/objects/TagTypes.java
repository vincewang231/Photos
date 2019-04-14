package objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * TagType object
 *
 */
@SuppressWarnings("serial")
public class TagTypes implements Serializable {
	private String tagName;
	private ArrayList <String> tags;

	/**
	 * Constructs a TagType with just the name
	 * @param nameOfTags The name of this tag
	 */
	public TagTypes(String nameOfTags) {
		this.tagName = nameOfTags;
		this.tags = new ArrayList<String>();
	}

	/**
	 * Constructs a TagType with the name and a list of tags
	 * @param nameOfTag The name of this tag
	 * @param listOfTags The list of all the tag values that correspond to this tag
	 */
	public TagTypes(String nameOfTag, ArrayList<String> listOfTags) {
		this.tagName = nameOfTag;

		tags = new ArrayList<String>();
		for (String s : listOfTags) {
			this.tags.add(s);
		}
	}

	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public ArrayList<String> getTags() {
		return tags;
	}
	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}


	public void addTag(String tag){
		this.tags.add(tag);
	}

	public ArrayList<String> getAllTags(){
		return this.tags;
	}
	@Override
	public String toString() {
		return "TagTypes [tagName=" + tagName + ", tags=" + tags + "]";
	}

}
