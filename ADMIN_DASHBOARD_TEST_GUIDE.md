# Admin Dashboard Testing Guide

## Prerequisites
- Application is running on `http://localhost:8080`
- You have admin credentials (email and password)
- Database is properly configured and running

## Test Scenarios

### 1. Admin Login Test
**URL**: `http://localhost:8080/admin/login`

**Steps**:
1. Navigate to the admin login page
2. Enter admin email and password
3. Click "Sign In"

**Expected Result**:
- Successful login redirects to `/admin/dashboard`
- No white label error
- Dashboard displays with statistics

**Possible Issues**:
- If login fails, verify admin user exists in database with ROLE_ADMIN
- Check that password is correctly encoded

---

### 2. Dashboard Functionality Test
**URL**: `http://localhost:8080/admin/dashboard`

**Steps**:
1. After login, verify you're on the dashboard
2. Check all statistics cards display numbers:
   - Total Users
   - Total Events
   - Pending Events
   - Active Events
3. Verify "Recent Events" section shows events (if any exist)
4. Verify "Recent Users" section shows users (if any exist)
5. Click each quick action button:
   - Manage Users
   - Manage Events
   - Create Event
   - Pending Events

**Expected Result**:
- All statistics display correctly
- All quick action buttons navigate to correct pages
- No white label errors

---

### 3. Navigation Bar Test
**Test on**: All admin pages

**Steps**:
1. Click "Dashboard" link - should go to `/admin/dashboard`
2. Click "Users" link - should go to `/admin/users`
3. Click "Events" link - should go to `/admin/events`
4. Click "Reports" link - should go to `/admin/reports`
5. Click "Public Site" link - should go to `/` (home page)
6. Click profile dropdown and select "Profile" - should go to `/user/profile`
7. Click profile dropdown and select "Logout" - should logout and redirect to `/admin/login?logout=true`

**Expected Result**:
- All navigation links work correctly
- No white label errors
- Active page is highlighted in navigation
- Logout works and shows success message

---

### 4. User Management Test
**URL**: `http://localhost:8080/admin/users`

**Steps**:
1. Navigate to Users page
2. Verify user list displays with columns:
   - Name
   - Email
   - Phone
   - Role
   - Status
   - Actions
3. Test Enable/Disable functionality:
   - Click disable button on an enabled user
   - Verify success message appears
   - Verify user status changes to "Disabled"
   - Click enable button
   - Verify user status changes back to "Active"
4. Test Role Change functionality:
   - Click role dropdown button
   - Select a different role (e.g., "Make Organizer")
   - Verify success message appears
   - Verify role badge updates
5. Test Delete functionality:
   - Click delete button (red trash icon)
   - Verify confirmation dialog appears
   - Click OK to confirm
   - Verify success message appears
   - Verify user is removed from list

**Expected Result**:
- All user management operations work correctly
- Flash messages display for each action
- Page refreshes with updated data
- No white label errors

---

### 5. Event Management Test
**URL**: `http://localhost:8080/admin/events`

**Steps**:
1. Navigate to Events page
2. Verify event list displays with columns:
   - Event (title and description)
   - Category
   - Date & Time
   - Location
   - Organizer
   - Participants
   - Status
   - Actions
3. Test Filter functionality:
   - Click "Active" filter - shows active events
   - Click "Upcoming" filter - shows upcoming events
   - Click "Pending" filter - shows pending events
   - Click "Inactive" filter - shows inactive events
   - Verify badge counts update
4. Test View functionality:
   - Click eye icon to view event details
   - Verify redirects to event details page
5. Test Edit functionality:
   - Click edit icon
   - Verify redirects to edit event page
6. Test Deactivate functionality:
   - Click deactivate button (ban icon) on active event
   - Verify confirmation dialog appears
   - Click OK to confirm
   - Verify success message appears
   - Verify event status changes to "Inactive"
7. Test Activate functionality:
   - Click activate button (undo icon) on inactive event
   - Verify confirmation dialog appears
   - Click OK to confirm
   - Verify success message appears
   - Verify event status changes to "Active"
8. Test Delete functionality:
   - Click delete button (red trash icon)
   - Verify confirmation dialog appears
   - Click OK to confirm
   - Verify success message appears
   - Verify event is removed from list

**Expected Result**:
- All event management operations work correctly
- Filters work and show correct events
- Flash messages display for each action
- Page refreshes with updated data
- No white label errors

---

### 6. Reports Page Test
**URL**: `http://localhost:8080/admin/reports`

**Steps**:
1. Navigate to Reports page
2. Verify "User Statistics" section displays:
   - Total Users
   - Organizers
   - Administrators
3. Verify "Event Statistics" section displays:
   - Total Events
   - Active Events
   - Upcoming Events
4. Verify "Platform Overview" table displays all metrics
5. Test Quick Actions buttons:
   - View All Users
   - View All Events
   - Pending Events
   - Dashboard

**Expected Result**:
- All statistics display correctly
- Numbers match actual database counts
- Quick action buttons navigate correctly
- No white label errors

---

### 7. Profile Access Test
**URL**: `http://localhost:8080/user/profile`

