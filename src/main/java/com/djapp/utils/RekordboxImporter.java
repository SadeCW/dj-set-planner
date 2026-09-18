package com.djapp.utils;

import com.djapp.models.Track;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a Rekordbox XML export and converts each track into a Track object.
 * Uses Java's built-in DOM parser (no extra dependencies needed).
 */
public class RekordboxImporter {

    /**
     * Main method: takes a path to a Rekordbox XML file, returns a list of Tracks.
     */
    public List<Track> importTracks(String xmlFilePath) {
        List<Track> tracks = new ArrayList<>();
        
        try {
            // --- STEP 1: Load the XML file into memory ---
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);       // Parse the file
            doc.getDocumentElement().normalize();          // Clean up the tree

            // --- STEP 2: Find every <TRACK> element in the XML ---
            // Find the COLLECTION element first
            NodeList collectionNodes = doc.getElementsByTagName("COLLECTION");
            if (collectionNodes.getLength() == 0) {
                System.err.println("❌ No <COLLECTION> found in XML.");
                return tracks;
            }
            Element collection = (Element) collectionNodes.item(0);

            // Get only TRACKs inside COLLECTION (not playlists)
            NodeList trackNodes = collection.getElementsByTagName("TRACK");
            // --- STEP 3: Loop through each track element ---
            for (int i = 0; i < trackNodes.getLength(); i++) {
                Node node = trackNodes.item(i);
                
                // Only process actual element nodes (not comments/whitespace)
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    
                    Track track = new Track();
                    
                    // --- STEP 4: Extract basic text fields ---
                    // getAttr(element, "Name", "default") → reads the attribute, or returns default
                    track.setTitle(getAttr(element, "Name", "Unknown Title"));
                    track.setArtist(getAttr(element, "Artist", "Unknown Artist"));
                    track.setAlbum(getAttr(element, "Album", "Unknown Album"));
                    track.setGenre(getAttr(element, "Genre", "Unknown Genre"));
                    
                    // --- STEP 5: Year (Rekordbox uses "0" for unknown) ---
                    String yearStr = getAttr(element, "Year", null);
                    if (yearStr != null && !yearStr.equals("0")) {
                        try { track.setYear(Integer.parseInt(yearStr)); } catch (Exception ignored) {}
                    }

                    // --- STEP 6: BPM (the whole reason we're using Rekordbox XML) ---
                    String bpmStr = getAttr(element, "AverageBpm", null);
                    if (bpmStr != null && !bpmStr.isEmpty()) {
                        try { track.setBpm(Double.parseDouble(bpmStr)); } catch (Exception ignored) {}
                    }

                    // --- STEP 7: Key (Rekordbox calls it "Tonality") ---
                    track.setKey(getAttr(element, "Tonality", null));

                    // --- STEP 8: Duration in seconds ---
                    String totalTimeStr = getAttr(element, "TotalTime", null);
                    if (totalTimeStr != null) {
                        try { track.setDuration(Integer.parseInt(totalTimeStr)); } catch (Exception ignored) {}
                    }
                    
                    // --- STEP 9: File path (stored as a URL — needs decoding) ---
                    String locationUri = getAttr(element, "Location", "");
                    track.setFilePath(decodeLocation(locationUri));

                    tracks.add(track);
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error parsing Rekordbox XML: " + e.getMessage());
            e.printStackTrace();
        }

        return tracks;
    }

    /**
     * Safely read an XML attribute. Returns defaultValue if it doesn't exist.
     */
    private String getAttr(Element element, String name, String defaultValue) {
        return element.hasAttribute(name) ? element.getAttribute(name) : defaultValue;
    }

    /**
     * Rekordbox stores paths as URLs like:
     *   "file://localhost/C:/Music/My%20Track.flac"
     * We convert that to a normal Windows path:
     *   "C:/Music/My Track.flac"
     */
    private String decodeLocation(String uri) {
        if (uri == null || uri.isEmpty()) return "";
        try {
            // Remove "file://localhost/" or "file:///" prefix
            String path = uri.replaceFirst("^file://(localhost)?/", "");
            // Decode %20 → space, etc.
            return URLDecoder.decode(path, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return uri;  // If decoding fails, just return the original
        }
    }
}