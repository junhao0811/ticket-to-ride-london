# Ticket to Ride: London - Prototype

A digital implementation of the Ticket to Ride: London board game, built as a self-contained Java application.

## Target Platforms
* **Windows** (using Command Prompt, PowerShell, or Git Bash)
* **macOS** (using Terminal)
* **Linux** (using Terminal)

## Prerequisites
* **Java Runtime Environment (JRE) or JDK 17 or higher** must be installed and available in your system's PATH.
* No other tools (like Maven or Gradle) or third-party libraries are required.

## Build Instructions
This project has been configured to build without any external dependency managers.

### Windows Users
1. Open the project root folder in File Explorer.
2. **Double-click** the `build.bat` file.
3. Once the script finishes, you will see `TicketToRideLondon.jar` in the folder.

### Mac/Linux/Git Bash Users
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

Alternatively, on most desktop environments (Windows, macOS, Linux), you can simply **double-click** the `TicketToRideLondon.jar` file to start the game.

## Troubleshooting: "Inaccessible JAR file"
If your system says the JAR is missinDg or inaccessible:
1. Ensure you have **built** the project first using `build.bat` (Windows) or `./build.sh` (Mac/Linux).
2. Check that a file named `TicketToRideLondon.jar` appeared in your folder.
3. Make sure you are running the command `java -jar TicketToRideLondon.jar` from the **project root folder**, where the JAR file is located.

## Submission Compliance
* **No IDE Required:** The project can be built and run entirely from the command line.
* **No Third-Party Libraries:** All dependencies (including the JSON parser) have been internalized into the source code.
* **Self-Contained:** The JAR file contains all necessary assets and logic.