**Steps**:
1. From any admin page, click profile dropdown
2. Click "Profile"
3. Verify profile page loads
4. Verify admin user information is displayed
5. Test profile update functionality

**Expected Result**:
- Profile page loads correctly
- Admin can view and update their profile
- No white label errors

---

### 8. Logout Test
**Test on**: Any admin page

**Steps**:
1. Click profile dropdown in navigation
2. Click "Logout" button
3. Verify logout confirmation

**Expected Result**:
- User is logged out
- Redirected to `/admin/login?logout=true`
- Success message "You have been logged out successfully" appears
- Cannot access admin pages without logging in again

---

## Common Issues and Troubleshooting

### Issue: White Label Error on Any Page
**Possible Causes**:
1. Controller endpoint missing
2. Template file missing or has wrong name
3. Typo in URL mapping

**Solution**:
1. Check browser URL and compare with controller mapping
2. Verify template file exists in correct location
3. Check application logs for errors

### Issue: Statistics Not Displaying
**Possible Causes**:
1. Database connection issue
2. No data in database
3. Model attributes not being passed

**Solution**:
1. Check database connection
2. Add test data to database
3. Check controller method adds attributes to Model

### Issue: Flash Messages Not Showing
**Possible Causes**:
1. RedirectAttributes not being used
2. Template not checking for flash attributes

**Solution**:
1. Verify controller uses RedirectAttributes
2. Check template has th:if="${success}" and th:if="${error}" blocks

### Issue: Buttons Not Working
**Possible Causes**:
1. JavaScript not loaded
2. Bootstrap not loaded
3. Form action URL incorrect

**Solution**:
1. Check browser console for JavaScript errors
2. Verify Bootstrap JS is loaded
3. Check form action URL matches controller endpoint

### Issue: Confirmation Dialogs Not Appearing
**Possible Causes**:
1. JavaScript not loaded
2. onclick attribute missing

**Solution**:
1. Check browser console for errors
2. Verify button has onclick="return confirm('...')"

---

## Test Data Setup

### Create Admin User (if not exists)
```sql
INSERT INTO users (name, email, password, phone, role, enabled, created_at)
VALUES ('Admin User', 'admin@eventsphere.com', '$2a$10$encoded_password_here', '1234567890', 'ADMIN', true, NOW());
```

### Create Test Users
```sql
INSERT INTO users (name, email, password, phone, role, enabled, created_at)
VALUES 
('John Doe', 'john@example.com', '$2a$10$encoded_password', '1234567890', 'USER', true, NOW()),
('Jane Organizer', 'jane@example.com', '$2a$10$encoded_password', '0987654321', 'ORGANIZER', true, NOW());
```

### Create Test Events
```sql
INSERT INTO events (title, description, category, location, date_time, end_date_time, max_participants, active, requires_approval, created_at, organizer_id)
VALUES 
('Test Event 1', 'Description 1', 'MUSIC', 'Location 1', '2025-12-01 18:00:00', '2025-12-01 22:00:00', 100, true, false, NOW(), 2),
('Test Event 2', 'Description 2', 'SPORTS', 'Location 2', '2025-12-15 10:00:00', '2025-12-15 14:00:00', 50, true, false, NOW(), 2);
```

---

## Success Criteria

✅ All navigation links work without white label errors
✅ Dashboard displays all statistics correctly
✅ User management operations (enable/disable/role change/delete) work
✅ Event management operations (activate/deactivate/delete) work
✅ Reports page displays all statistics
✅ Logout functionality works
✅ Profile access works
✅ Flash messages display for all operations
✅ Confirmation dialogs appear for delete operations
✅ Filter buttons work on events page

---

## Browser Testing

Test the admin dashboard on:
- ✅ Chrome/Edge (Chromium-based)
- ✅ Firefox
- ✅ Safari (if on Mac)

Verify:
- All pages render correctly
- All buttons and links work
- No console errors
- Responsive design works on mobile view

---

## Performance Testing

1. **Load Time**: Dashboard should load within 2 seconds
2. **User List**: Should handle 100+ users without performance issues
3. **Event List**: Should handle 100+ events without performance issues
4. **Filter Operations**: Should be instant (client-side or fast server-side)

---

## Security Testing

1. **Access Control**: Try accessing admin pages without login - should redirect to login
2. **Role Check**: Try accessing admin pages with non-admin user - should be denied
3. **CSRF Protection**: All forms should include CSRF token
4. **Session Management**: Logout should invalidate session

---

## Final Checklist

Before marking as complete, verify:

- [ ] No white label errors on any admin page
- [ ] All navigation links work
- [ ] All CRUD operations work (Create, Read, Update, Delete)
- [ ] Flash messages display correctly
- [ ] Confirmation dialogs work
- [ ] Statistics are accurate
- [ ] Logout works properly
- [ ] Security is properly configured
- [ ] No console errors in browser
- [ ] Responsive design works
- [ ] All test scenarios pass

---

## Support

If you encounter any issues not covered in this guide:
1. Check application logs for errors
2. Check browser console for JavaScript errors
3. Verify database connection and data
4. Review the ADMIN_DASHBOARD_FIXES.md document
5. Check Spring Security configuration

---

**Document Version**: 1.0
**Last Updated**: 2025-10-07
**Status**: Ready for Testing