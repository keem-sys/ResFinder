# Student Accommodation Finder App 

[![Java](https://img.shields.io/badge/Java-17+-orange.svg?style=flat-square)](https://www.java.com/)
[![Swing](https://img.shields.io/badge/Java%20Swing-GUI-blue.svg?style=flat-square)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![H2 Database](https://img.shields.io/badge/H2%20Database-Embedded-lightgrey.svg?style=flat-square)](https://www.h2database.com/html/)
[![License](https://img.shields.io/badge/Apache%202.0-License-blue.svg?style=flat-square&logo=apache)](LICENSE) 

<!-- TODO: ADD SCREENSHOT OF THE APP -->

## Project Overview

The **Student Accommodation Finder App** is a desktop application developed in Java Swing as part of an assignment for the
**PRT262S Project 2 (Application Development)** subject at the Cape Peninsula University of Technology (CPUT) in 2025.

This application goal is to provide CPUT students and students more broadly, 
with a user-friendly and alternative way to search for suitable, affordable, and conveniently located accommodations.

## Problem Definition

CPUT students often encounter significant difficulties in finding appropriate accommodation near campuses. The official CPUT accommodation platform can be overloaded and inefficient, leaving many students scrambling to find alternative housing solutions in a limited and competitive market. This desktop application aims to alleviate these challenges by providing an alternative platform for students to:

- Discover a wider range of accommodation options beyond just university residences.
- Filter and search listings based on various criteria (campus location, price, room type, accreditation, availability).
- Access comprehensive details about accommodation listings (accreditation type, address, pricing, rules, images).
- Directly contact accommodation providers.

## Key Features (Term 1 & Planned)

**Currently Implemented (Term 1 Deliverables):**

- **User Authentication:**
    - Secure user registration (Full Name, Username, Email, Password).
    - Secure login using Username/Email and Password.
    - Secure password hashing using Argon2-jvm.
    - JWT (JSON Web Token) based session management (JWT generation implemented).
    - "Remember Me" functionality using Java Preferences API for persistent JWT storage (basic implementation).
    - Client-side and server-side input validation for registration and login forms.
    - Basic error handling and user feedback in UI.
- **Basic UI Structure:**
    - Login Panel (using JGoodies FormLayout).
    - Registration Panel (using JGoodies FormLayout).
    - Panel switching between Login and Registration forms.

**Planned Features (Future Terms):**

- **Accommodation Listing Management:**
    - Display a list of pre-populated accommodation listings.
    - Detailed view for individual accommodation listings.
    - Display listings with comprehensive details (accreditation, address, pricing, rules, images).
- **Search, Filtering, and Sorting:**
    - Keyword search functionality.
    - Filtering by accommodation type, campus location, room type, accreditation, and availability.
    - Sorting listings by price, distance to campus, and relevance.
- **Map Integration:**
    - Display accommodation listings on an interactive map.
    - Indicate campus locations.
- **Contact Us Feature:**
    - Basic "Contact Us" form for user inquiries.
- **System and User Documentation:**
    - Comprehensive system documentation.
    - User manual/guide.

## Technologies Used

- **Programming Language:** Java (version 17 or higher)
- **GUI Toolkit:** Java Swing
- **Database:** H2 Embedded Database
- **Password Hashing:** Argon2-jvm library
- **JWT (JSON Web Token):** JJWT library (for authentication and session management)
- **Testing Framework:** JUnit 5
- **Build Tool:** Maven 

## Setup and Running Instructions

**Prerequisites:**

- **Java Development Kit (JDK):**  Version 17 or higher. Download from [Oracle Java Downloads](https://www.oracle.com/java/technologies/javase-jdk-downloads.html) or [OpenJDK](https://openjdk.java.net/).
- **Integrated Development Environment (IDE)**
- **Git:** [Git SCM](https://git-scm.com/downloads)

**Steps to Setup and Run:**

1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/keem-sys/ResFinder.git]
    cd [repository-folder-name]
    ```
2.  **Open the Project in your IDE**

3.  **Build the Project**

4. **Run the Application:**


## Contributors
This project is developed and maintained by:

<div style="text-align: center;">

  <a href="https://github.com/keem-sys" target="_blank">
    <img src="https://github.com/keem-sys.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;" alt="Keem-sys"/>
    <br />
  </a>

  <a href="https://github.com/AidenWallace" target="_blank">
    <img src="https://github.com/AidenWallace.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;" alt="Aiden Clinton Wallace"/>
    <br />

  </a>
    <a href="https://github.com/Jaydenchoppa" target="_blank">
    <img src="https://github.com/Jaydenchoppa.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;"
    alt="Jayden Avontuur"/>
    <br />
  </a>

  <a href="https://github.com/Matthew-codez" target="_blank">
    <img src="https://github.com/Matthew-codez.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;"
    alt="Matthew"/>
    <br />
  </a>

  <a href="https://github.com/WazeerG" target="_blank">
    <img src="https://github.com/WazeerG.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;"
    alt="Wazeer"/>
    <br />
    <sub><b>Wazeer</b></sub>
  </a>

   <a href="https://github.com/Gwangwa-Innocentia" target="_blank">
    <img src="https://github.com/Gwangwa-Innocentia.png?size=100" width="80px;" style="border-radius:50%; border: 1px solid #888;"
      alt="Innocentia Gwangwa"/>
    <br />
  </a>
</div>

## License

[Apache License 2.0](LICENSE)

This project is licensed under the **Apache License 2.0**.

This license is a permissive open-source license that grants broad freedoms to use, modify, 
and distribute the software for both commercial and non-commercial purposes. See the `LICENSE` file for the full license text.

## Contact Information
You can contact us by email at [ResFinder](mailto:iamwriter@regnum.slmail.me)