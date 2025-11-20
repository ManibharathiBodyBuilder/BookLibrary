package com.booklibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table
public class BookEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column
	private Long bookId;
	@Column
	private String bookName;

	 @Lob
	    private byte[] bookDocument; // PDF stored as bytes
		private String fileName;
		
		
		@Column(name = "book_year")
		private Integer bookYear;


	public Integer getBookYear() {
			return bookYear;
		}

		public void setBookYear(Integer bookYear) {
			this.bookYear = bookYear;
		}

	public BookEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public BookEntity(Long bookId, String bookName, byte[] bookDocument) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
		this.bookDocument = bookDocument;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


	public byte[] getBookDocument() {
		return bookDocument;
	}

	public void setBookDocument(byte[] bookDocument) {
		this.bookDocument = bookDocument;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}