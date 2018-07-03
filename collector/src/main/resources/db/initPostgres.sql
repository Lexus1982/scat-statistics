DROP TABLE IF EXISTS tracked_domains;
DROP TABLE IF EXISTS tracked_results;

CREATE TABLE tracked_domains (
  regex_pattern VARCHAR UNIQUE,
  is_active     BOOLEAN   NOT NULL DEFAULT TRUE,
  date_added    TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE tracked_results (
  regex_pattern VARCHAR,
  address       CHAR(15),
  login         VARCHAR,
  first_time    TIMESTAMP,
  last_time     TIMESTAMP,
  count         BIGINT,
  CONSTRAINT unique_track UNIQUE (regex_pattern, address, login)
);