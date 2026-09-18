package com.djapp.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Set {
    private int id;
    private String name;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private int totalDuration;
    private Double bpmMin;
    private Double bpmMax;
    private Double bpmAvg;
    private String tags;
    
    // Transient — loaded separately from set_tracks table
    private List<SetTrack> tracks;

    public Set() {
        this.tracks = new ArrayList<>();
    }

    public Set(String name) {
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    public int getTotalDuration() { return totalDuration; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }

    public Double getBpmMin() { return bpmMin; }
    public void setBpmMin(Double bpmMin) { this.bpmMin = bpmMin; }

    public Double getBpmMax() { return bpmMax; }
    public void setBpmMax(Double bpmMax) { this.bpmMax = bpmMax; }

    public Double getBpmAvg() { return bpmAvg; }
    public void setBpmAvg(Double bpmAvg) { this.bpmAvg = bpmAvg; }  
    
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public List<SetTrack> getTracks() { return tracks; }
    public void setTracks(List<SetTrack> tracks) { this.tracks = tracks; }

    @Override
    public String toString() {
        return name + " (" + tracks.size() + " tracks)";
    }
}