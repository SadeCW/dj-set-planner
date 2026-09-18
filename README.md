# DJ Set Planner

A personal Java application for building and organizing DJ sets.

## Current Progress
- ✅ MySQL database schema
- ✅ Track model + DAO
- ✅ File scanner (WAV, FLAC, M4A)
- ✅ Metadata reader
- ✅ Rekordbox XML import
- ✅ Set builder
- ⬜ Rekordbox m3u export

## Tech Stack
- Java 25
- MySQL
- Maven
- Jaudiotagger

## Setup
1. Create the database using `schema.sql`
2. Copy `config.properties.example` to `config.properties` and add your credentials
3. Run `mvn clean install`
4. Run `App.java`