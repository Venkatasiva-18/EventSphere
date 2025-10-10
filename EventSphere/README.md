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

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.6/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.6/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Thymeleaf](https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.6/reference/using/devtools.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.5.6/reference/web/spring-security.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.


