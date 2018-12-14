CREATE SCHEMA IF NOT EXISTS reports;

CREATE SEQUENCE IF NOT EXISTS reports.domain_regex_id_seq
  START 1000
  INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS reports.domain_regex (
  id         BIGINT PRIMARY KEY      DEFAULT nextval('domain_regex_id_seq'),
  pattern    VARCHAR UNIQUE NOT NULL,
  date_added TIMESTAMP      NOT NULL DEFAULT now(),
  is_active  BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS reports.tracked_domain_requests (
  date       DATE        NOT NULL,
  domain_id  BIGINT REFERENCES domain_regex (id) ON DELETE CASCADE,
  address    VARCHAR(15) NOT NULL,
  login      VARCHAR     NOT NULL,
  first_time TIME        NOT NULL,
  last_time  TIME        NOT NULL,
  count      BIGINT      NOT NULL,
  CONSTRAINT unique_track UNIQUE (date, domain_id, address, login)
);

CREATE TABLE IF NOT EXISTS reports.click_count (
  date  DATE   NOT NULL UNIQUE,
  count BIGINT NOT NULL
);