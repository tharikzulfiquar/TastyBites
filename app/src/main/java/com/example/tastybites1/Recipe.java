package com.example.tastybites1;  // Tweaked: Main package

public class Recipe {
    private int id;  // Tweaked: Not final (for flexibility)
    private String title;
    private int readyInMinutes;  // Tweaked: Replaced description with readyInMinutes
    private String image;  // Tweaked: Renamed imageUrl to image

    public Recipe(int id, String title, int readyInMinutes, String image) {
        this.id = id;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
        this.image = image;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getReadyInMinutes() { return readyInMinutes; }  // Tweaked
    public String getImage() { return image; }  // Tweaked
}