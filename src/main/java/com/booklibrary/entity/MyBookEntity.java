package com.booklibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class MyBookEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Long bookId;
	@Column
	private String bookName;
	
	
	public Long getBookId() {
		return bookId;
	}
	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public MyBookEntity(Long bookId, String bookName) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
	}
	public MyBookEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	





}