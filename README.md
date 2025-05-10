# 🏢 EliteDesk - Modern Workspace Management System (Frontend)

<div align="left">

![Java](https://img.shields.io/badge/Java-17-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-17-blue) ![Status](https://img.shields.io/badge/Status-Developed-brightgreen)

</div>

## 📋 Overview

EliteDesk is a sophisticated workspace management system built with Java and JavaFX, designed to streamline the process of booking and managing various types of workspaces. The application provides an intuitive interface for users to view, book, and manage their workspace reservations efficiently.

## ✨ Features

### 🔐 Authentication & User Management
- Secure login and registration system ([`LoginController.java`](src/main/java/com/elitedesk/LoginController.java))
- Role-based access control ([`AuthService.java`](src/main/java/com/elitedesk/service/AuthService.java))
- Session management with JWT tokens ([`SessionManager.java`](src/main/java/com/elitedesk/service/SessionManager.java))
- User profile management ([`User.java`](src/main/java/com/elitedesk/model/User.java))

### 🏢 Space Management
- Multiple space types support ([`SpaceType.java`](src/main/java/com/elitedesk/SpaceType.java)):
  - Conference Rooms
  - Meeting Rooms
  - Offices
  - Coworking Spaces
  - Event Spaces
  - Studios
- Detailed space information ([`Space.java`](src/main/java/com/elitedesk/Space.java)):
  - Capacity
  - Location
  - Pricing
  - Availability status

### 📅 Reservation System
- Interactive calendar view ([`CalendarController.java`](src/main/java/com/elitedesk/CalendarController.java))
- Real-time availability checking ([`ReservationService.java`](src/main/java/com/elitedesk/service/ReservationService.java))
- Time slot selection ([`TimeSlot.java`](src/main/java/com/elitedesk/model/TimeSlot.java))
- Reservation management ([`Reservation.java`](src/main/java/com/elitedesk/model/Reservation.java)):
  - Create new reservations
  - Modify existing bookings
  - Cancel reservations
  - View booking history

### 🎨 Modern UI/UX
- Clean and intuitive interface ([`application.css`](src/main/resources/styles/application.css))
- Responsive design
- Real-time updates
- Interactive calendar visualization
- Custom styling and themes

## 🏗️ Architecture

### Frontend
- **JavaFX**: Modern UI framework
- **FXML**: Declarative UI design
  - [`login.fxml`](src/main/resources/fxml/login.fxml)
  - [`main_layout.fxml`](src/main/resources/fxml/main_layout.fxml)
  - [`calendar_view.fxml`](src/main/resources/fxml/calendar_view.fxml)
  - [`space_booking.fxml`](src/main/resources/fxml/space_booking.fxml)
- **CSS**: Custom styling and theming

### Backend
- **Java 17**: Core application logic
- **HTTP Client**: API communication
- **Jackson**: JSON processing
- **Session Management**: Secure user sessions

### Key Components
- **Controllers**: Handle UI interactions
  - [`MainController.java`](src/main/java/com/elitedesk/MainController.java)
  - [`MainLayoutController.java`](src/main/java/com/elitedesk/MainLayoutController.java)
  - [`SpaceBookingController.java`](src/main/java/com/elitedesk/SpaceBookingController.java)
- **Services**: Business logic implementation
  - [`SpaceService.java`](src/main/java/com/elitedesk/service/SpaceService.java)
  - [`ReservationService.java`](src/main/java/com/elitedesk/service/ReservationService.java)
- **Models**: Data structures
  - [`Reservation.java`](src/main/java/com/elitedesk/model/Reservation.java)
  - [`TimeSlot.java`](src/main/java/com/elitedesk/model/TimeSlot.java)
- **Config**: Application configuration
  - [`AppConfig.java`](src/main/java/com/elitedesk/config/AppConfig.java)

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven or Gradle
- JavaFX 17

### Installation

1. Clone the repository:
```bash
git clone https://github.com/agrawalabr/EliteDesk-App.git
```

2. Navigate to the project directory:
```bash
cd EliteDesk-App
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw javafx:run
```

## 📱 Application Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── elitedesk/
│   │           ├── config/         # Configuration classes
│   │           │   └── AppConfig.java
│   │           ├── model/          # Data models
│   │           │   ├── Reservation.java
│   │           │   ├── TimeSlot.java
│   │           │   └── User.java
│   │           ├── service/        # Business logic
│   │           │   ├── AuthService.java
│   │           │   ├── ReservationService.java
│   │           │   ├── SessionManager.java
│   │           │   └── SpaceService.java
│   │           └── controllers/    # UI controllers
│   │               ├── CalendarController.java
│   │               ├── LoginController.java
│   │               ├── MainController.java
│   │               └── SpaceBookingController.java
│   └── resources/
│       ├── fxml/                   # FXML layouts
│       │   ├── calendar_view.fxml
│       │   ├── login.fxml
│       │   ├── main_layout.fxml
│       │   └── space_booking.fxml
│       └── styles/                 # CSS styles
│           └── application.css
```

## 🔧 Configuration

The application can be configured through [`AppConfig.java`](src/main/java/com/elitedesk/config/AppConfig.java):
- API endpoints
- Authentication settings
- Environment variables

## 🎨 UI Components

### Main Views
- Login/Registration ([`login.fxml`](src/main/resources/fxml/login.fxml))
- Dashboard ([`main_layout.fxml`](src/main/resources/fxml/main_layout.fxml))
- Space Management ([`main.fxml`](src/main/resources/fxml/main.fxml))
- Reservation Calendar ([`calendar_view.fxml`](src/main/resources/fxml/calendar_view.fxml))
- Booking Interface ([`space_booking.fxml`](src/main/resources/fxml/space_booking.fxml))

### Features
- Interactive calendar
- Real-time availability updates
- Custom styling ([`application.css`](src/main/resources/styles/application.css))
- Responsive layouts

## 🔐 Security

- JWT-based authentication ([`AuthService.java`](src/main/java/com/elitedesk/service/AuthService.java))
- Secure session management ([`SessionManager.java`](src/main/java/com/elitedesk/service/SessionManager.java))
- Role-based access control
- Input validation
- Error handling

## 📈 Future Enhancements

- [ ] Mobile application
- [ ] Real-time notifications
- [ ] Analytics dashboard
- [ ] Integration with calendar services
- [ ] Payment gateway integration


## 👥 Authors

- **Abhishek Agrawal** - *Developer* - [![GitHub](https://img.shields.io/badge/GitHub-Profile-informational?logo=github)](https://github.com/agrawalabr) [![LinkedIn](https://img.shields.io/badge/LinkedIn-Profile-blue?logo=linkedin)](https://www.linkedin.com/in/agrawalabr)
- **Maneesh Kolli** - *Developer* - [![GitHub](https://img.shields.io/badge/GitHub-Profile-informational?logo=github)](https://github.com/Maneeshk11) [![LinkedIn](https://img.shields.io/badge/LinkedIn-Profile-blue?logo=linkedin)](https://www.linkedin.com/in/maneeshkolli)

## 📚 References

- JavaFX Documentation
- OpenJDK Documentation
- Contributions from the open-source community

---

<div align="center">
Made with ❤️ by EliteDesk Team
</div>