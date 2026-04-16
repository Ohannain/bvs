# BVS - Library Management System

BVS is a small console-based library management system written in Java.

## Technologies and Architecture

- Java (standard library)
- Gradle as build tool
- JSON files for persistence (Gson or Jackson)
- JUnit for tests
- Text-based console application (no GUI)

## Features

- User management: create, edit, search, list, block, and delete users.
- Media management: maintain books, DVDs, and CDs with status tracking.
- Lending: borrow, return, and extend loans.
- Reservations: create and manage reservations.
- Fines: calculate and track overdue fines.
- Reports: annual, monthly, usage, popularity, trend, overdue, and fine statistics.
- Persistence: stores data as JSON files.
- CLI: runs as a text-based application without a GUI.

## Quick Use

1. Start the application.
2. Follow the console menu.
3. Select a module and enter the requested data.
4. Close the program from the menu when you are done.

## Installation

### Windows

1. Install a current Java JDK.
2. Open a terminal in the project folder.
3. Run `gradlew.bat test` to verify the setup.
4. Run `gradlew.bat run` to start the application.

### Linux

1. Install a current Java JDK.
2. Open a terminal in the project folder.
3. Run `./gradlew test` to verify the setup.
4. Run `./gradlew run` to start the application.

### macOS

1. Install a current Java JDK.
2. Open a terminal in the project folder.
3. Run `./gradlew test` to verify the setup.
4. Run `./gradlew run` to start the application.

## Notes

- The first start creates the `data` directory automatically if it does not exist.
- The project uses Gradle Wrapper, so no separate Gradle installation is required.

