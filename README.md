# Beatnik - DJ Software

Welcome to **Beatnik**, your next-generation DJ software for seamless mixing, beatmatching, and live performances.

![Grupp_24_poster](https://github.com/user-attachments/assets/2397196c-1798-4d58-9b11-c4362c21a286)

## Features
- Intuitive UI for easy mixing
- Customizable effects and transitions
- Playlist management

## Technologies Used
- **Java** for core development
- **Gradle** for dependancies and building the project
- **JavaFX** for the graphical user interface
- **TarsosDSP** for audio processing

## Installation
1. Clone this repository:
   ```sh
   git clone https://github.com/maxkoste/Beatnik.git
   ```

## Running the Application with Gradle (Terminal)
1. Navigate to the project directory:
   ```sh
   cd /path/to/your/project
   ```
2. Ensure Gradle is installed or use the Gradle Wrapper:
   ```sh
   ./gradlew --version   # On macOS/Linux
   gradlew.bat --version # On Windows
   ```
3. Build the project:
   ```sh
   ./gradlew build   # On macOS/Linux
   gradlew.bat build # On Windows
   ```
4. Run the application:
   ```sh
   ./gradlew run  # On macOS/Linux
   gradlew.bat run # On Windows
   ```
## Running the Application with Gradle (IntelliJ)
1. Open the project in IntelliJ.
2. Press the Gradle tab on the right of the program and ensure Gradle tasks have loaded (Gradle should be installed and enabled by default).
3. Press the drop-down menu by the "run" button, and select "Edit Configurations...".
4. Press the "+" symbol to add a new Gradle configuration, and enter the following into the run section:
```
   app:build run
   ```
5. Press the "ok" button and launch the application.

## Could not run Application (Windows)
The **TarsosDSP** dependency automatically ensures a required file called **FFMPEG** is downloaded on your computer, however, 
some Windows users have settings which prevent this from occurring properly. To remedy this issue, follow this guide;
1. Download and extract the latest **FFMPEG** file for your version of Windows from this website:
   ```
   https://www.gyan.dev/ffmpeg/builds/
   ```
2. Search for System Environment Variables or Path in your Windows search bar to find the "Edit the system environment variables" page in your control panel.
3. Navigate to the "advanced" tab and press "Environment Variables" at the bottom.
4. Open the Path variable and add a new Path that leads to the FFMPEG's bin folder.
5. Your program should now run properly!
