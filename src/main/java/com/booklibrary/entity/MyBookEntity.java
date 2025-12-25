package com.booklibrary.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table
public class MyBookEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	@Column
	private Long bookId;
	@Column
	private String bookName;
	
    // 🔥 VERY IMPORTANT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @Column(nullable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onSave() {
        this.savedAt = LocalDateTime.now();
    }

	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public UserEntity getUser() {
		return user;
	}
	public void setUser(UserEntity user) {
		this.user = user;
	}
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