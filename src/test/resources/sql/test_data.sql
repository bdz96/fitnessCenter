-- test_data.sql

-- cleanup
TRUNCATE TABLE client_membershipdb, memberships, users RESTART IDENTITY CASCADE;

-- USERS
INSERT INTO users (first_name, last_name, date_of_birth, email, phone_number, password, user_type)
VALUES
 ('Login', 'Client', '1990-01-01', 'login.client@example.com', '111111111','$2a$10$a4m1GcikG5pVVB7tonCq2.GvAv6mK/vBP5PZwTHjil7Im1EV0FgDC', 1),
 ('Bexxx', 'Dizdarevic', '1996-05-27', 'berksLoginTest@gmail.com', '062008466','$2a$10$wWNMiE6gKy8J2FtepAwr/ObeaxRVIRSgS/5OpL4KqZGct8SXKsLhK', 1),
  ('Test', 'User', '1990-01-01', 'test.user@example.com', '123456789','$2a$10$dummyhashedpass', 1),
  ('Active', 'Client', '1990-01-01', 'active.client@example.com', '123456788','$2a$10$dummyhashedpass', 1),
  ('ZeroSessions', 'Client', '1990-01-01', 'zero.sessions@example.com', '123456787','$2a$10$dummyhashedpass', 1);

-- MEMBERSHIPS
INSERT INTO memberships (name, price, duration, time_unit, sessions_available)
VALUES
  ('Basic', 100, '1', 'MONTHS', 10),
  ('Standard', 150, '1', 'MONTHS', 10),
  ('ZeroPlan', 50, '1', 'MONTHS', 0);

-- CLIENT_MEMBERSHIPS
INSERT INTO client_membershipdb (client_id, membership_id, created_at, expires_at, sessions_remaining)
VALUES
  (2, 2, CURRENT_DATE, CURRENT_DATE + INTERVAL '30' DAY, 5),  -- Active membership
  (3, 3, CURRENT_DATE, CURRENT_DATE + INTERVAL '30' DAY, 0);  -- Zero sessions