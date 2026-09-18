package com.djapp.data;

import com.djapp.models.SetTrack;
import com.djapp.models.Track;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SetTrackDAO {
    private DatabaseManager dbManager;

    public SetTrackDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Add a track to a set at a specific position
    public void insert(int setId, int trackId, int position) throws SQLException {
        String sql = "INSERT INTO set_tracks (set_id, track_id, position) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, setId);
            stmt.setInt(2, trackId);
            stmt.setInt(3, position);
            stmt.executeUpdate();
        }
    }

    // Get all tracks in a set, with the full Track data (via JOIN)
    public List<SetTrack> getBySetId(int setId) throws SQLException {
        List<SetTrack> setTracks = new ArrayList<>();
        String sql = "SELECT st.id, st.set_id, st.track_id, st.position, st.transition_notes, " +
                     "t.* " +
                     "FROM set_tracks st " +
                     "JOIN tracks t ON st.track_id = t.id " +
                     "WHERE st.set_id = ? " +
                     "ORDER BY st.position";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, setId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SetTrack st = new SetTrack();
                    st.setId(rs.getInt("st.id"));
                    st.setSetId(rs.getInt("st.set_id"));
                    st.setTrackId(rs.getInt("st.track_id"));
                    st.setPosition(rs.getInt("st.position"));
                    st.setTransitionNotes(rs.getString("st.transition_notes"));
                    
                    // Build the Track object from the joined columns
                    Track t = new Track();
                    t.setId(rs.getInt("id"));
                    t.setFilePath(rs.getString("file_path"));
                    t.setTitle(rs.getString("title"));
                    t.setArtist(rs.getString("artist"));
                    t.setAlbum(rs.getString("album"));
                    t.setGenre(rs.getString("genre"));
                    t.setKey(rs.getString("key"));
                    
                    double bpm = rs.getDouble("bpm");
                    if (!rs.wasNull()) t.setBpm(bpm);
                    
                    int duration = rs.getInt("duration");
                    if (!rs.wasNull()) t.setDuration(duration);
                    
                    st.setTrack(t);
                    setTracks.add(st);
                }
            }
        }
        return setTracks;
    }

    // Remove a track from a set by its set_track id
    public boolean delete(int setTrackId) throws SQLException {
        String sql = "DELETE FROM set_tracks WHERE id = ?";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, setTrackId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Update the position of a set_track entry
    public void updatePosition(int setTrackId, int newPosition) throws SQLException {
        String sql = "UPDATE set_tracks SET position = ? WHERE id = ?";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, newPosition);
            stmt.setInt(2, setTrackId);
            stmt.executeUpdate();
        }
    }
}
