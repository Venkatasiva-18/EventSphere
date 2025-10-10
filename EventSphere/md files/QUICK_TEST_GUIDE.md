# Quick Test Guide - Admin Dashboard Fixes

## 🚀 Quick Start

1. **Start the application** (if not already running)
2. **Navigate to:** `http://localhost:8080/admin/login`
3. **Login with admin credentials**

---

## ✅ Test Checklist

### 1. Navigation Bar (Test on ALL admin pages)

| Test | Expected Result | Status |
|------|----------------|--------|
| Click "EventSphere Admin" logo | Redirects to `/admin/dashboard` | ⬜ |
| Click "Dashboard" link | Goes to `/admin/dashboard` | ⬜ |
| Click "Users" link | Goes to `/admin/users` | ⬜ |
| Click "Events" link | Goes to `/admin/events` | ⬜ |
| Click "Reports" link | Goes to `/admin/reports` | ⬜ |
| Click "Public Site" link | Redirects to home page `/` | ⬜ |
| Click profile dropdown → "Profile" | Redirects to `/user/profile` | ⬜ |
| Click profile dropdown → "Logout" | Logs out, redirects to login | ⬜ |

**No white label errors should occur!**

---

### 2. User Management (`/admin/users`)

| Test | Expected Result | Status |
|------|----------------|--------|
| Click "Enable" button (green check) | User becomes active, success message | ⬜ |
| Click "Disable" button (yellow ban) | User becomes disabled, success message | ⬜ |
| Click role dropdown → "Make User" | User role changes to USER, success message | ⬜ |
| Click role dropdown → "Make Organizer" | User role changes to ORGANIZER, success message | ⬜ |
| Click role dropdown → "Make Admin" | User role changes to ADMIN, success message | ⬜ |
| Click "Delete" button (red trash) | Confirmation dialog appears | ⬜ |
| Confirm delete | User deleted, success message | ⬜ |

**No loading forever or white label errors!**

---

### 3. Event Management (`/admin/events`)

| Test | Expected Result | Status |
|------|----------------|--------|
| Click "Active" filter | Shows only active events | ⬜ |
| Click "Upcoming" filter | Shows only upcoming events | ⬜ |
| Click "Pending" filter | Shows only pending events | ⬜ |
| Click "Inactive" filter | Shows only inactive events | ⬜ |
| Click "View" button (eye icon) | Opens event details page | ⬜ |
| Click "Edit" button (pencil icon) | Opens event edit page | ⬜ |
| Click "Deactivate" button (ban icon) | Confirmation dialog, then deactivates | ⬜ |
| Click "Activate" button (undo icon) | Confirmation dialog, then activates | ⬜ |
| Click "Delete" button (red trash) | Confirmation dialog appears | ⬜ |
| Confirm delete | Event deleted, success message | ⬜ |

**No loading forever or white label errors!**

---

## 🐛 If Something Doesn't Work

### Check Browser Console
1. Press `F12` to open Developer Tools
2. Go to "Console" tab
3. Look for red error messages
4. Take a screenshot and report

### Check Network Tab
1. Press `F12` to open Developer Tools
2. Go to "Network" tab
3. Click the button that's not working
4. Look for red/failed requests
5. Click on the failed request to see details

### Common Issues

| Issue | Possible Cause | Solution |
|-------|---------------|----------|
| 403 Forbidden | CSRF token missing | Check if forms have CSRF token |
| 404 Not Found | Endpoint doesn't exist | Check controller has the endpoint |
| White Label Error | Controller exception | Check application logs |
| Infinite loading | JavaScript error | Check browser console |
| Nothing happens | Button not submitting | Check form action URL |

---

## 📝 Test Results Template

Copy this and fill it out:

```
## Test Results - [Your Name] - [Date]

### Environment
- Browser: [Chrome/Firefox/Safari/Edge]
- Browser Version: [e.g., 120.0.6099.130]
- OS: [Windows/Mac/Linux]

### Navigation Tests
- EventSphere Admin logo: ✅/❌
- Dashboard link: ✅/❌
- Users link: ✅/❌
- Events link: ✅/❌
- Reports link: ✅/❌
- Public Site link: ✅/❌
- Profile link: ✅/❌
- Logout: ✅/❌

### User Management Tests
- Enable user: ✅/❌
- Disable user: ✅/❌
- Change role to USER: ✅/❌
- Change role to ORGANIZER: ✅/❌
- Change role to ADMIN: ✅/❌
- Delete user: ✅/❌

### Event Management Tests
- Active filter: ✅/❌
- Upcoming filter: ✅/❌
- Pending filter: ✅/❌
- Inactive filter: ✅/❌
- View event: ✅/❌
- Edit event: ✅/❌
- Deactivate event: ✅/❌
- Activate event: ✅/❌
- Delete event: ✅/❌

### Issues Found
[List any issues here]

### Screenshots
[Attach screenshots if any issues]
```

---

## 🎯 Priority Tests

If you have limited time, test these first:

1. **Public Site link** (was broken before)
2. **EventSphere Admin logo** (was broken before)
3. **User enable/disable buttons** (were broken before)
4. **Event activate/deactivate buttons** (were broken before)
5. **Pending events filter** (was broken before)

---

## ✨ Success Criteria

All tests should:
- ✅ Complete without errors
- ✅ Show appropriate success/error messages
- ✅ Redirect to correct pages
- ✅ Update data correctly
- ✅ No white label errors
- ✅ No infinite loading
- ✅ No console errors

---

## 📞 Report Issues

If you find any issues, please report:
1. What you were trying to do
2. What you expected to happen
3. What actually happened
4. Browser console errors (if any)
5. Screenshots (if possible)

---

**Happy Testing! 🎉**
