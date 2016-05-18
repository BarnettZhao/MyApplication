package com.example.administrator.myapplication.dao;

/**
 * Created by kkkkk on 2016/4/29.
 */
public class FootNews {
	private String title;
	private String source;
	private String content;
	private String time;
	private String url;
	private String imageUrl;

	public FootNews(String title, String source, String content, String time, String url, String imageUrl) {
		this.title = title;
		this.source = source;
		this.content = content;
		this.time = time;
		this.url = url;
		this.imageUrl = imageUrl;
	}

	public FootNews(String title, String source, String content, String time, String url) {
		this.title = title;
		this.source = source;
		this.content = content;
		this.time = time;
		this.url = url;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
