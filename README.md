# Vroom — Uber-Like Transport Platform

> A full-stack ride-sharing platform built for the **SIIT** curriculum (2025/2026).  
> Covers the complete SDLC: REST API design, web client, Android application, and automated testing.

---

## Table of Contents

- [Overview](#overview)
- [User Roles](#user-roles)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
  - [Backend](#backend)
  - [Web Frontend](#web-frontend)
  - [Mobile (Android)](#mobile-android)
- [Pricing Model](#pricing-model)
- [Testing](#testing)
- [Agile Process](#agile-process)

---

## Overview

Vroom minimizes friction in urban transport by automating driver assignment, providing real-time vehicle tracking, and enabling structured communication between passengers, drivers, and administrators — all in one platform.

---

## User Roles

| Role | Description |
|---|---|
| **Unregistered User** | Browse the live vehicle map and request price / ETA estimates without an account |
| **Registered Passenger** | Book on-demand or scheduled rides, manage favorite routes, link co-passengers, trigger PANIC alerts, and rate drivers |
| **Driver** | Receive automated ride assignments, navigate routes, track daily active hours (8h cap enforced), and manage availability |
| **Administrator** | Create driver accounts, monitor all active rides, respond to PANIC alerts, manage user access, define pricing, and provide 24/7 live chat support |

---

## Key Features

### Booking & Dispatch
- Immediate and future-scheduled rides (up to 5 hours in advance)
- Multi-stop routing with ordered waypoints
- Linked co-passengers via email — each receives live tracking access
- Smart driver assignment: nearest free driver
- Automatic rejection with notification if no eligible driver is available

### Real-Time Tracking
- Live vehicle positions on an interactive map (Leaflet / OpenStreetMap)
- ETA updates as the vehicle approaches the destination
- Mid-ride inconsistency reporting by passengers (text note, stored in ride report)

### Safety
- **PANIC Button** — available to both passengers and drivers; triggers an audible and visual alert to all online administrators, and highlights the vehicle on the map
- Administrator dashboard for active PANIC notifications

### Ride Lifecycle
- Driver marks ride start after all passengers have boarded
- Early termination supported: price is recalculated based on actual stop location
- Driver marks ride as complete and paid; status resets automatically
- Passengers can cancel up to 10 minutes before a scheduled ride
- Drivers can reject or abort a ride with a mandatory reason

### History & Reports
- Full ride history for passengers, drivers, and administrators
- Filterable and sortable by date, cost, route, and PANIC status
- Date-range analytics with daily graphs: ride count, kilometres travelled, revenue / spend, cumulative totals, and averages
- Administrators can generate global reports or per-user reports

### Profile Management
- Profile photo, contact details, and password change for all roles
- Driver profile changes require administrator approval before going live
- Drivers see their cumulative active hours for the past 24 hours

### Live Support
- Persistent one-on-one chat between any user and an administrator
- Full message history retained across sessions

### Administration
- Block / unblock passengers and drivers with a visible note explaining the reason
- Define and update pricing per vehicle type (Standard, Luxury, Minivan)
- View real-time ride status for any driver

---

## Tech Stack

### Backend
| Concern | Technology |
|---|---|
| Language & Framework | Java 17+, Spring Boot |
| Database | H2 (embedded) |
| Authentication | JWT (JSON Web Tokens) |
| Email | JavaMail API |
| Real-time | WebSockets (notifications & live tracking) |
| API Docs | OpenAPI |

### Web Frontend
| Concern | Technology |
|---|---|
| Framework | Angular 16+ (TypeScript) |
| UI Components | Angular Material |
| Maps | Leaflet (OpenStreetMap) |
| Design | Figma |
| Auth Guard | Angular AuthGuard + JWT |

### Mobile (Android)
| Concern | Technology |
|---|---|
| Language | Java |
| UI | Material Design 3 |
| Architecture | UI / Domain / Data layers |
| Maps | OpenStreetMap |
| Local Storage | SharedPreferences |
| Notifications | Firebase Cloud Messaging |

### Testing
| Scope | Tool |
|---|---|
| Backend unit & integration | JUnit / TestNG |
| Frontend unit | Jasmine |
| End-to-end | Selenium |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   Clients                           │
│  ┌──────────────────┐   ┌─────────────────────────┐ │
│  │  Angular Web App │   │   Android Mobile App    │ │
│  │  (port 4200)     │   │   (emulator / device)   │ │
│  └────────┬─────────┘   └────────────┬────────────┘ │
└───────────┼──────────────────────────┼──────────────┘
            │  REST + WebSocket        │  REST + WebSocket
┌───────────▼──────────────────────────▼───────────────┐
│               Spring Boot API (port 8080)            │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐ │
│  │  Auth    │  │  Rides   │  │  Notifications/WS   │ │
│  │  (JWT)   │  │  Service │  │  (WebSocket broker) │ │
│  └──────────┘  └──────────┘  └─────────────────────┘ │
│  ┌─────────────────────────────────────────────────┐ │
│  │              H2 Database (embedded)             │ │
│  └─────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────┘
```

---

## Prerequisites

Ensure all of the following are installed before proceeding:

| Tool | Minimum Version |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| Node.js & npm | Node 18+ |
| Angular CLI | 16+ |
| Android Studio | Latest stable |

---

## Installation

### Backend

```bash
# 1. Navigate to the backend directory
cd server/vroom

# 2. Configure application properties
#    Edit: src/main/resources/application.properties
#    Set your SendGrid API key and any custom DB settings

# 3. Start the server
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

> **Note:** The H2 in-memory console is accessible at `http://localhost:8080/h2-console` during development.

---

### Web Frontend

```bash
# 1. Navigate to the frontend directory
cd web/vroom

# 2. Install dependencies
npm install

# 3. Start the development server
ng serve
```

Open `http://localhost:4200` in your browser.

---

### Mobile (Android)

1. Open the `/mobile` directory as a project in **Android Studio**.
2. Locate the app configuration file and set `BASE_URL` to your machine's local IP address.

   ```java
   // Example configuration
   private static final String BASE_URL = "http://192.168.1.x:8080/api/";
   
   // When using the Android Emulator, use:
   private static final String BASE_URL = "http://10.0.2.2:8080/api/";
   ```

3. Sync Gradle, then **Build → Run** on a physical device or emulator.

---

## Pricing Model

Ride cost is calculated using the following formula:

```
total_price = base_price_by_vehicle_type + (distance_km × 120)
```

| Vehicle Type | Base Price |
|---|---|
| Standard | Configured by administrator |
| Luxury | Configured by administrator |
| Minivan | Configured by administrator |

Administrators can update base prices at any time from the admin dashboard. Each completed ride record stores both the final total and the per-km rate that was active at the time of booking.

---


## Testing

```bash
# Backend — JUnit
cd server/vroom/src/test/java/org/example/vroom # choose which type of tests for which layer you want
mvn test  

# Frontend — Jasmine (via Karma)
cd web/vroom
ng test

# End-to-End — Selenium
cd server/vroom/src/test/java/org/example/vroom/e2e
mvn test   # or use the configured test runner
```

## Agile Process

This project followed the **Scrum** methodology across multiple sprints:
- **Task Management:** Trello Board
- **Roles:** Rotating Scrum Master and Product Owner roles
- **Artifacts:** Sprint Backlogs, Burndown Charts, and Retrospective documents
---

*Academic project — SIIT, 2025/2026*
