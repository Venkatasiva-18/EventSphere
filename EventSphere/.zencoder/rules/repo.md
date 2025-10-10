# EventSphere Repository Rules

## Primary Tech Stack
- **Backend**: Spring Boot (Java)
- **Frontend**: Thymeleaf templates, static assets
- **Database**: MySQL

## Guidelines for Contributions
1. Adhere to Spring conventions for controllers, services, and repositories.
2. Keep Thymeleaf templates aligned with backend endpoints to avoid broken links.
3. Ensure configuration files (e.g., `application.properties`) contain valid local setup details for MySQL and security.
4. Run tests before finalizing changes.

## Context for Admin Dashboard Work
- Admin dashboard manages event states (active, inactive).
- Approval workflows have been retired; avoid reintroducing them.
- Pending counts derive from events requiring approval but without direct approval actions.
- Confirm template buttons map to actual controller endpoints.

## Testing Expectations
- Verify the app boots without Whitelabel errors by hitting admin routes.
- Validate event activation/deactivation functionality end-to-end.