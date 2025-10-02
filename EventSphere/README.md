# EventSphere

EventSphere is an interactive web portal designed to bring small local events to the attention of the community. It serves as a platform where organizers can post their events and community members can explore, RSVP, volunteer, and participate in these events.

## Features

### For Users
- **User Registration & Login**: Create accounts to RSVP or volunteer for events
- **Event Browsing**: View upcoming local events with filtering by category, date, or location
- **RSVP/Participation**: RSVP to events you're interested in attending
- **Volunteer Registration**: Sign up to help organize or assist at events
- **Event Details**: Access detailed information about each event
- **Search & Filter**: Search for events by keyword, type, or location
- **Notifications**: Email notifications about new or upcoming events

### For Organizers
- **Event Creation**: Post new events with details like date, time, location, and description
- **Participant Tracking**: View RSVPs and volunteer registrations
- **Event Management**: Update, cancel, or delete events as needed
- **Analytics Dashboard**: See participation trends and user engagement metrics

### For Admins
- **User Management**: Manage and monitor user accounts
- **Event Moderation**: Approve or reject event submissions to maintain quality
- **Reports**: Generate reports on events, participation, and feedback

## Technology Stack

- **Backend**: Spring Boot 3.5.6 (Java 21)
- **Database**: MySQL
- **Security**: Spring Security
- **Frontend**: Thymeleaf, Bootstrap 5, JavaScript
- **Build Tool**: Maven
- **Email**: JavaMail API

## Prerequisites

- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd EventSphere
```

### 2. Database Setup
1. Install MySQL and create a database:
```sql
CREATE DATABASE eventsphere;
```

2. Update the database configuration in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/eventsphere?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Email Configuration (Optional)
Update email settings in `application.properties` for notifications:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### 4. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## Default Admin Account

- **Username**: admin
- **Password**: admin123
- **Role**: ADMIN

## Usage

### Getting Started
1. Visit `http://localhost:8080`
2. Register as a user or organizer
3. Browse events or create your own
4. RSVP to events or volunteer to help

### User Roles
- **USER**: Can browse events, RSVP, and volunteer
- **ORGANIZER**: Can create and manage events
- **ADMIN**: Full system access and user management

### Event Categories
- Workshop
- Hackathon
- Donation Drive
- Meetup
- Conference
- Seminar
- Other

## API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /events` - Browse events
- `GET /events/{id}` - Event details
- `GET /login` - Login page
- `GET /register` - User registration
- `GET /register-organizer` - Organizer registration

### User Endpoints
- `GET /user/profile` - User profile
- `POST /user/update-profile` - Update profile
- `POST /events/{id}/rsvp` - RSVP to event
- `POST /events/{id}/volunteer` - Volunteer for event

### Organizer Endpoints
- `GET /events/create` - Create event form
- `POST /events/create` - Create event
- `GET /events/{id}/edit` - Edit event form
- `POST /events/{id}/edit` - Update event

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - Manage users
- `GET /admin/events` - Manage events
- `POST /admin/users/{id}/enable` - Enable user
- `POST /admin/users/{id}/disable` - Disable user
- `POST /admin/users/{id}/role` - Change user role
- `POST /admin/events/{id}/deactivate` - Deactivate event

## Database Schema

### Users Table
- `user_id` (Primary Key)
- `name`
- `email`
- `password`
- `phone`
- `role` (user/organizer/admin)
- `enabled`

### Events Table
- `event_id` (Primary Key)
- `title`
- `description`
- `category`
- `location`
- `date_time`
- `end_date_time`
- `organizer_id` (Foreign Key)
- `max_participants`
- `requires_approval`
- `is_active`
- `created_at`

### RSVP Table
- `rsvp_id` (Primary Key)
- `event_id` (Foreign Key)
- `user_id` (Foreign Key)
- `status` (going/interested/not_going)
- `rsvp_date`
- `notes`

### Volunteers Table
- `volunteer_id` (Primary Key)
- `event_id` (Foreign Key)
- `user_id` (Foreign Key)
- `role_description`
- `status` (pending/approved/rejected)
- `registration_date`
- `notes`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please contact the development team or create an issue in the repository.

## Future Enhancements

- Mobile app integration
- Social media sharing
- Event rating and review system
- AI-based event recommendations
- Gamification with badges and rewards
- Real-time notifications
- Calendar integration
- Payment integration for paid events
