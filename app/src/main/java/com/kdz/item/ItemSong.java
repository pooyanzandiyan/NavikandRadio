package com.kdz.item;

import android.graphics.Bitmap;

public class ItemSong {
	
	private String id;
	private String CategoryId;
	private String CategoryName;
	private String artist;
	private String Mp3Url;
	private String Imagebig;
	private String Imagesmall;
	private String Mp3Name;
	private String Duration;
	private String Description;
	private Bitmap image;

	public ItemSong(String id, String CategoryId, String CategoryName, String artist, String Mp3Url,String Imagebig, String Imagesmall, String Mp3Name, String Duration, String Description) {
		this.id = id;
		this.CategoryId = CategoryId;
		this.CategoryName = CategoryName;
		this.artist = artist;
		this.Mp3Url = Mp3Url;
		this.Imagebig = Imagebig;
		this.Imagesmall = Imagesmall;
		this.Mp3Name = Mp3Name;
		this.Duration = Duration;
		this.Description = Description;
	}

	public ItemSong(String id, String artist, String Mp3Url, Bitmap image, String Mp3Name, String Duration) {
		this.id = id;
		this.artist = artist;
		this.Mp3Url = Mp3Url;
		this.image = image;
		this.Mp3Name = Mp3Name;
		this.Duration = Duration;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCategoryId() {
		return CategoryId;
	}

	public String getArtist() {
		return artist;
	}

	public void setCategoryId(String categoryid) {
		this.CategoryId = categoryid;
	}
	
	public String getCategoryName() {
		return CategoryName;
	}

	public void setCategoryName(String categoryname) {
		this.CategoryName = categoryname;
	}
	
	public String getMp3Url() {
		return Mp3Url;
	}

	public void setMp3Url(String mp3url) {
		this.Mp3Url = mp3url;
	}
	
	public String getImageBig() {
		return Imagebig;
	}

	public void setImageBig(String imagebig) {
		this.Imagebig = imagebig;
	}

	public String getImageSmall() {
		return Imagesmall;
	}

	public void setImageSmall(String imagesmall) {
		this.Imagesmall = imagesmall;
	}
	
	public String getMp3Name() {
		return Mp3Name;
	}

	public void setMp3Name(String mp3name) {
		this.Mp3Name = mp3name;
	}
	
	public String getDuration() {
		return Duration;
	}

	public void setDuration(String duration) {
		this.Duration = duration;
	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String desc) {
		this.Description = desc;
	}

	public Bitmap getBitmap() {
		return image;
	}

}
