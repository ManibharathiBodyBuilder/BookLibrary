/*package com.booklibrary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "fileModel")
public class FileModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fileId")
	private Long fileId;

	@Column(name = "content")
	@Lob
	private byte[] content;

	@Column(name = "name")
	private String name;

	@Column(name = "fileType")
	private String fileType;

	public FileModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileModel(byte[] content, String name, String fileType) {
		super();
		this.content = content;
		this.name = name;
		this.fileType = fileType;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
*/