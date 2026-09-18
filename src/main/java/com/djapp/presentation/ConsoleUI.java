package com.djapp.presentation;

import com.djapp.data.SetDAO;
import com.djapp.data.SetTrackDAO;
import com.djapp.data.TrackDAO;
import com.djapp.models.Track;
import com.djapp.utils.MetadataReader;
import com.djapp.data.SetDAO;
import com.djapp.data.SetTrackDAO;
import com.djapp.models.Set;
import com.djapp.models.SetTrack;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private TrackDAO trackDAO;
    private SetDAO setDAO;
    private SetTrackDAO setTrackDAO;
    private boolean running;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.trackDAO = new TrackDAO();
        this.setDAO = new SetDAO();
        this.setTrackDAO = new SetTrackDAO();
        this.running = true;
    }

    public void start() {
        System.out.println("🎧 DJ SET PLANNER 🎧");
        System.out.println("====================");

        while (running) {
            printMenu();
            int choice = readInt("Enter choice: ");
            handleChoice(choice);
        }

        System.out.println("👋 Goodbye!");
        scanner.close();
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. View Library");
        System.out.println("2. Search Tracks");
        System.out.println("3. Create New Set");
        System.out.println("4. View Sets");
        System.out.println("5. Delete Track");
        System.out.println("6. Add Track");
        System.out.println("7. Exit");
        System.out.println();
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1: viewLibrary(); break;
            case 2: searchTracks(); break;
            case 3: createSet(); break;
            case 4: viewSets(); break;
            case 5: addTrack(); break;
            case 6: deleteTrack(); break;
            case 7: running = false; break;
            default: System.out.println("❌ Invalid choice. Try 1-7.");
        }
    }

    // --- VIEW LIBRARY ---
    private void viewLibrary() {
        try {
            List<Track> tracks = trackDAO.getAll();
            System.out.println("\n📚 Library (" + tracks.size() + " tracks):");
            System.out.println("─────────────────────────────────────");
            
            for (Track t : tracks) {
                System.out.printf("%-30s | %-25s | %s BPM | %s%n",
                    truncate(t.getTitle(), 30),
                    truncate(t.getArtist(), 25),
                    t.getBpm() != null ? t.getBpm() : "?",
                    t.getKey() != null ? t.getKey() : "?");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading library: " + e.getMessage());
        }
    }

    // --- SEARCH TRACKS ---
    private void searchTracks() {
        System.out.println("\n🔍 Search by:");
        System.out.println("1. Title");
        System.out.println("2. Artist");
        System.out.println("3. BPM range");
        System.out.println("4. Key");
        int choice = readInt("Enter choice: ");

        try {
            List<Track> results = null;

            switch (choice) {
                case 1:
                    String title = readString("Enter title (or part of it): ");
                    results = trackDAO.searchByTitle(title);
                    break;
                case 2:
                    String artist = readString("Enter artist: ");
                    results = trackDAO.searchByArtist(artist);
                    break;
                case 3:
                    double min = readDouble("Min BPM: ");
                    double max = readDouble("Max BPM: ");
                    results = trackDAO.searchByBPMRange(min, max);
                    break;
                case 4:
                    List<String> availableKeys = trackDAO.getAllKeys();
                    System.out.println("Available keys: " + String.join(", ", availableKeys));
                    String key = readString("Enter key (e.g. 4A, 8B): ").toUpperCase();
                    results = trackDAO.searchByKey(key);
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
                    return;
            }

            displayResults(results);

        } catch (SQLException e) {
            System.err.println("❌ Error searching: " + e.getMessage());
        }
    }

    private void displayResults(List<Track> tracks) {
        if (tracks == null || tracks.isEmpty()) {
            System.out.println("😕 No tracks found.");
            return;
        }

        System.out.println("\n✅ Found " + tracks.size() + " track(s):");
        System.out.println("─────────────────────────────────────");
        for (Track t : tracks) {
            System.out.printf("%-30s | %-25s | %s BPM | %s%n",
                truncate(t.getTitle(), 30),
                truncate(t.getArtist(), 25),
                t.getBpm() != null ? t.getBpm() : "?",
                t.getKey() != null ? t.getKey() : "?");
        }
    }
    // ADD TRACK
        private void addTrack() {
        System.out.println("\n➕ Add Track");
        System.out.println("1. From file (auto-read metadata)");
        System.out.println("2. Manual entry");
        int choice = readInt("Enter choice: ");
        
        if (choice == 1) {
            addTrackFromFile();
        } else if (choice == 2) {
            addTrackManually();
        } else {
            System.out.println("❌ Invalid choice.");
        }
    }
    // ADD TRACK FROM LOCAL FILE
    private void addTrackFromFile() {
        String filePath = readString("Enter full file path: ");
        File file = new File(filePath);
        
        if (!file.exists()) {
            System.out.println("❌ File not found: " + filePath);
            return;
        }
        
        try {
            MetadataReader reader = new MetadataReader();
            Track track = reader.readMetadata(file);
            
            System.out.println("\n📖 Read metadata:");
            System.out.println("   Title:  " + track.getTitle());
            System.out.println("   Artist: " + track.getArtist());
            System.out.println("   Album:  " + track.getAlbum());
            System.out.println("   BPM:    " + track.getBpm());
            System.out.println("   Key:    " + track.getKey());
            
            String confirm = readString("\nAdd this track? (yes/no): ");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("❌ Cancelled.");
                return;
            }
            
            trackDAO.insert(track);
            System.out.println("✅ Track added with ID: " + track.getId());
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
    // MANUAL ADD
    private void addTrackManually() {
        Track track = new Track();
        
        track.setFilePath(readString("File path: "));
        track.setTitle(readString("Title: "));
        track.setArtist(readString("Artist: "));
        track.setAlbum(readString("Album: "));
        
        String yearStr = readString("Year (or blank): ");
        if (!yearStr.isEmpty()) {
            try { track.setYear(Integer.parseInt(yearStr)); } catch (Exception ignored) {}
        }
        
        track.setGenre(readString("Genre: "));
        
        String bpmStr = readString("BPM (or blank): ");
        if (!bpmStr.isEmpty()) {
            try { track.setBpm(Double.parseDouble(bpmStr)); } catch (Exception ignored) {}
        }
        
        track.setKey(readString("Key (e.g. 4A): "));
        
        String durStr = readString("Duration in seconds (or blank): ");
        if (!durStr.isEmpty()) {
            try { track.setDuration(Integer.parseInt(durStr)); } catch (Exception ignored) {}
        }
        
        try {
            trackDAO.insert(track);
            System.out.println("✅ Track added with ID: " + track.getId());
        } catch (SQLException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }


    private void deleteTrack() {
    System.out.println("\n🗑️  Delete Track");
    
    // Step 1: Search for the track first
    String query = readString("Search by title or artist: ");
    
    try {
        List<Track> results = trackDAO.searchByTitle(query);
        if (results.isEmpty()) {
            results = trackDAO.searchByArtist(query);
        }
        
        if (results.isEmpty()) {
            System.out.println("😕 No tracks found.");
            return;
        }
        
        // Step 2: Show results with IDs
        System.out.println("\nFound " + results.size() + " track(s):");
        System.out.println("─────────────────────────────────────");
        for (Track t : results) {
            System.out.printf("[%d] %s | %s | %s BPM | %s%n",
                t.getId(),
                truncate(t.getTitle(), 30),
                truncate(t.getArtist(), 25),
                t.getBpm() != null ? t.getBpm() : "?",
                t.getKey() != null ? t.getKey() : "?");
        }
        
        // Step 3: Ask which one to delete
        int trackId = readInt("\nEnter track ID to delete (0 to cancel): ");
        if (trackId == 0) {
            System.out.println("❌ Cancelled.");
            return;
        }
        
        // Step 4: Confirm
        String confirm = readString("⚠️  Are you sure? Type 'yes' to confirm: ");
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("❌ Cancelled.");
            return;
        }
        
        // Step 5: Delete
        boolean deleted = trackDAO.delete(trackId);
        if (deleted) {
            System.out.println("✅ Track deleted.");
        } else {
            System.out.println("❌ Track ID not found.");
        }
        
    } catch (SQLException e) {
        System.err.println("❌ Error: " + e.getMessage());
    }
}

    // --- CREATE NEW SET ---
    private void createSet() { 
        System.out.println("\n➕ Create New Set");
        
        String name = readString("Set name: ");
        String description = readString("Description (or blank): ");
        String tags = readString("Tags (comma-separated, or blank): ");
        
        Set set = new Set(name);
        set.setDescription(description.isEmpty() ? null : description);
        set.setTags(tags.isEmpty() ? null : tags);
        
        try {
            setDAO.insert(set);
            System.out.println("✅ Created set: " + set.getName() + " (ID: " + set.getId() + ")");
            
            // Immediately let them add tracks
            String addNow = readString("Add tracks now? (yes/no): ");
            if (addNow.equalsIgnoreCase("yes")) {
                manageSet(set.getId());
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating set: " + e.getMessage());
        }
    }

    // --- VIEW SETS ---
    private void viewSets() {
        try {
            List<Set> sets = setDAO.getAll();
            
            if (sets.isEmpty()) {
                System.out.println("\n😕 No sets yet. Create one first!");
                return;
            }
            
            System.out.println("\n📋 Your Sets:");
            System.out.println("─────────────────────────────────────");
            for (Set s : sets) {
                System.out.printf("[%d] %s%n", s.getId(), s.getName());
                if (s.getDescription() != null) {
                    System.out.println("    " + s.getDescription());
                }
            }
            
            int setId = readInt("\nEnter set ID to manage (0 to cancel): ");
            if (setId == 0) return;
            
            Set set = setDAO.getById(setId);
            if (set == null) {
                System.out.println("❌ Set not found.");
                return;
            }
            
            manageSet(setId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error loading sets: " + e.getMessage());
        }
    }

    // --- MANAGE A SET (the big one) ---
    private void manageSet(int setId) {
        boolean managing = true;
        
        while (managing) {
            try {
                Set set = setDAO.getById(setId);
                if (set == null) {
                    System.out.println("❌ Set not found.");
                    return;
                }
                
                List<SetTrack> setTracks = setTrackDAO.getBySetId(setId);
                
                // Display current set
                System.out.println("\n🎧 Set: " + set.getName());
                System.out.println("─────────────────────────────────────");
                
                if (setTracks.isEmpty()) {
                    System.out.println("(empty)");
                } else {
                    int totalSeconds = 0;
                    double minBpm = Double.MAX_VALUE;
                    double maxBpm = 0;
                    double bpmSum = 0;
                    int bpmCount = 0;
                    
                    for (SetTrack st : setTracks) {
                        Track t = st.getTrack();
                        System.out.printf("[%d] %d. %s | %s | %s BPM | %s%n",
                            st.getId(),
                            st.getPosition(),
                            truncate(t.getTitle(), 25),
                            truncate(t.getArtist(), 20),
                            t.getBpm() != null ? t.getBpm() : "?",
                            t.getKey() != null ? t.getKey() : "?");
                        
                        if (t.getDuration() != null) totalSeconds += t.getDuration();
                        if (t.getBpm() != null) {
                            if (t.getBpm() < minBpm) minBpm = t.getBpm();
                            if (t.getBpm() > maxBpm) maxBpm = t.getBpm();
                        }
                        if (t.getBpm() != null) {
                            if (t.getBpm() < minBpm) minBpm = t.getBpm();
                            if (t.getBpm() > maxBpm) maxBpm = t.getBpm();
                            bpmSum += t.getBpm();
                            bpmCount++;
                        }
                    }
                    double avgBpm = bpmCount > 0 ? bpmSum / bpmCount : 0;
                    
                    // Save updated stats to DB
                    set.setTotalDuration(totalSeconds);
                    set.setBpmMin(minBpm == Double.MAX_VALUE ? null : minBpm);
                    set.setBpmMax(maxBpm == 0 ? null : maxBpm);
                    set.setBpmAvg(bpmCount > 0 ? avgBpm : null);
                    setDAO.update(set);
                    
                    System.out.println("─────────────────────────────────────");
                    System.out.printf("Total: %d tracks | %s | BPM: %.1f-%.1f (avg %.1f)%n",
                    setTracks.size(), formatDuration(totalSeconds), minBpm, maxBpm, avgBpm);
                }
                
                // Menu
                System.out.println("\n1. Add Track");
                System.out.println("2. Remove Track");
                System.out.println("3. Move Track Up");
                System.out.println("4. Move Track Down");
                System.out.println("5. Delete This Set");
                System.out.println("6. Back to Main Menu");
                
                int choice = readInt("Choice: ");
                
                switch (choice) {
                    case 1: addTrackToSet(setId); break;
                    case 2: removeTrackFromSet(); break;
                    case 3: moveTrack(setId, -1); break;
                    case 4: moveTrack(setId, 1); break;
                    case 5:
                        if (deleteSet(setId)) {
                            managing = false; // exit back to main menu
                        }
                        break;
                    case 6: managing = false; break;
                    default: System.out.println("❌ Invalid choice.");
                }
                
            } catch (SQLException e) {
                System.err.println("❌ Error: " + e.getMessage());
                return;
            }
        }
    }

    // --- HELPERS ---

            // --- ADD TRACK TO SET ---
    private void addTrackToSet(int setId) throws SQLException {
        System.out.println("\n🔍 Search library:");
        String query = readString("Search (title/artist): ");
        
        List<Track> results = trackDAO.searchByTitle(query);
        if (results.isEmpty()) {
            results = trackDAO.searchByArtist(query);
        }
        
        if (results.isEmpty()) {
            System.out.println("😕 No tracks found.");
            return;
        }
        
        System.out.println("\nFound " + results.size() + " track(s):");
        System.out.println("─────────────────────────────────────");
        for (int i = 0; i < results.size(); i++) {
            Track t = results.get(i);
            System.out.printf("[%d] %d. %s | %s | %s BPM | %s%n",
                i + 1,
                t.getId(),
                truncate(t.getTitle(), 25),
                truncate(t.getArtist(), 20),
                t.getBpm() != null ? t.getBpm() : "?",
                t.getKey() != null ? t.getKey() : "?");
        }
        
        int pick = readInt("\nPick a track (0 to cancel): ");
        if (pick < 1 || pick > results.size()) {
            System.out.println("❌ Cancelled.");
            return;
        }
        
        Track chosen = results.get(pick - 1);
        
        // Get next position
        List<SetTrack> existing = setTrackDAO.getBySetId(setId);
        int nextPosition = existing.size() + 1;
        
        setTrackDAO.insert(setId, chosen.getId(), nextPosition);
        System.out.println("✅ Added: " + chosen.getTitle() + " at position " + nextPosition);
    }

    // --- REMOVE TRACK FROM SET ---
    private void removeTrackFromSet() throws SQLException {
        int setTrackId = readInt("Enter [ID] of track to remove (0 to cancel): ");
        if (setTrackId == 0) return;
        
        boolean deleted = setTrackDAO.delete(setTrackId);
        if (deleted) {
            System.out.println("✅ Removed.");
        } else {
            System.out.println("❌ Not found.");
        }
    }

    // --- MOVE TRACK UP/DOWN ---
    private void moveTrack(int setId, int direction) throws SQLException {
        List<SetTrack> setTracks = setTrackDAO.getBySetId(setId);
        if (setTracks.size() < 2) {
            System.out.println("😕 Need at least 2 tracks to reorder.");
            return;
        }
        
        int setTrackId = readInt("Enter [ID] of track to move: ");
        
        SetTrack current = null;
        int currentIndex = -1;
        for (int i = 0; i < setTracks.size(); i++) {
            if (setTracks.get(i).getId() == setTrackId) {
                current = setTracks.get(i);
                currentIndex = i;
                break;
            }
        }
        
        if (current == null) {
            System.out.println("❌ Track not in set.");
            return;
        }
        
        int newIndex = currentIndex + direction;
        if (newIndex < 0 || newIndex >= setTracks.size()) {
            System.out.println("❌ Can't move further in that direction.");
            return;
        }
        
        SetTrack neighbor = setTracks.get(newIndex);
        
        int currentPos = current.getPosition();
        int neighborPos = neighbor.getPosition();
        
        // 3-step swap to avoid UNIQUE constraint violation
        setTrackDAO.updatePosition(current.getId(), -1);        // Step 1: temp position
        setTrackDAO.updatePosition(neighbor.getId(), currentPos); // Step 2: neighbor takes current's spot
        setTrackDAO.updatePosition(current.getId(), neighborPos); // Step 3: current takes neighbor's spot
        
        System.out.println("✅ Moved.");
    }

        private boolean deleteSet(int setId) throws SQLException {
        Set set = setDAO.getById(setId);
        if (set == null) {
            System.out.println("❌ Set not found.");
            return false;
        }
        
        String confirm = readString("⚠️  Delete set '" + set.getName() + "' and all its tracks? Type 'yes' to confirm: ");
        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("❌ Cancelled.");
            return false;
        }
        
        boolean deleted = setDAO.delete(setId);
        if (deleted) {
            System.out.println("✅ Set deleted.");
            return true;
        } else {
            System.out.println("❌ Could not delete set.");
            return false;
        }
    }

    // --- FORMAT DURATION ---
    private String formatDuration(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else {
            return String.format("%dm %ds", minutes, seconds);
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("❌ Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("❌ Please enter a number: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine();
        return value;
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String truncate(String str, int max) {
        if (str == null) return "";
        return str.length() > max ? str.substring(0, max - 3) + "..." : str;
    }

}