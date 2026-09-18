package com.djapp;

import com.djapp.data.DatabaseManager;
import com.djapp.models.Track;
import com.djapp.presentation.ConsoleUI;
import com.djapp.data.TrackDAO;
import com.djapp.utils.MetadataReader;
import com.djapp.utils.RekordboxImporter;
import com.djapp.utils.FileScanner;
import java.sql.SQLException;
import java.util.List;
import java.io.File;
import com.djapp.utils.ConfigLoader;

public class DJApp {
    public static void main(String[] args) {
        // try {
        //     DatabaseManager db = new DatabaseManager();
        //     db.connect();
        //     System.out.println("✅ App is running!");
        //     db.disconnect();
        // } catch (Exception e) {
        //     System.err.println("❌ Error: " + e.getMessage());
        //     e.printStackTrace();
        // }

    //     String musicPath = ConfigLoader.get("music.library.path");

    //     FileScanner scanner = new FileScanner();
    //     List<File> files = scanner.scan(musicPath);

    //     System.out.println("🎵 Found " + files.size() + " audio files!");

    //     // Show file types breakdown
    //     int wav = 0, flac = 0, m4a = 0;
    //     for (File f : files) {
    //         String name = f.getName().toLowerCase();
    //         if (name.endsWith(".wav"))
    //             wav++;
    //         else if (name.endsWith(".flac"))
    //             flac++;
    //         else if (name.endsWith(".m4a"))
    //             m4a++;
    //     }
    //     System.out.println("   📊 Breakdown: " + wav + " WAV, " + flac + " FLAC, " + m4a + " M4A");

    //     // Show first 5 files
    //     int count = 0;
    //     for (File file : files) {
    //         if (count >= 5)
    //             break;
    //         System.out.println("   - " + file.getName());
    //         count++;
    //     }

    //     if (files.size() > 5) {
    //         System.out.println("   ... and " + (files.size() - 5) + " more");
    //     }

    //     File firstFile = files.get(0);
    //     System.out.println("\n📖 Reading metadata from: " + firstFile.getName());

    //     MetadataReader reader = new MetadataReader();
    //     Track track = reader.readMetadata(firstFile);

    //     System.out.println("\n✅ Track metadata:");
    //     System.out.println("   Title:    " + track.getTitle());
    //     System.out.println("   Artist:   " + track.getArtist());
    //     System.out.println("   Album:    " + track.getAlbum());
    //     System.out.println("   Genre:    " + track.getGenre());
    //     System.out.println("   Year:     " + track.getYear());
    //     System.out.println("   BPM:      " + track.getBpm());
    //     System.out.println("   Key:      " + track.getKey());
    //     System.out.println("   Duration: " + track.getDuration() + " seconds");
    //     System.out.println("   Bitrate:  " + track.getBitrate() + " kbps");
    //     System.out.println("   Path:     " + track.getFilePath());

    // }

    // String xmlPath = ConfigLoader.get("rekordbox.xml.path");
    //     System.out.println("📂 Reading from: " + xmlPath);
        
    //     RekordboxImporter importer = new RekordboxImporter();
    //     List<Track> tracks = importer.importTracks(xmlPath);
        
    //     System.out.println("🎵 Found " + tracks.size() + " tracks in XML.\n");
        
    //     // Show first 3 tracks
    //     int preview = Math.min(3, tracks.size());
    //     for (int i = 0; i < preview; i++) {
    //         Track t = tracks.get(i);
    //         System.out.println("  " + t.getArtist() + " - " + t.getTitle());
    //         System.out.println("    BPM: " + t.getBpm() + " | Key: " + t.getKey());
    //     }

    //     // Insert into DB
    //     TrackDAO dao = new TrackDAO();
    //     int success = 0;
    //     for (Track track : tracks) {
    //         try {
    //             dao.insert(track);
    //             success++;
    //         } catch (Exception e) {
    //             System.err.println("⚠️ Skipped: " + track.getTitle() + " — " + e.getMessage());
    //         }
    //     }
        
    //     System.out.println("\n✅ Imported " + success + "/" + tracks.size() + " tracks.");
    
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }

}
