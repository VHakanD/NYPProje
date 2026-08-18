<div align="center">
  <img src="https://github.com/VHakanD/NYPProje/blob/main/HAZERFEN%20AIRLINES.png" alt="Logo">
</div>

## Badges


[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)

# Hazerfen Airlines Reservation System

**Course:** BLM2012 - Object Oriented Programming (Fall 2025-2026)  
**Language:** Java (JavaFX)  
**Architecture:** File-Based Data Storage
**Authors:** By Zeynep & Hakan
  - [@Hakan](https://www.github.com/VHakanD)
  - [@Zeynep](https://github.com/zeyneppkts)


## Project Overview
The aim of this project is to demonstrate the principles of object-oriented programming (abstraction, polymorphism, inheritance and encapsulation) using the Java programming language through a comprehensive airline reservation and management system. The project provides a GUI for managers and passengers, simulating real-world operations such as flight scheduling, dynamic pricing, real-time filtering search, seat selection, and concurrency handling in GUI.

The system has been built without a traditional database as required by the rules; instead, it uses a special File I/O architecture to make data persistent across sessions using .txt files.

## Features and Functionality ##
We explained what our project does, now we will tell you how it works:
In this project, every operation is seperated to it's category:
### 1-Admin and Passenger Panels
* **Admin Panel:**
  * **Dashboard:** A central hub for managing Flights, Staff, and System Reports.
  * **Flight Management:** Add, update, delete flights with automatic validation ( e.g., preventing past dates ).
  * **Staff Management:** Manage airline personnel with role-based access control.
  * **Reports:** Generate asynchronous occupancy reports without freezing the UI.

* **Passenger Panel:**
  * **Flight Search:** Real-time filtering by city, date, or flight number.
  * **Visual Seat Selection:** Simple but standart seat map distinguishing Economy, Business, and Occupied seats.
  * **Reservation Management:** Add reservation( *Under the passenger's name, for themselves and others* ), view active tickets, cancel reservations (valid until 24h before flight), and **change seats** ( *paying the price difference if upgrading, but if you downgrade there is no refund by choice* :] ).

### 2. Pricing Engine
Ticket prices are calculated dynamically based on:
  * **Base Distance:** Kilometers between cities.
  * **Seat Class:** Business class is 1.5x the price of Economy.
  * **Dynamic Factors:** "Last Minute" flights ( <3 days left ) incur a 1.5x surcharge.

### 3. Concurrency & Multithreading Simulation
A dedicated simulation screen demonstrates thread safety:
  * **Unsafe Mode:** Simulates 90 threads trying to book seats without synchronization, visualizing race conditions ( double bookings ).
  * **Safe Mode:** Uses `synchronized` blocks to ensure data integrity, resulting in zero errors.

### Prerequisites
  * **Java Development Kit (JDK):** Version 17 or higher.
  * **JavaFX SDK:** Ensure your IDE ( IntelliJ/Eclipse ) is configured to load JavaFX modules.

### Installation
1.  Clone the repository:
    ```bash
    git clone [https://github.com/VHakanD/NYPProje.git](https://github.com/VHakanD/NYPProje.git)
    ```
2.  Open the project in your IDE.
3.  **Run:** Execute the `gui.MainApp` class to launch the application.

## Default Credentials
For testing purposes, the system is pre-loaded with the following Admin accounts:

| Username | Password | Role |
| :--- | :--- | :--- |
| `VHakanD` | `1234` | Admin |
| `zeyneppkts` | `5678` | Admin |
| `admin` | `admin1` | Admin |

*(Passengers can register for a new account on the Login screen).*

## 📂 Project Structure
```text
src/
├── flightManagement/       # Core Models (Flight, Plane, Seat, Route)
├── servicesAndManagers/    # Business Logic (FlightManager, ReservationManager)
├── reservationAndTicketing/# Reservation Entities (Ticket, Passenger)
├── gui/                    # JavaFX Views (Controllers & UI Layouts)
├── tests/                  # JUnit 5 Test Cases
├──
├── Files/                  # Text files for data storage
