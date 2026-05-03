# Ticket to Ride: London - Prototype

A digital implementation of the Ticket to Ride: London board game, provided as a self-contained Java executable.

## Target Platforms
* **Windows** 
* **macOS**
* **Linux** 

## Prerequisites
* **Java Runtime Environment (JRE) 17 or higher** must be installed and available in your system's PATH.
* No IDE, JDK, Maven, Gradle, or third-party libraries are required to run the prototype.

## Run Instructions (For Evaluators)
This project is provided as a pre-compiled executable JAR file (`TicketToRideLondon.jar`), which contains all necessary logic, dependencies, and resources.

1. Open a terminal (Command Prompt, PowerShell, or macOS/Linux Terminal) in the folder containing `TicketToRideLondon.jar`.
2. Launch the game using the following command:
   ```bash
   java -jar TicketToRideLondon.jar
   ```

Alternatively, on most desktop environments, you can simply **double-click** the `TicketToRideLondon.jar` file to start the game.

## Build Instructions (Optional - Requires JDK)
If you wish to rebuild the executable from source, a **JDK (Java Development Kit) 17+** is required. 

### Windows Users
1. Open the project root folder.
2. **Double-click** the `build.bat` file.
3. This will compile the source code and generate a fresh `TicketToRideLondon.jar`.

### Mac/Linux Users
1. Open a terminal in the project root directory.
2. Run the build script:
   ```bash
   chmod +x build.sh
   ./build.sh
   ```
   *(Alternatively, run `make` if installed).*

## Submission Compliance
* **Executable Provided:** A pre-compiled `.jar` file is included in the repository.
* **JRE Only:** The executable runs purely on a standard JRE 17+.
* **No Third-Party Libraries:** The JSON parser (`org.json`) has been internalized into the source code, removing any external dependencies.
* **Self-Contained:** The JAR file bundles all necessary assets (images, configs) and logic.
