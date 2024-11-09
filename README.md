# MP3Player
## Project Overview
An MP3 player with standard options for the player (pause/play/next/previous/volume control) along
with a playlist of songs and the ability to change playlists. Settings like volume should have persistence
across session. The application is developed using Java and JavaFX to allow use on any system with a Java
runtime environment installed.*

\*Once the Java archive file is compiled after reaching the deployment stage.

## Setup Instructions
1. Ensure the following are running on your system
    1. Java 23.0
        1. java --version
    1. Maven
        1. mvn --version
    1. git
        1. git --version
1. If they are not, download them
1. Once downloaded ensure environment variables are set
    1. Set JAVA_HOME to the java 23.0 directory
    1. Set MAVEN_HOME to the maven directory
1. Set path variables
    1. Add a path to the /bin folder for Java
    1. Add a path to the /bin folder for Maven
1. Clone the repository with the command: git clone https://github.com/LStevens-UMGC/MP3Player.git
1. Open the repository in your IDE
1. Rune the project with the maven command: javafx: run

   ## Project Structure
The project uses a MVC architecture for separation of responsibilities.

- `src/`: Contains the source code.
- `src/main/java/group2.mp3player/controller`: Contains the controllers .java files.
- `src/main/java/group2.mp3player/model`: Contains the models .java files.
- `src/resources/group2.mp3player/view`: Contains the view in .fxml files.
- `src/resources/`: Contains other resources.
- `pom.xml`: Contains the maven resources for project dependencies.

- ## Features
- **Playback Controls**: Play, pause, next, and previous song controls.
- **Playlist Management**: Add, remove, and change playlists. Load and save playlists as needed.
- **Volume Control**: Adjust the volume, which will persist across sessions.
- **Cross-Platform Compatibility**: Runs on any system with a Java runtime environment.

- ## Usage Instructions
1. **Adding Songs to the Playlist**: Use file browser to add songs to the playlist display.
1. **Adding Songs to the Playlist**: Select a playlist and song, then select "Add to playlist" button.
1. **Saving/Loading Playlists**: Access options to save the current playlist or load a saved one from the menu.
1. **Volume Persistence**: Volume settings are automatically saved when you close the application and restored when you restart.

2. ## Configuration
The application may include a configuration file for customizable settings, such as:
- **Default Volume**: Set the initial volume level.

- ## Requirements
- **Java Runtime Environment**: Ensure a Java Runtime Environment is installed on your computer
- **Supported Media Formats**: Only MP3 files are supported.

- ## Contribution Guide
All contributions are to be made on a project branch first. Once a feature is developed, a pull
request is to be sent. Provided there are no merge conflicts, the feature branch will then be
merged to main.
