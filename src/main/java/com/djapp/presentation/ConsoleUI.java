package com.djapp.presentation;

import com.djapp.data.TrackDAO;
import com.djapp.models.Track;
import com.djapp.utils.MetadataReader;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Scanner scanner;
    private TrackDAO trackDAO;
    private boolean running;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.trackDAO = new TrackDAO();
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

    // --- PLACEHOLDER METHODS ---
    private void createSet() {
        System.out.println("🚧 Set builder coming soon...");
    }

    private void viewSets() {
        System.out.println("🚧 View sets coming soon...");
    }

    // --- HELPERS ---
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