package com.djapp.presentation;

import com.djapp.data.TrackDAO;
import com.djapp.models.Track;
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
        System.out.println("5. Exit");
        System.out.println();
    }

    private void handleChoice(int choice) {
        switch (choice) {
            case 1: viewLibrary(); break;
            case 2: searchTracks(); break;
            case 3: createSet(); break;
            case 4: viewSets(); break;
            case 5: running = false; break;
            default: System.out.println("❌ Invalid choice. Try 1-5.");
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