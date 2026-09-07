package com.djapp.utils;

import com.djapp.models.Track;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;

public class MetadataReader {

    /**
     * Reads metadata from an audio file and returns a Track object
     * 
     * @param audioFile The audio file (WAV, FLAC, or M4A)
     * @return Track object with metadata populated
     */
    public Track readMetadata(File audioFile) {
        Track track = new Track();
        track.setFilePath(audioFile.getAbsolutePath());

        try {
            AudioFile f = AudioFileIO.read(audioFile);
            Tag tag = f.getTag();

            // --- Text Fields ---
            track.setTitle(getTagValue(tag, FieldKey.TITLE, "Unknown Title"));
            track.setArtist(getTagValue(tag, FieldKey.ARTIST, "Unknown Artist"));
            track.setAlbum(getTagValue(tag, FieldKey.ALBUM, "Unknown Album"));
            track.setGenre(getTagValue(tag, FieldKey.GENRE, "Unknown Genre"));

            // --- Year (Integer) ---
            String yearStr = getTagValue(tag, FieldKey.YEAR, null);
            if (yearStr != null && !yearStr.isEmpty()) {
                try {
                    track.setYear(Integer.parseInt(yearStr));
                } catch (NumberFormatException e) {
                    track.setYear(null);
                }
            }

            // --- BPM (Double) ---
            String bpmStr = getTagValue(tag, FieldKey.BPM, null);
            if (bpmStr != null && !bpmStr.isEmpty()) {
                try {
                    track.setBpm(Double.parseDouble(bpmStr));
                } catch (NumberFormatException e) {
                    track.setBpm(null);
                }
            }

            // --- Key ---
            // Note: jaudiotagger doesn't have a standard KEY field.
            // This may need to be read from a custom field.
            String keyStr = getTagValue(tag, FieldKey.KEY, null);
            track.setKey(keyStr);

            // --- Duration (seconds) ---
            int duration = f.getAudioHeader().getTrackLength();
            track.setDuration(duration);

            // --- Bitrate & Sample Rate ---
            track.setBitrate((int) f.getAudioHeader().getBitRateAsNumber());

            String sampleRateStr = f.getAudioHeader().getSampleRate();
            if (sampleRateStr != null && !sampleRateStr.isEmpty()) {
                try {
                    track.setSampleRate(Integer.parseInt(sampleRateStr));
                } catch (NumberFormatException e) {
                    track.setSampleRate(null);
                }
            }

            // --- Default Values ---
            track.setPlayCount(0);

        } catch (Exception e) {
            System.err.println("⚠️ Could not read metadata from: " + audioFile.getName());
            System.err.println("   Error: " + e.getMessage());
        }

        return track;
    }

    /**
     * Helper method to safely get tag values
     */
    private String getTagValue(Tag tag, FieldKey key, String defaultValue) {
        try {
            if (tag != null) {
                String value = tag.getFirst(key);
                if (value != null && !value.isEmpty()) {
                    return value;
                }
            }
        } catch (Exception e) {
            // Field might not exist in this file format
        }
        return defaultValue;
    }
}