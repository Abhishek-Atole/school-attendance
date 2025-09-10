# School Attendance Management System

A comprehensive web-based attendance management system built with Spring Boot backend and React frontend.

## 🚀 Features

### Backend (Spring Boot)
- **Student Management**: Complete CRUD operations for student records
- **Teacher Management**: Teacher profiles with subject and class assignments
- **Attendance Tracking**: Daily attendance marking with multiple status options
- **Report Generation**: Export attendance reports in CSV, Excel, and PDF formats
- **Database Integration**: MariaDB with proper relationships and constraints
- **REST APIs**: Complete RESTful API endpoints for all operations
- **School Branding**: Custom school logo and information in reports

### Frontend (React)
- **Responsive Design**: Mobile-first approach with TailwindCSS
- **Modern UI**: Clean and intuitive interface with Lucide React icons
- **Authentication**: JWT-based login system with protected routes
- **Dashboard**: Statistics, charts, and quick actions overview
- **Student Management**: Search, filter, add, edit, and delete students
- **Teacher Management**: Manage teachers with subject/class assignments
- **Attendance Interface**: User-friendly attendance marking with bulk operations
- **Reports**: Generate and export reports with various filters
- **Data Visualization**: Charts and graphs using Recharts library

## �️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.1.4
- **Database**: MariaDB
- **Build Tool**: Maven
- **Java Version**: 17+
- **Key Dependencies**:
  - Spring Data JPA
  - Spring Web
  - Apache POI (Excel generation)
  - iText (PDF generation)
  - OpenCSV (CSV generation)

### Frontend
- **Framework**: React 18
- **Build Tool**: Vite
- **Styling**: TailwindCSS
- **Routing**: React Router v6
- **State Management**: React Query (TanStack Query)
- **HTTP Client**: Axios
- **Icons**: Lucide React
- **Charts**: Recharts
- **Forms**: React Hook Form

## 📋 Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- MariaDB 10.5 or higher
- Maven 3.6 or higher

## 🔧 Installation & Setup

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd school-attendance
   ```

2. **Database Configuration**
   - Create a MariaDB database named `school_attendance`
   - Update database credentials in `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/school_attendance
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify Backend**
   - The backend will start on `http://localhost:8080`
   - Check API documentation at `http://localhost:8080/swagger-ui.html` (if Swagger is configured)

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Access Application**
   - The frontend will start on `http://localhost:5173`
   - Default login credentials: admin/admin123

## 📊 Database Schema

### Core Tables
- **schools**: School information and configuration
- **students**: Student records with personal details
- **teachers**: Teacher profiles and contact information
- **subjects**: Subject master data
- **classes**: Class and section information
- **teacher_subjects**: Teacher-subject assignments
- **teacher_classes**: Teacher-class assignments
- **attendance**: Daily attendance records

### Key Relationships
- Students belong to classes and sections
- Teachers can be assigned to multiple subjects and classes
- Attendance records link students with date and status
- All entities are associated with a school

## 🔌 API Endpoints

### Students
- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `POST /api/students` - Create new student
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### Teachers
- `GET /api/teachers` - Get all teachers
- `GET /api/teachers/{id}` - Get teacher by ID
- `POST /api/teachers` - Create new teacher
- `PUT /api/teachers/{id}` - Update teacher
- `DELETE /api/teachers/{id}` - Delete teacher

### Attendance
- `GET /api/attendance` - Get attendance records
- `POST /api/attendance` - Mark attendance
- `PUT /api/attendance/{id}` - Update attendance
- `GET /api/attendance/date/{date}` - Get attendance for specific date

### Reports
- `GET /api/reports/csv` - Export CSV report
- `GET /api/reports/excel` - Export Excel report
- `GET /api/reports/pdf` - Export PDF report

## 🎨 Frontend Pages

### Authentication
- **Login Page**: User authentication with form validation

### Dashboard
- **Overview**: Statistics cards with key metrics
- **Charts**: Attendance trends and class-wise data
- **Quick Actions**: Shortcuts to common tasks

### Management Pages
- **Students**: Complete student management with search and filters
- **Teachers**: Teacher management with subject/class assignments
- **Attendance**: Daily attendance marking interface

### Reports
- **Export Options**: CSV, Excel, and PDF export functionality
- **Filters**: Date range, class, section, and report type filters
- **Quick Reports**: Pre-configured report templates

## 📱 Responsive Design

The application is built with a mobile-first approach and supports:
- **Mobile**: 375px and up
- **Tablet**: 768px and up
- **Desktop**: 1024px and up

Key responsive features:
- Collapsible sidebar on mobile
- Responsive tables with horizontal scroll
- Touch-friendly buttons and interactions
- Optimized layouts for all screen sizes

## 🔒 Security Features

- JWT-based authentication
- Protected routes in frontend
- Input validation on both frontend and backend
- SQL injection prevention with parameterized queries
- XSS protection with proper input sanitization

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file: `mvn clean package`
2. Deploy to your server: `java -jar target/school-attendance-0.0.1-SNAPSHOT.jar`

### Frontend Deployment
1. Build for production: `npm run build`
2. Deploy the `dist` folder to your web server

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
