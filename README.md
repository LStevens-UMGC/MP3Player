# MP3 Player

## **Project Overview**
The MP3 Player is a lightweight, feature-rich music player application built using **Java** and **JavaFX**. It allows users to manage playlists, control playback, and adjust volume settings, all while ensuring a clean and modern user interface. The application is designed with a **Model-View-Controller (MVC)** architecture for better separation of concerns and maintainability.

This cross-platform application can run on any system with a compatible **Java Runtime Environment (JRE)** installed. Key features include **persistent settings** across sessions, **dynamic playlist management**, and **support for MP3 media formats**.

---

## **Features**
- 🎵 **Playback Controls**:
    - Play, pause, skip to the next song, or return to the previous song.
    - Adjust the playback position using a progress slider.

- 📋 **Playlist Management**:
    - Add, remove, and edit playlists.
    - Load and save playlists for future sessions.

- 🔊 **Volume Control**:
    - Adjust volume seamlessly using a slider.
    - Volume preferences persist between application sessions.

- 🌐 **Cross-Platform Compatibility**:
    - Compatible with all systems running Java 23 or higher.

- 🎛️ **Equalizer**:
    - Fine-tune audio output with an intuitive equalizer interface.

- 📁 **Drag-and-Drop Support**:
    - Easily add MP3 files to your playlists by dragging and dropping them into the application.

---

## **Setup Instructions**
To set up and run the application, follow these steps:

### **Prerequisites**
1. Ensure the following software is installed on your system:
    - **Java 23.0** or higher:
        - Run `java --version` to check.
        - [Download Java](https://www.oracle.com/java/technologies/javase-downloads.html) if not installed.
    - **Maven**:
        - Run `mvn --version` to check.
        - [Download Maven](https://maven.apache.org/download.cgi) if not installed.
    - **Git**:
        - Run `git --version` to check.
        - [Download Git](https://git-scm.com/downloads) if not installed.

2. Ensure environment variables are properly configured:
    - **JAVA_HOME**: Set this to the root directory of your Java installation.
    - **MAVEN_HOME**: Set this to the root directory of your Maven installation.

3. Update your system's `PATH` variable:
    - Add the `bin` directory of Java.
    - Add the `bin` directory of Maven.

---

### **Installation**
1. Clone the repository:
   ```bash
   git clone https://github.com/LStevens-UMGC/MP3Player.git
   ```

2. Navigate to the project directory:
   ```bash
   cd MP3Player
   ```

3. Open the project in your preferred IDE.

4. Run the project using Maven:
   ```bash
   mvn javafx:run
   ```

---

## **Project Structure**
The project follows an **MVC architecture** for clear separation of concerns:

```
src/
├── main/
│   ├── java/group2.mp3player/
│   │   ├── controller/   # Controllers for handling UI interactions
│   │   ├── model/        # Application data models
│   │   └── view/         # FXML files for UI design
│   └── resources/        # Application resources (CSS, icons, etc.)
├── test/
│   ├── java/group2.mp3player/
│   │   ├── controller/   # Unit tests for controllers
│   │   ├── model/        # Unit tests for models
pom.xml                   # Maven configuration file for dependencies
```

---

## **Usage Instructions**
1. **Add Songs**:
    - Use the `File > Add New Song` menu to select and load MP3 files into the playlist.

2. **Manage Playlists**:
    - Create, rename, or delete playlists using the playlist management options.

3. **Playback Controls**:
    - Use the play, pause, next, and previous buttons to control song playback.
    - Use the progress slider to skip to a specific position in a song.

4. **Adjust Volume**:
    - Use the volume slider to adjust playback volume. The application saves this setting for your next session.

5. **Equalizer**:
    - Open the equalizer using `View > Equalizer` and adjust bass, treble, and mid frequencies for custom sound tuning.

---

## **Requirements**
- **Java Runtime Environment (JRE)**: Java 23.0 or higher.
- **Supported File Formats**: MP3.
- **Maven**: Required for building and running the project.

---

## **Configuration**
The application can be configured using a `config.json` file (optional). Customizable settings include:
- **Default Volume Level**: Specify the initial volume when the application starts.

---

## **Contribution Guide**
We welcome contributions to the MP3 Player project! Please follow these guidelines:

1. **Create a New Branch**:
    - Develop features or bug fixes on a separate branch.
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Commit Changes**:
    - Write clear and descriptive commit messages.
   ```bash
   git commit -m "Add feature description"
   ```

3. **Push to GitHub**:
    - Push your branch to the repository.
   ```bash
   git push origin feature/your-feature-name
   ```

4. **Submit a Pull Request**:
    - Open a pull request to merge your changes into the `main` branch.
    - Ensure your code passes all tests and is reviewed by maintainers.

---

## **License**
This project is licensed under the MIT License. See the `LICENSE` file for details.
