CREATE TABLE IF NOT EXISTS board_entity
(
    id uuid PRIMARY KEY,
    name varchar(64) NOT NULL,
    project_id uuid not null,
    position_weight integer NOT NULL
);
