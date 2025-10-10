
## Quick Start

### 1. Start the Application
```powershell
cd c:\Users\Admin\Downloads\EventSphere\EventSphere
mvn spring-boot:run
```

The application will start on: **http://localhost:8080**

---

## Test Scenarios

### Scenario 1: Test Organizer Event Creation ✅

**Steps:**
1. Open browser and go to `http://localhost:8080`
2. Click "Become Organizer" or navigate to `/register-organizer`
3. Register a new organizer account:
   - Username: `testorganizer`
   - Email: `organizer@test.com`
   - Password: `password123`
   - Full Name: `Test Organizer`
4. After registration, login with the credentials
5. Click "Create Event" in the navigation bar
6. Fill in the event form:
   - **Title:** "Hack with Ongola"
   - **Category:** HACKATHON
   - **Description:** "A fast-paced hackathon bringing students, developers, and entrepreneurs together"
   - **Location:** "Ongola Tech Hub"
   - **Date & Time:** Select a future date/time
   - **End Time:** (Optional) Select end date/time
   - **Max Participants:** 100
   - **Require Approval:** Check or uncheck as desired
7. Click "Create Event"

**Expected Result:**
- ✅ Event is created successfully
- ✅ Redirects to event details page
- ✅ Success message displayed: "Event created successfully!"
- ✅ Event details are displayed correctly

**If Error Occurs:**
- ❌ Error message will be displayed at the top of the create event page
- ❌ Check the error message for details
- ❌ Verify all required fields are filled

---

### Scenario 2: Test User Logout ✅

**Steps:**
1. Login as any user (USER, ORGANIZER, or ADMIN)
2. Verify you see your username in the top-right navigation bar
3. Click on the username dropdown
4. Click "Logout" button

**Expected Result:**
- ✅ Page redirects to home page (`/`)
- ✅ User is logged out
- ✅ Navigation bar shows "Login" and "Register" buttons
- ✅ No user dropdown visible
- ✅ Session is completely cleared
- ✅ Clicking browser back button doesn't restore session

**If Logout Fails:**
- ❌ Button shows "pending" - indicates CSRF token issue
- ❌ Page doesn't redirect - check browser console for errors
- ❌ User still logged in - check SecurityConfig

### Scenario 3: Test Organizer Logout ✅

**Steps:**
1. Login as organizer (from Scenario 1)
2. Navigate to any page (home, create event, event details)
3. Click on username dropdown in navigation
4. Click "Logout"

**Expected Result:**
- ✅ Same as Scenario 2
- ✅ Organizer is completely logged out
- ✅ Cannot access "Create Event" page without logging in again

---

### Scenario 4: Test Event Creation Validation

**Steps:**
1. Login as organizer
2. Go to "Create Event" page
3. Try to submit form with missing required fields

**Expected Result:**
- ✅ Browser validation prevents submission
- ✅ Required fields are highlighted
- ✅ User must fill all required fields before submission

---

### Scenario 5: Test CSRF Protection ✅

**Steps:**
1. Login as organizer
2. Open browser developer tools (F12)
3. Go to "Create Event" page
4. Inspect the form HTML
5. Look for hidden CSRF token input

**Expected Result:**
- ✅ Form contains hidden input: `<input type="hidden" name="_csrf" value="..."/>`
- ✅ CSRF token is automatically included by Thymeleaf
- ✅ Form submission includes CSRF token

---

## Database Verification

### Check Created Events

**MySQL Query:**
```sql
USE eventsphere;

-- View all events
SELECT event_id, title, category, location, date_time, requires_approval, is_active
FROM events
ORDER BY created_at DESC;

-- View events by organizer
SELECT e.title, e.category, u.username as organizer
FROM events e
JOIN users u ON e.organizer_id = u.user_id
ORDER BY e.created_at DESC;
```

### Check User Sessions

**After Logout:**
```sql
-- Check if user sessions are cleared
-- (Spring Security manages sessions in memory by default)
-- Verify by trying to access protected pages after logout
```

---

## Common Issues and Solutions

### Issue 1: Event Creation Shows Error
**Possible Causes:**
- Missing required fields
- Invalid date/time format
- Database connection issue
- Organizer not properly authenticated

**Solution:**
1. Check error message displayed on page
2. Verify all required fields are filled
3. Check application logs for detailed error
4. Verify database connection in `application.properties`

### Issue 2: Logout Button Not Working
**Possible Causes:**
- CSRF token missing
- JavaScript error
- Form not submitting

**Solution:**
1. Check browser console for JavaScript errors
2. Verify CSRF token is present in form
3. Check network tab to see if POST request is sent
4. Verify SecurityConfig has proper logout configuration

