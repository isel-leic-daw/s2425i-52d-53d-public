BEGIN;

-- Insert into dbo.users and get the generated ID
WITH inserted_user AS (
INSERT INTO dbo.users (name, email, password_validation)
VALUES ('Paul Atreides', 'paul@atreides.com', 'muadib')
    RETURNING id
    )

-- Use the returned user ID to insert into dbo.events
INSERT INTO dbo.events (title, description, organizer_id, selection_type)
SELECT
    'Arrakis Sandstorm Meeting',
    'Discuss plans for the Fremen alliance',
    id,
    'SINGLE'
FROM inserted_user;

COMMIT;
