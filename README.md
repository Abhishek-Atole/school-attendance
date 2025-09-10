# School Attendance Management System

A comprehensive web-based solution for managing student attendance in educational institutions.

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- MariaDB 10.6+
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Database Setup
```bash
# Create database
mysql -u root -p
CREATE DATABASE school_attendance;
```

## 🏗️ Architecture

- **Frontend**: React.js with Material-UI
- **Backend**: Spring Boot with Spring Security
- **Database**: MariaDB
- **Authentication**: JWT-based

## 📁 Project Structure

```
school-attendance/
├── backend/           # Spring Boot application
├── frontend/          # React.js application
├── docs/             # Documentation
├── docker-compose.yml # Docker setup
└── README.md
```

## 🔧 Features

- ✅ Student Management
- ✅ Teacher Management
- ✅ Attendance Tracking
- ✅ Report Generation
- ✅ Role-based Access Control
- ✅ Data Export (PDF, Excel, CSV)

## 📊 Documentation

- [High-Level Design](docs/HLD.md)
- [Low-Level Design](docs/LLD.md)
- [Database Schema](docs/ERD.md)

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.
