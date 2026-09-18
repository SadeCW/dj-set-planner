package com.djapp.external;

import com.djapp.models.SetTrack;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exports a set to an M3U8 playlist file for import into Rekordbox.
 * M3U8 is just an M3U file saved with UTF-8 encoding.
 */
public class M3UExporter {

    /**
     * Exports a list of SetTracks to an M3U8 playlist file.
     * The order of the list determines the order in the playlist.
     */
    public void export(List<SetTrack> setTracks, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Required header
            writer.write("#EXTM3U");
            writer.newLine();

            for (SetTrack st : setTracks) {
                String artist = st.getTrack().getArtist() != null 
                    ? st.getTrack().getArtist() : "Unknown Artist";
                String title = st.getTrack().getTitle() != null 
                    ? st.getTrack().getTitle() : "Unknown Title";
                int duration = st.getTrack().getDuration() != null 
                    ? st.getTrack().getDuration() : -1;

                // Optional metadata line: #EXTINF:<seconds>,<artist> - <title>
                writer.write("#EXTINF:" + duration + "," + artist + " - " + title);
                writer.newLine();

                // The actual file path
                writer.write(st.getTrack().getFilePath());
                writer.newLine();
            }
        }
    }
}