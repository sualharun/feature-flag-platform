# API Specification

## Create Feature Flag
POST /flags

Request:
{
  "flagName": "new_checkout",
  "enabled": true,
  "rolloutPercentage": 10,
  "description": "New checkout flow"
}

Response: 201 Created

---

## Get Feature Flag
GET /flags/{flagName}

Response:
{
  "flagName": "new_checkout",
  "enabled": true,
  "rolloutPercentage": 10,
  "version": 3
}

---

## Update Feature Flag
PUT /flags/{flagName}

Request:
{
  "enabled": true,
  "rolloutPercentage": 50
}

---

## Delete Feature Flag
DELETE /flags/{flagName}

Response: 204 No Content




