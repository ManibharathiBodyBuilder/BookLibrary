package com.booklibrary.entity;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String role;
    
    @Column(unique = true, nullable = false)
    private String username;

    
  
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Column(nullable = false)
    private LocalDateTime createdAt;
	
	@Column(name = "full_name")
	private String fullName;

	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }

    
    public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Column(nullable = false)
    private LocalDateTime updatedAt;


    public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public UserEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserEntity(Long id, String username, String email, String password, String fullName, boolean enabled) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.fullName = fullName;
		this.enabled = enabled;
	}

	@Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    private String password; // encoded


    private boolean enabled = true; // or false if you want email verification
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	

	

    
}

