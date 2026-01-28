# E-Hailing Backend API Documentation

## Base URL
`http://localhost:8080/api/v1`

## Swagger UI
Interactive documentation is available at:
`http://localhost:8080/swagger-ui/index.html`

## Authentication

### Register
**POST** `/auth/register`
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "+263771234567",
  "role": "PASSENGER" // or "DRIVER"
}
```

### Login
**POST** `/auth/login`
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```
**Response:**
```json
{
  "token": "jwt_token_here"
}
```
*Note: Include this token in the `Authorization` header as `Bearer <token>` for all subsequent requests.*

---

## Ride Management

### Request a Ride (Passenger)
**POST** `/rides/request`
```json
{
  "pickupLat": -17.824858,
  "pickupLng": 31.053028,
  "destLat": -17.784858,
  "destLng": 31.103028
}
```

### Place a Bid (Driver)
**POST** `/rides/bid`
```json
{
  "rideRequestId": 1,
  "amount": 5.50
}
```

### Accept a Bid (Passenger)
**POST** `/rides/accept-bid`
```json
{
  "bidId": 1
}
```

### Start Ride (Driver)
**POST** `/rides/{rideId}/start`

### Complete Ride (Driver)
**POST** `/rides/{rideId}/complete`

---

## Driver Operations

### Update Location
**POST** `/driver/location`
```json
{
  "lat": -17.825,
  "lng": 31.054
}
```

---

## Ratings & History

### Submit Rating
**POST** `/ratings`
```json
{
  "rideId": 1,
  "score": 5,
  "comment": "Great driver!"
}
```

### Get User Average Rating
**GET** `/ratings/user/{userId}`

### Get Ride History
**GET** `/history`
*Returns list of past rides for the authenticated user.*

---

## Admin Dashboard

*Requires Role: ADMIN*

### Get Stats
**GET** `/admin/stats`
```json
{
  "totalUsers": 150,
  "totalDrivers": 25,
  "totalRides": 450,
  "totalRevenue": 1250.50
}
```

### Get Pending Drivers
**GET** `/admin/pending-drivers`
*Returns list of drivers waiting for approval.*

### Approve Driver
**POST** `/admin/approve-driver/{userId}`

### Reject Driver
**POST** `/admin/reject-driver/{userId}`

---

## Real-time Updates (WebSocket)

**Endpoint:** `/ws`
**Protocol:** STOMP

### For Drivers
**Subscribe:** `/user/queue/ride-requests`
**Payload:** `DriverRideNotification`
```json
{
  "rideRequestId": 1,
  "pickupLat": -17.824858,
  "pickupLng": 31.053028,
  "destLat": -17.784858,
  "destLng": 31.103028,
  "distance": 5.2,
  "suggestedPrice": 9.8,
  "surgeMultiplier": 1.0
}
```

### For Passengers (Coming Soon)
**Subscribe:** `/user/queue/ride-updates`
