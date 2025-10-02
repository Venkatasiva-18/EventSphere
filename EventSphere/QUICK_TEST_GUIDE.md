# Quick Test Guide - EventSphere Fixes

## 🚀 Quick Start

```bash
# Start the application
mvn spring-boot:run
```

Then open: http://localhost:8080

---

## ✅ Test Checklist

### 1. Event Creation Test (2 minutes)

**Steps:**
1. Login as organizer (create one if needed)
2. Click "Create Event" in navigation
3. Fill form:
   - Title: "Test Event"
   - Description: "Testing event creation"
   - Category: WORKSHOP
   - Location: "Test Location"
   - Date/Time: Tomorrow at 10:00 AM
   - Max Participants: 50
4. Click "Create Event"

**Expected:**
- ✅ Success message appears
- ✅ Redirected to event details page
- ✅ All event information displays correctly
- ✅ Shows "0 / 50 participants"
- ✅ No error messages

**If Failed:**
- ❌ Check console for errors
- ❌ Verify database connection
- ❌ Check application logs

---

### 2. Browse Events Test (1 minute)

**Steps:**
1. Click "Events" in navigation
2. Observe the events list

**Expected:**
- ✅ All events display in cards
- ✅ Each card shows participant count
- ✅ No error messages
- ✅ "View Details" buttons work

**If Failed:**
- ❌ Check if events exist in database
- ❌ Check browser console for errors

---

### 3. View Event Details Test (1 minute)

**Steps:**
1. From home page or events page
2. Click "View Details" on any event

**Expected:**
- ✅ Event details page loads
- ✅ Shows event title, description, category
- ✅ Shows date, time, location
- ✅ Shows participant count
- ✅ Shows organizer information
- ✅ No LazyInitializationException error

**If Failed:**
- ❌ Check application logs for LazyInitializationException
- ❌ Verify EventController uses findByIdWithDetails()

---

### 4. Logout Test (30 seconds)

**Steps:**
1. Login as any user
2. Click user dropdown (top right)
3. Click "Logout"
4. Observe the behavior

**Expected:**
- ✅ Logout completes in 1-2 seconds
- ✅ Redirected to home page
- ✅ Navigation shows "Login" and "Register"
- ✅ User dropdown disappears
- ✅ No "pending" state on button

**If Failed:**
- ❌ Check browser console for JavaScript errors
- ❌ Clear browser cache and try again
- ❌ Try in incognito/private mode

---

### 5. RSVP Test (1 minute)

**Steps:**
1. Login as regular user
2. Navigate to any event
3. Click "I'm Going"
4. Refresh the page

**Expected:**
- ✅ Success message appears
- ✅ Your RSVP status shows as "GOING"
- ✅ Participant count increases by 1
- ✅ Page loads without errors

**If Failed:**
- ❌ Check if user is authenticated
- ❌ Verify RSVP service is working

---

## 🐛 Common Issues & Quick Fixes

### Issue: "LazyInitializationException"

**Quick Fix:**
```bash
# Verify the fix is applied
grep -r "findByIdWithDetails" src/main/java/com/example/EventSphere/controller/EventController.java
```

Should show: `eventService.findByIdWithDetails(eventId)`

---

### Issue: Logout Button Not Working

**Quick Fix:**
1. Clear browser cache (Ctrl+Shift+Delete)
2. Hard refresh (Ctrl+F5)
3. Try in incognito mode

**Check JavaScript:**
```bash
# Verify the fix is in main.js
grep -A 5 "Skip logout forms" src/main/resources/static/js/main.js
```

---

### Issue: Events Not Showing

**Quick Fix:**
```sql
-- Check if events exist
SELECT * FROM events WHERE is_active = 1;

-- Check if events are in the future
SELECT * FROM events WHERE is_active = 1 AND date_time > NOW();
```

---

### Issue: Participant Count Wrong

**Quick Fix:**
```sql
-- Check RSVPs
SELECT event_id, status, COUNT(*) 
FROM rsvps 
GROUP BY event_id, status;
```

Only RSVPs with status = 'GOING' count as participants.

---

## 📊 Performance Check

### Check Database Queries

Add to `application.properties`:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
```

**Expected for Event Details:**
- ✅ 1 query with JOIN FETCH
- ❌ NOT multiple SELECT queries

**Example Good Query:**
```sql
SELECT e.*, o.*, r.*, v.* 
FROM events e 
LEFT JOIN users o ON e.organizer_id = o.user_id
LEFT JOIN rsvps r ON e.event_id = r.event_id
LEFT JOIN volunteers v ON e.event_id = v.event_id
WHERE e.event_id = ?
```

---

## 🔍 Debug Mode

### Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.com.example.EventSphere=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate=DEBUG
```

### Check Application Logs

```bash
# Watch logs in real-time
tail -f logs/spring.log

# Or check console output when running with mvn
mvn spring-boot:run
```

---

## 🎯 Success Criteria

All tests should pass with these results:

| Test | Status | Time |
|------|--------|------|
| Event Creation | ✅ PASS | < 3s |
| Browse Events | ✅ PASS | < 2s |
| View Details | ✅ PASS | < 2s |
| Logout | ✅ PASS | < 2s |
| RSVP | ✅ PASS | < 2s |

**Total Test Time:** ~5 minutes

---

## 📝 Test Report Template

```
Date: _______________
Tester: _______________

Event Creation:        [ ] PASS  [ ] FAIL  Notes: _______________
Browse Events:         [ ] PASS  [ ] FAIL  Notes: _______________
View Event Details:    [ ] PASS  [ ] FAIL  Notes: _______________
Logout:                [ ] PASS  [ ] FAIL  Notes: _______________
RSVP:                  [ ] PASS  [ ] FAIL  Notes: _______________

Overall Status:        [ ] ALL PASS  [ ] SOME FAILED

Issues Found:
1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

Additional Notes:
_______________________________________________
_______________________________________________
```

---

## 🆘 Emergency Rollback

If something goes wrong, rollback to previous version:

```bash
# Revert changes
git checkout HEAD~1

# Or restore specific files
git checkout HEAD~1 -- src/main/java/com/example/EventSphere/repository/EventRepository.java
git checkout HEAD~1 -- src/main/java/com/example/EventSphere/service/EventService.java
git checkout HEAD~1 -- src/main/java/com/example/EventSphere/controller/EventController.java
git checkout HEAD~1 -- src/main/java/com/example/EventSphere/config/SecurityConfig.java
git checkout HEAD~1 -- src/main/resources/static/js/main.js

# Rebuild
mvn clean install
```

---

## 📞 Support

If issues persist:

1. Check `LAZY_LOADING_AND_LOGOUT_FIXES.md` for detailed troubleshooting
2. Review application logs
3. Check database connection
4. Verify all dependencies are installed
5. Try restarting the application

---

**Last Updated:** October 2, 2025  
**Version:** 1.0  
**Status:** ✅ Ready for Testing