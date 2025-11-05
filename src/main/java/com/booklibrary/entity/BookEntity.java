package com.booklibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class BookEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Long BookId;
	@Column
	private String BookName;
	@Column
	private String BookAuthor;
	@Column
	private String BookLanch;
	
	public BookEntity() {
		super();
	}
	public BookEntity(Long bookId, String bookName, String bookAuthor, String bookLanch) {
		super();
		BookId = bookId;
		BookName = bookName;
		BookAuthor = bookAuthor;
		BookLanch =  bookLanch;
	}
	public Long getBookId() {
		return BookId;
	}
	public void setBookId(Long bookId) {
		BookId = bookId;
	}
	public String getBookName() {
		return BookName;
	}
	public void setBookName(String bookName) {
		BookName = bookName;
	}

	public String getBookAuthor() {
		return BookAuthor;
	}
	public void setBookAuthor(String bookAuthor) {
		BookAuthor = bookAuthor;
	}
	public String getBookLanch() {
		return BookLanch;
	}
	public void setBookLanch(String bookLanch) {
		BookLanch = bookLanch;
	}

}
