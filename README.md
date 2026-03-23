# MediaTracker

**MediaTracker** is a personal web application for tracking movies and series.
It allows you to maintain a "plan to watch" list, track your series viewing progress, and sort/filter content. Data is stored locally in PostgreSQL, ensuring privacy and robust data management without relying on external cloud services.

## Features

*   **Content Separation:** Movies, Series, Anime, Animation, Live Action.
*   **Smart Search:** Instant search by title and director with **highlighting** of the found text.
*   **Filtering and Sorting:**
    *   Filter by type (e.g., Anime only) and status (Watching / Dropped / Planned / Completed).
    *   Sort by Year, Duration, Title, Type, and **Status**.
*   **Series Tracking:** Convenient `+` / `-` buttons to change the number of watched episodes directly in the list.
*   **Automation:** Status automatically changes to **"Completed"** when the number of episodes reaches the maximum.
*   **Technology:** Modern stack built on Java 21.

---

## Tech Stack

*   **Backend:** Java 21, Spring Boot 3
*   **Database:** PostgreSQL 16
*   **ORM:** Spring Data JPA + Hibernate (using Specifications for dynamic search optimization)
*   **Frontend:** Thymeleaf (Server-Side Rendering), Bootstrap 5
*   **Deployment:** Docker & Docker Compose

---

## How to Run

### Option 1: Using Docker (Recommended)
You only need Docker installed.

1.  Clone the project:
    ```bash
    git clone https://github.com/kudryavcAS/Media-Tracker.git
    cd Media-Tracker
    ```
2.  Start the application with one command:
    ```bash
    docker-compose up -d
    ```
    *(The first launch might take a couple of minutes to build the image)*

3.  Open in your browser:[http://localhost:8080](http://localhost:8080)

> **Important:** The database is saved in the `./test_db_data` folder inside the project, so data will not be lost upon restart.

### Option 2: Locally (Java)
Requires JDK 21 installed and a running PostgreSQL instance.

1.  Build the project:
    ```bash
    # Windows
    .\mvnw clean package
    
    # Linux / macOS
    ./mvnw clean package
    ```
2.  Run the JAR file:
    ```bash
    java -jar target/mediatracker-0.0.1-SNAPSHOT.jar
    ```
---