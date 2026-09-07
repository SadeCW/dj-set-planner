package com.djapp.models;

import java.time.LocalDateTime;

public class Track {
    private int id;
    private String filePath;
    private String title;
    private String artist;
    private String album;
    private Integer year;
    private String genre;
    private Double bpm;
    private String key;
    private Integer duration; // seconds
    private Integer bitrate;
    private Integer sampleRate;
    private int playCount;
    private LocalDateTime lastPlayed;
    private LocalDateTime addedDate;

    // Empty constructor
    public Track() {
    }

    // Constructor with required fields
    public Track(String filePath, String title, String artist, String album,
            Integer year, String genre, Double bpm, String key,
            Integer duration, Integer bitrate, Integer sampleRate) {
        this.filePath = filePath;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.genre = genre;
        this.bpm = bpm;
        this.key = key;
        this.duration = duration;
        this.bitrate = bitrate;
        this.sampleRate = sampleRate;
        this.playCount = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Double getBpm() {
        return bpm;
    }

    public void setBpm(Double bpm) {
        this.bpm = bpm;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public Integer getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(Integer sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public LocalDateTime getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public String toString() {
        return String.format("%s - %s [%s BPM, %s]", artist, title, bpm, key);
    }
}