### Issue 3: "Access Denied" After Logout
**Possible Causes:**
- Session not properly cleared
- Browser cache issue

**Solution:**
1. Clear browser cache and cookies
2. Close and reopen browser
3. Verify `clearAuthentication(true)` is in SecurityConfig
4. Check that `deleteCookies("JSESSIONID")` is configured

---

## Browser Console Checks

### Check for JavaScript Errors
1. Open Developer Tools (F12)
2. Go to Console tab
3. Look for any red error messages
4. Common errors to watch for:
   - CSRF token missing
   - Form validation errors
   - Network request failures

### Check Network Requests
1. Open Developer Tools (F12)
2. Go to Network tab
3. Perform action (create event, logout)
4. Check the request:
   - **Status Code:** Should be 200 or 302 (redirect)
   - **Request Method:** Should be POST
   - **Form Data:** Should include CSRF token
   - **Response:** Check for errors

---

## Application Logs

### View Logs While Running
The application logs will show in the terminal where you ran `mvn spring-boot:run`

**Look for:**
- ✅ "Started EventSphereApplication" - Application started successfully
- ✅ "HikariPool-1 - Start completed" - Database connection successful
- ❌ Any ERROR or WARN messages
- ❌ Stack traces indicating exceptions

### Enable Debug Logging (if needed)
Add to `application.properties`:
```properties
logging.level.com.example.EventSphere=DEBUG
logging.level.org.springframework.security=DEBUG
```

---

## Test Data Setup

### Create Test Users

**Regular User:**
```
Username: testuser
Email: user@test.com
Password: password123
Role: USER
```

**Organizer:**
```
Username: testorganizer
Email: organizer@test.com
Password: password123
Role: ORGANIZER
```

**Admin:**
```
Username: admin
Email: admin@test.com
Password: admin123
Role: ADMIN
```

### Create Test Events

Use the "Create Event" form with various data:

**Event 1:**
- Title: "Spring Boot Workshop"
- Category: WORKSHOP
- Location: "Tech Hub"
- Date: Tomorrow
- Max Participants: 50

**Event 2:**
- Title: "Community Meetup"
- Category: MEETUP
- Location: "Coffee Shop"
- Date: Next week
- Requires Approval: Yes

---

## Performance Testing

### Test Concurrent Users
1. Open multiple browser windows/tabs
2. Login with different users in each
3. Perform actions simultaneously
4. Verify no session conflicts

### Test Form Submission Speed
1. Fill out event creation form
2. Submit and measure response time
3. Should redirect within 1-2 seconds

---

## Security Testing

### Test CSRF Protection
1. Try to submit form without CSRF token (using curl or Postman)
2. Should receive 403 Forbidden error

**Example:**
```bash
curl -X POST http://localhost:8080/events/create \
  -d "title=Test&category=WORKSHOP&description=Test&location=Test&dateTime=2025-10-10T10:00"
```
**Expected:** 403 Forbidden (CSRF token missing)

### Test Authentication
1. Try to access `/events/create` without logging in
2. Should redirect to login page

### Test Authorization
1. Login as regular USER
2. Try to access `/events/create`
3. Should be denied (only ORGANIZER and ADMIN can create events)

---

## Regression Testing

After fixes, verify these still work:

- ✅ User registration
- ✅ User login
- ✅ Organizer registration
- ✅ Admin dashboard access
- ✅ Event browsing
- ✅ RSVP to events
- ✅ Volunteer registration
- ✅ User profile viewing/editing

---

## Success Criteria

All tests pass when:

1. ✅ Organizers can create events without errors
2. ✅ All users can logout successfully
3. ✅ Sessions are properly cleared after logout
4. ✅ CSRF protection is working
5. ✅ Error messages are displayed when needed
6. ✅ Success messages are displayed after successful actions
7. ✅ No JavaScript errors in browser console
8. ✅ No exceptions in application logs
9. ✅ Database records are created correctly
10. ✅ All existing functionality still works

---

## Reporting Issues

If you find any issues during testing, please report:

1. **What you were doing** (steps to reproduce)
2. **What you expected to happen**
3. **What actually happened**
4. **Error messages** (from UI and logs)
5. **Browser and version** (Chrome, Firefox, etc.)
6. **Screenshots** (if applicable)

---

## Next Steps After Testing

Once all tests pass:

1. ✅ Deploy to staging environment
2. ✅ Perform user acceptance testing
3. ✅ Create backup of database
4. ✅ Deploy to production
5. ✅ Monitor logs for any issues
6. ✅ Gather user feedback

---

## Contact

For questions or issues during testing, refer to:
- `LOGOUT_AND_EVENT_CREATION_FIXES.md` - Detailed fix documentation
- `FIXES_SUMMARY.md` - Previous fixes documentation
- Application logs in terminal