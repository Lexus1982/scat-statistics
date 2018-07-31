DROP TABLE IF EXISTS domain_regex;
DROP TABLE IF EXISTS tracked_results;
DROP SEQUENCE domain_regex_id_seq;

CREATE SEQUENCE IF NOT EXISTS domain_regex_id_seq
  START 1000
  INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS domain_regex (
  id            BIGINT PRIMARY KEY      DEFAULT nextval('domain_regex_id_seq'),
  regex_pattern VARCHAR UNIQUE NOT NULL,
  date_added    TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tracked_results (
  regex_pattern VARCHAR,
  address       CHAR(15),
  login         VARCHAR,
  first_time    TIMESTAMP,
  last_time     TIMESTAMP,
  count         BIGINT,
  CONSTRAINT unique_track UNIQUE (regex_pattern, address, login)
);