-- Create the schema dbo
CREATE SCHEMA IF NOT EXISTS dbo;

-- Create table for users in the dbo schema
CREATE TABLE dbo.users
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255)        NOT NULL,
    email               VARCHAR(255) UNIQUE NOT NULL,
    password_validation VARCHAR(255)        NOT NULL
);

create table dbo.Tokens
(
    token_validation VARCHAR(256) primary key,
    user_id          int references dbo.Users (id),
    created_at       bigint not null,
    last_used_at     bigint not null
);

-- Create table for events in the dbo schema
CREATE TABLE dbo.events
(
    id             SERIAL PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    organizer_id   INT          NOT NULL,
    selection_type VARCHAR(10)  NOT NULL CHECK (selection_type IN ('SINGLE', 'MULTIPLE')),
    FOREIGN KEY (organizer_id) REFERENCES dbo.users (id)
);

-- Create table for time slots in the dbo schema
CREATE TABLE dbo.time_slots
(
    id                  SERIAL PRIMARY KEY,
    start_time          TIMESTAMP NOT NULL,
    duration_in_minutes INT       NOT NULL,
    event_id            INT       NOT NULL,
    FOREIGN KEY (event_id) REFERENCES dbo.events (id)
);

-- Create table for single time slots in the dbo schema
CREATE TABLE dbo.time_slot_singles
(
    time_slot_id INT PRIMARY KEY,
    owner_id     INT,
    FOREIGN KEY (time_slot_id) REFERENCES dbo.time_slots (id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES dbo.users (id) ON DELETE SET NULL
);

-- Create table for multiple time slots in the dbo schema
CREATE TABLE dbo.time_slot_multiples
(
    time_slot_id INT PRIMARY KEY,
    FOREIGN KEY (time_slot_id) REFERENCES dbo.time_slots (id) ON DELETE CASCADE
);

-- Create table for participants (for TimeSlotMultiple) in the dbo schema
CREATE TABLE dbo.participants
(
    id                    SERIAL PRIMARY KEY,
    user_id               INT NOT NULL,
    time_slot_multiple_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES dbo.users (id),
    FOREIGN KEY (time_slot_multiple_id) REFERENCES dbo.time_slot_multiples (time_slot_id)
);
