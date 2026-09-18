package com.djapp.data;

import com.djapp.models.Set;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SetDAO {
    private DatabaseManager dbManager;

    public SetDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Insert a new set
    public void insert(Set set) throws SQLException {
        String sql = "INSERT INTO sets (set_name, set_description, tags) VALUES (?, ?, ?)";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, set.getName());
            stmt.setString(2, set.getDescription());
            stmt.setString(3, set.getTags());
            
            stmt.executeUpdate();
            
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    set.setId(keys.getInt(1));
                }
            }
        }
    }

    // Get all sets (without tracks — just the metadata)
    public List<Set> getAll() throws SQLException {
        List<Set> sets = new ArrayList<>();
        String sql = "SELECT * FROM sets ORDER BY modified_date DESC";
        
        try (Connection conn = dbManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sets.add(mapSet(rs));
            }
        }
        return sets;
    }

    // Get a set by ID (with tracks loaded)
    public Set getById(int setId) throws SQLException {
        String sql = "SELECT * FROM sets WHERE id = ?";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, setId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapSet(rs);
                }
            }
        }
        return null;
    }

    // Update set metadata (duration, BPM range, modified_date)
    public void update(Set set) throws SQLException {
    String sql = "UPDATE sets SET set_name=?, set_description=?, tags=?, total_duration=?, bpm_min=?, bpm_max=?, bpm_avg=? WHERE id=?";        
        
    try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, set.getName());
            stmt.setString(2, set.getDescription());
            stmt.setString(3, set.getTags());
            stmt.setInt(4, set.getTotalDuration());
            stmt.setObject(5, set.getBpmMin());
            stmt.setObject(6, set.getBpmMax());
            stmt.setObject(7, set.getBpmAvg());
            stmt.setInt(8, set.getId());
            
            stmt.executeUpdate();
        }
    }

    // Delete a set (cascades to set_tracks)
    public boolean delete(int setId) throws SQLException {
        String sql = "DELETE FROM sets WHERE id = ?";
        
        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, setId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Set mapSet(ResultSet rs) throws SQLException {
        Set set = new Set();
        set.setId(rs.getInt("id"));
        set.setName(rs.getString("set_name"));
        set.setDescription(rs.getString("set_description"));
        set.setTags(rs.getString("tags"));
        set.setTotalDuration(rs.getInt("total_duration"));
        set.setBpmAvg(rs.getDouble("bpm_avg"));

        double bpmMin = rs.getDouble("bpm_min");
        if (!rs.wasNull()) set.setBpmMin(bpmMin);
        
        double bpmMax = rs.getDouble("bpm_max");
        if (!rs.wasNull()) set.setBpmMax(bpmMax);

        double bpmAvg = rs.getDouble("bpm_avg");
        if (!rs.wasNull()) set.setBpmAvg(bpmAvg);
        
        Timestamp created = rs.getTimestamp("created_date");
        if (created != null) set.setCreatedDate(created.toLocalDateTime());
        
        Timestamp modified = rs.getTimestamp("modified_date");
        if (modified != null) set.setModifiedDate(modified.toLocalDateTime());
        
        return set;
    }
}