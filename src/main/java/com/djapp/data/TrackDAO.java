package com.djapp.data;

import com.djapp.models.Track;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrackDAO {
    private DatabaseManager dbManager;

    public TrackDAO() {
        this.dbManager = new DatabaseManager();
    }

    // Insert a track into the database
    public void insert(Track track) throws SQLException {
        String sql = "INSERT INTO tracks (file_path, title, artist, album, year, genre, bpm, `key`, duration, bitrate, sample_rate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, track.getFilePath());
            stmt.setString(2, track.getTitle());
            stmt.setString(3, track.getArtist());
            stmt.setString(4, track.getAlbum());
            stmt.setObject(5, track.getYear());
            stmt.setString(6, track.getGenre());
            stmt.setObject(7, track.getBpm());
            stmt.setString(8, track.getKey());
            stmt.setObject(9, track.getDuration());
            stmt.setObject(10, track.getBitrate());
            stmt.setObject(11, track.getSampleRate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Insert failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    track.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Get all tracks
    public List<Track> getAll() throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY artist, title";

        try (Connection conn = dbManager.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tracks.add(mapResultSetToTrack(rs));
            }
        }
        return tracks;
    }

    // Search tracks by title (partial match)
    public List<Track> searchByTitle(String query) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks WHERE title LIKE ? ORDER BY artist, title";

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tracks.add(mapResultSetToTrack(rs));
                }
            }
        }
        return tracks;
    }

    // Search by BPM range
    public List<Track> searchByBPMRange(double minBPM, double maxBPM) throws SQLException {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks WHERE bpm BETWEEN ? AND ? ORDER BY bpm";

        try (Connection conn = dbManager.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, minBPM);
            stmt.setDouble(2, maxBPM);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tracks.add(mapResultSetToTrack(rs));
                }
            }
        }
        return tracks;
    }

    // Helper method to convert ResultSet to Track object
    private Track mapResultSetToTrack(ResultSet rs) throws SQLException {
        Track track = new Track();
        track.setId(rs.getInt("id"));
        track.setFilePath(rs.getString("file_path"));
        track.setTitle(rs.getString("title"));
        track.setArtist(rs.getString("artist"));
        track.setAlbum(rs.getString("album"));

        int year = rs.getInt("year");
        if (!rs.wasNull())
            track.setYear(year);

        track.setGenre(rs.getString("genre"));

        double bpm = rs.getDouble("bpm");
        if (!rs.wasNull())
            track.setBpm(bpm);

        track.setKey(rs.getString("key"));

        int duration = rs.getInt("duration");
        if (!rs.wasNull())
            track.setDuration(duration);

        int bitrate = rs.getInt("bitrate");
        if (!rs.wasNull())
            track.setBitrate(bitrate);

        int sampleRate = rs.getInt("sample_rate");
        if (!rs.wasNull())
            track.setSampleRate(sampleRate);

        track.setPlayCount(rs.getInt("play_count"));

        Timestamp lastPlayed = rs.getTimestamp("last_played");
        if (lastPlayed != null) {
            track.setLastPlayed(lastPlayed.toLocalDateTime());
        }

        Timestamp addedDate = rs.getTimestamp("added_date");
        if (addedDate != null) {
            track.setAddedDate(addedDate.toLocalDateTime());
        }

        return track;
    }
}