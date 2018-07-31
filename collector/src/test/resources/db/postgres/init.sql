DROP TABLE IF EXISTS domain_regex;
DROP TABLE IF EXISTS tracked_results;
DROP SEQUENCE domain_regex_id_seq;

CREATE SEQUENCE IF NOT EXISTS domain_regex_id_seq
  START 1000
  INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS domain_regex (
  id         BIGINT PRIMARY KEY      DEFAULT nextval('domain_regex_id_seq'),
  pattern    VARCHAR UNIQUE NOT NULL,
  date_added TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS tracked_domain_requests (
  date       DATE        NOT NULL,
  pattern    VARCHAR     NOT NULL,
  address    VARCHAR(15) NOT NULL,
  login      VARCHAR     NOT NULL,
  first_time TIME        NOT NULL,
  last_time  TIME        NOT NULL,
  count      BIGINT      NOT NULL,
  CONSTRAINT unique_track UNIQUE (date, pattern, address, login)
);

CREATE TABLE IF NOT EXISTS click_count (
  date  DATE   NOT NULL UNIQUE,
  count BIGINT NOT NULL
);