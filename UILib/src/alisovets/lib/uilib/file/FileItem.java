package alisovets.lib.uilib.file;


import android.graphics.drawable.Drawable;

/**
 * To store some information about a file 
 *
 */
class FileItem implements Comparable<FileItem> {
	public static int DIRECTORY = 0;
	public static int FILE = 1;

	/*
	 * an icon of the file
	 */
	private Drawable icon;
	/*
	 * a file name
	 */
	private String name;
	/*
	 * a type of the file (file or directory)  
	 */
	private int type;

	public FileItem() {
	}

	public FileItem(Drawable icon, String name, int type) {
		this.icon = icon;
		this.name = name;
		this.type = type;
	}

	/**
	 * compare the current object with the object passed in the parameter the to sort by types and names
	 * @param another object to compare
	 * @return  0 if object are equal, < 0 if current object is more, >0 if current object is less  
	 */
	@Override
	public int compareTo(FileItem another) {
		if (type < another.type) {
			return -1;
		}
		if (type > another.type) {
			return 1;
		}
		
		return name.compareToIgnoreCase(another.name);
	}

	/**
	 * @return icon
	 */
	public Drawable getIcon() {
		return icon;
	}

	/**
	 * sets an icon for the file 
	 * @param icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	/**
	 * @return name of the file
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the file 
	 * @param name - the file name
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return type of the file (file or directory) 
	 */
	public int getType() {
		return type;
	}

	
	/**
	 * sets the file type
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
}
