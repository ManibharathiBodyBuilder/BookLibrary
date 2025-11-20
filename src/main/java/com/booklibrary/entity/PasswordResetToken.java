package com.booklibrary.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PasswordResetToken {
    public Long getId() {
		return id;
	}

	public PasswordResetToken(Long id, String token, UserEntity user, LocalDateTime expiryDate) {
		super();
		this.id = id;
		this.token = token;
		this.user = user;
		this.expiryDate = expiryDate;
	}

	public PasswordResetToken() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private UserEntity user;

    private LocalDateTime expiryDate;

    // helper to set expiry e.g., now + 1 hour
}
