package com.djapp.models;

public class SetTrack {
    private int id;
    private int setId;
    private int trackId;
    private int position;
    private String transitionNotes;
    
    // Transient — the full Track object, loaded via JOIN
    private Track track;

    public SetTrack() {}

    public SetTrack(int trackId, int position) {
        this.trackId = trackId;
        this.position = position;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSetId() { return setId; }
    public void setSetId(int setId) { this.setId = setId; }

    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public String getTransitionNotes() { return transitionNotes; }
    public void setTransitionNotes(String transitionNotes) { this.transitionNotes = transitionNotes; }

    public Track getTrack() { return track; }
    public void setTrack(Track track) { this.track = track; }
}
