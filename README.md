# Ticket to Ride: London

A digital implementation of the Ticket to Ride: London board game, built as a self-contained Java application.

## Target Platforms
* **Windows** (using Git Bash, PowerShell, or Command Prompt)
* **macOS** (using Terminal)
* **Linux** (using Terminal)

## Prerequisites
* **Java Runtime Environment (JRE) or JDK 17 or higher** must be installed and available in your system's PATH.
* No other tools (like Maven or Gradle) or third-party libraries are required.

## Build Instructions
This project has been configured to build without any external dependency managers.

### Using the Shell Script (Recommended for Mac/Linux/Git Bash)
1. Open a terminal in the project root directory.
2. Grant execution permission (if needed):
   ```bash
   chmod +x build.sh
   ```
3. Run the build script:
   ```bash
   ./build.sh
   ```

### Using the Makefile (Alternative)
If you have `make` installed on your system:
```bash
make
```

These scripts will compile the source code, bundle the game resources (images and configuration), and produce a single executable file: `TicketToRideLondon.jar`.

## Run Instructions
Once the build is complete, you can launch the game using the following command:

```bash
java -jar TicketToRideLondon.jar
```

Alternatively, on most desktop environments, you can simply **double-click** the `TicketToRideLondon.jar` file to start the game.

## Submission Compliance
* **No IDE Required:** The project can be built and run entirely from the command line.
* **No Third-Party Libraries:** All dependencies (including the JSON parser) have been internalized into the source code.
* **Self-Contained:** The JAR file contains all necessary assets and logic.
