CREATE SEQUENCE IF NOT EXISTS domain_regex_id_seq
  START 1000
  INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS domain_regex (
  id         BIGINT PRIMARY KEY      DEFAULT nextval('domain_regex_id_seq'),
  pattern    VARCHAR UNIQUE NOT NULL,
  date_added TIMESTAMP      NOT NULL DEFAULT now(),
  is_active  BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tracked_domain_requests (
  date       DATE        NOT NULL,
  domain_id  BIGINT REFERENCES domain_regex (id),
  address    VARCHAR(15) NOT NULL,
  login      VARCHAR     NOT NULL,
  first_time TIME        NOT NULL,
  last_time  TIME        NOT NULL,
  count      BIGINT      NOT NULL,
  CONSTRAINT unique_track UNIQUE (date, domain_id, address, login)
);

CREATE INDEX IF NOT EXISTS tracked_domain_requests_date_pattern_idx
  ON tracked_domain_requests (date, domain_id);

CREATE TABLE IF NOT EXISTS click_count (
  date  DATE   NOT NULL UNIQUE,
  count BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS click_count_date_idx
  ON click_count (date);
