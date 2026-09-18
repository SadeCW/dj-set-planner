CREATE DATABASE dj_set_planner;
USE dj_set_planner;

CREATE TABLE Tracks (
id INT AUTO_INCREMENT, -- unique track ID
file_path VARCHAR(500) UNIQUE NOT NULL, -- Full path to adio file
title VARCHAR(255), -- Track title
artist VARCHAR(255), -- Artist Name
album VARCHAR(255), -- Album Name
year INT, -- Release year
genre VARCHAR(255), -- Genre
bpm DECIMAL(5,2), -- Beats per Minute
`key` VARCHAR(5), -- Camelot key
duration INT, -- Length in seconds
bitrate INT, -- Audio bitrate
sample_rate INT, -- Sample rate in Hz
play_count INT DEFAULT 0, -- Number of times played
last_played DATETIME, -- Last play timestamp
added_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- Import date
PRIMARY KEY (id)
);
-- Indexes for tracks
CREATE INDEX idx_tracks_title ON tracks(title);
CREATE INDEX idx_tracks_artist ON tracks(artist);
CREATE INDEX idx_tracks_bpm ON tracks(bpm);
CREATE INDEX idx_tracks_key ON tracks(`key`);
CREATE INDEX idx_tracks_genre ON tracks(genre);

CREATE TABLE Sets (
id INT AUTO_INCREMENT, -- Unique set ID
set_name VARCHAR(255) NOT NULL, -- Set name
set_description TEXT, -- Set Description
created_date DATETIME DEFAULT CURRENT_TIMESTAMP, -- Creation date
modified_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- Last Modified
total_duration INT DEFAULT 0, -- Calculated from tracks
bpm_min DECIMAL(5,2), -- Lowest BPM in set
bpm_max DECIMAL(5,2), -- Highest BPM in set
bpm_avg DECIMAL(5,2), -- Average BPM of set
tags VARCHAR(255), -- Comma separated tags
PRIMARY KEY (id)
);

CREATE TABLE Set_Tracks (
id INT AUTO_INCREMENT, -- Unique row ID
set_id INT NOT NULL, -- Set Reference
track_id INT NOT NULL, -- Track reference
position INT NOT NULL, -- Order in set
transition_notes TEXT, -- Mixing notes
PRIMARY KEY(id),
FOREIGN KEY (set_id) REFERENCES Sets(id) ON DELETE CASCADE, 
FOREIGN KEY (track_id) REFERENCES Tracks(id),
UNIQUE KEY unique_set_position (set_id, position)
);
-- Unique index for set_tracks
CREATE UNIQUE INDEX idx_set_tracks_set_position ON set_tracks(set_id, position);

Show tables;

SELECT * FROM tracks
