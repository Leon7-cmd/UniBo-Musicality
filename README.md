# 🎵 Musicality

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-4285F4?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=flat-square&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Security: BCrypt](https://img.shields.io/badge/Security-BCrypt-4CAF50?style=flat-square)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)

> *A full-featured desktop platform for streaming, playlist curation, and music and podcast evaluations.*

## 🌟 Key Highlights

- **Role-Based Access Control (RBAC):** Dedicated dashboards and permission sets for Listeners, Creators, and Administrators.
- **Multi-Dimensional Rating System:** Granular song evaluations across three distinct parameters (Vocals, Lyrics, Production/Beat) with dynamically generated leaderboards.
- **Robust Architecture:** Clean Model-View-Controller (MVC) pattern coupled with the Data Access Object (DAO) pattern to decouple business logic from persistence.
- **Optimized SQL Analytics:** Advanced queries using aggregation functions, `GROUP BY`, and `HAVING` clauses for real-time statistical insights.
- **Secure Authentication:** Account passwords hashed and salted via the BCrypt cryptographic algorithm.

## ℹ️ Overview

**Musicality** is an engineering-driven desktop media streaming application. Designed to model complex relational database workflows, the platform enables users to stream music and podcasts, curate custom playlists, manage subscription tiers, and submit detailed multi-criteria reviews.

The application is built on top of native, optimized MySQL queries without the overhead of heavy ORMs, ensuring predictable performance and explicit transaction management.

<img src="https://github.com/user-attachments/assets/1db61559-fb35-4955-88aa-bd80ac19d585" width="400"/>

## 🚀 Core Features

- **Listener & Creator Workflows:** Sign up as an artist to upload tracks and lyrics, or as a listener to discover new releases and follow creators.
- **Tag-Based Discovery & Subscriptions:** Browse media categorized by custom genres/tags and simulate subscription plan management.
- **Live Leaderboards:** View dynamic rankings (Top 50 Artists, Top 50 Songs) computed directly from the rating engine.
- **Moderation Dashboard:** Administrative interface for content moderation, lyrics approval, and community reports.

## 🛠️ Tech Stack & Architecture

- **Language & UI:** Java 17+, JavaFX (FXML & CSS styling).
- **Architecture Pattern:** Model-View-Controller (MVC) with Data Access Object (DAO) layer.
- **Persistence & Database:** MySQL 8.0, JDBC (native prepared statements and connection management).
- **Security:** JBCrypt password hashing.
- **Build & Dependency Management:** Apache Maven.

## 💻 Developer Setup

### Prerequisites
- **Java JDK 17** or higher installed and configured.
- **Apache Maven 3.8+**.
- **MySQL Server** running locally.

### Installation & Execution

1. Clone the repository:
   git clone https://github.com/Leon7-cmd/musicality.git
   cd musicality

2. Initialize the database schema:
   Import the provided SQL schema into your local MySQL instance:
   mysql -u root -p < schema.sql

3. Configure database credentials:
   Open `Database.java`, enter your local MySQL credentials (username and password), and save the file.

4. Build the project with Maven:
   mvn clean install

5. Run the desktop application:
   mvn javafx:run

## 📄 License

Distributed under the MIT License. See the [LICENSE](LICENSE) file for details.
