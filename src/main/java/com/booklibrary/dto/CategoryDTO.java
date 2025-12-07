package com.booklibrary.dto;

public class CategoryDTO {
    private String name;
    private int count;

    public CategoryDTO(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() { return name; }
    public int getCount() { return count; }
}

