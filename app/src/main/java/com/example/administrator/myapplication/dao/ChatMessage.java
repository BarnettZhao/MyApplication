package com.example.administrator.myapplication.dao;

/**
 * Created by kkkkk on 2016/4/11.
 */
public class ChatMessage {
	private String tochat;
	private CharSequence words;
	private String type;

	public ChatMessage(String tochat, CharSequence words, String type) {
		this.tochat = tochat;
		this.words = words;
		this.type = type;
	}

	public ChatMessage() {
	}

	public String getTochat() {
		return tochat;
	}

	public void setTochat(String tochat) {
		this.tochat = tochat;
	}

	public CharSequence getWords() {
		return words;
	}

	public void setWords(CharSequence words) {
		this.words = words;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
