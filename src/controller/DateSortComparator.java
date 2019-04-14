package controller;

import java.util.Comparator;

import objects.*;


/**
 *
 * @author Tyler Falgiano and Vincent Wang
 *
 * A comparator to sort by date
 *
 */

public class DateSortComparator implements Comparator<Photo> {

	@Override
	public int compare(Photo photo1, Photo photo2) {
		if(photo1.getCal().before(photo2.getCal())){
			return -1;
		}
		if(photo2.getCal().before(photo1.getCal())){
			return 1;
		}
		else{
			return 0;
		}
	}
}
