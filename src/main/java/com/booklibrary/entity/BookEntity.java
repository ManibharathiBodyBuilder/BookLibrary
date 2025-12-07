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
	private Long bookId;
	@Column
	private String bookName;
	@Column(name = "category")
	private String category;

	
	
	 public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	@Column(name = "cover_url")
	    private String coverUrl;

	    // constructors, getters, setters...
	    public String getCoverUrl() { return coverUrl; }
	    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
	
	@Column(name = "pdf_url")
	private String pdfUrl;
	
	public String getPdfUrl() {
	    return pdfUrl;
	}

	public void setPdfUrl(String pdfUrl) {
	    this.pdfUrl = pdfUrl;
	}



	/* @Lob
	    private byte[] bookDocument;*/ // PDF stored as bytes
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

	public BookEntity(Long bookId, String bookName, String fileName) {
	    this.bookId = bookId;
	    this.bookName = bookName;
	    this.fileName = fileName;
	}


	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}


/*	public byte[] getBookDocument() {
		return bookDocument;
	}

	public void setBookDocument(byte[] bookDocument) {
		this.bookDocument = bookDocument;
	}*/

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}