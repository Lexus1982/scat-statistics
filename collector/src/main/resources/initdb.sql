CREATE SCHEMA IF NOT EXISTS ipfix_data;
CREATE SCHEMA IF NOT EXISTS log;

CREATE TABLE IF NOT EXISTS log.collectors_history (
  uuid                        TEXT PRIMARY KEY,
  address                     TEXT                        NOT NULL,
  port                        SMALLINT                    NOT NULL,
  started                     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  last_update                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  period                      SMALLINT                    NOT NULL,
  processors_threads_count    INTEGER                     NOT NULL  DEFAULT 0,
  packets_received_count      INTEGER                     NOT NULL  DEFAULT 0,
  packets_processed_count     INTEGER                     NOT NULL  DEFAULT 0,
  packets_parse_failed_count  INTEGER                     NOT NULL  DEFAULT 0,
  input_queue_overflow_count  INTEGER                     NOT NULL  DEFAULT 0,
  output_queue_overflow_count INTEGER                     NOT NULL  DEFAULT 0,
  records_exported_count      INTEGER                     NOT NULL  DEFAULT 0
);

CREATE UNLOGGED TABLE IF NOT EXISTS ipfix_data.cs_req (
  event_datetime TIMESTAMP     NOT NULL,
  login          TEXT          NOT NULL,
  ip_src         TEXT          NOT NULL,
  ip_dst         TEXT          NOT NULL,
  hostname       TEXT          NOT NULL,
  path           TEXT          NOT NULL,
  refer          TEXT          NOT NULL,
  user_agent     TEXT          NOT NULL,
  cookie         TEXT          NOT NULL,
  session_id     NUMERIC(1000) NOT NULL,
  locked         NUMERIC(1000) NOT NULL,
  host_type      SMALLINT      NOT NULL,
  method         SMALLINT      NOT NULL
)
  PARTITION BY RANGE (date_trunc('hour', event_datetime))
WITHOUT OIDS;

CREATE UNLOGGED TABLE IF NOT EXISTS ipfix_data.cs_resp (
  event_datetime TIMESTAMP,
  login          VARCHAR(8000),
  ip_src         VARCHAR(15),
  ip_dst         VARCHAR(15),
  result_code    BIGINT,
  content_length DECIMAL,
  content_type   VARCHAR(8000),
  session_id     DECIMAL
) PARTITION BY RANGE (date_trunc('hour', event_datetime))
WITHOUT OIDS;

CREATE TABLE IF NOT EXISTS ipfix_data.partitions (
  id       SERIAL PRIMARY KEY,
  basename TEXT UNIQUE NOT NULL,
  history  SMALLINT    NOT NULL DEFAULT 5
);

INSERT INTO ipfix_data.partitions (basename, history) VALUES ('cs_req', 5), ('cs_resp', 5) ON CONFLICT DO NOTHING;

-- Функция для создания новых секций

CREATE OR REPLACE FUNCTION ipfix_data.create_partitions(date TIMESTAMP DEFAULT (now() + '1 hour' :: INTERVAL))
  RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
  p                RECORD;
  start_date       TIMESTAMP := date_trunc('hour', date);
  end_date         TIMESTAMP := date_trunc('hour', date + INTERVAL '1 hour');
  partition_suffix TEXT := to_char(date, 'YYYYMMDD_HH24');
  schema_name      TEXT := 'ipfix_data';
  pattern          TEXT := 'CREATE UNLOGGED TABLE IF NOT EXISTS %s.%s_%s PARTITION OF %s.%s' ||
                           ' FOR VALUES FROM (%s) TO (%s) WITH (AUTOVACUUM_ENABLED = FALSE)';
BEGIN
  RAISE NOTICE 'create new partitions for between % and %...', start_date, end_date;

  FOR p IN SELECT basename FROM ipfix_data.partitions LOOP
    EXECUTE format(pattern, schema_name, p.basename, partition_suffix, schema_name, p.basename,
                   quote_literal(start_date), quote_literal(end_date));
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для отсоединения секций

CREATE OR REPLACE FUNCTION ipfix_data.detach_partitions(date timestamp DEFAULT (now() - INTERVAL '1 hour'))
  RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  p RECORD;
  sname text := 'ipfix_data';
  partition_name text;
BEGIN
  FOR p IN SELECT basename FROM ipfix_data.partitions LOOP
    partition_name = format('%s.%s_%s', sname, p.basename, to_char(date, 'YYYYMMDD_HH24'));
    RAISE NOTICE 'detach section: %', partition_name;
    EXECUTE format('ALTER TABLE %s.%s DETACH PARTITION %s', sname, p.basename, partition_name);
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для удаления старых секций

CREATE OR REPLACE FUNCTION ipfix_data.drop_old_partitions(date TIMESTAMP DEFAULT now())
  RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  p RECORD;
  sname text := 'ipfix_data';
  partition_name text;
BEGIN
  FOR p IN SELECT basename, history FROM ipfix_data.partitions LOOP
    partition_name = format('%s.%s_%s', sname, p.basename, to_char(date - INTERVAL '1 hour' * p.history , 'YYYYMMDD_HH24'));
    RAISE NOTICE 'drop section: %', partition_name;
    EXECUTE format('DROP TABLE IF EXISTS %s', partition_name);
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для агрегация данных

CREATE OR REPLACE FUNCTION ipfix_data.evaluate_data(date TIMESTAMP DEFAULT now() - INTERVAL '1 hour')
  RETURNS VOID AS $$
DECLARE
  partition_name TEXT := format('ipfix_data.cs_req_%s', to_char(date, 'YYYYMMDD_HH24'));
  rows_inserted  BIGINT;
BEGIN
  -- cоздаем в этой секции индекс по hostname
  RAISE NOTICE 'create index in section: %', partition_name;
  EXECUTE 'CREATE INDEX ON ' || partition_name || ' USING GIN (hostname gin_trgm_ops)';

  -- общее количество запросов
  RAISE NOTICE 'evaluate click count from %', partition_name;
  EXECUTE 'INSERT INTO reports.click_count AS cc SELECT event_datetime::date, count(*) FROM ' || partition_name ||
          ' GROUP BY 1 ON CONFLICT (date) DO UPDATE SET count = cc.count + EXCLUDED.count';
  GET DIAGNOSTICS rows_inserted = ROW_COUNT;
  RAISE NOTICE '% rows inserted', rows_inserted;

  -- статистика по отслеживаемым доменам
  RAISE NOTICE 'evaluate tracked domain requests from %', partition_name;
  EXECUTE 'INSERT INTO reports.tracked_domain_requests AS tdr ' ||
          ' SELECT ' ||
          '   event_datetime::date, ' ||
          '   dr.id              AS domain_id, ' ||
          '   cs.ip_src, ' ||
          '   cs.login, ' ||
          '   min(cs.event_datetime)::time AS first_time, ' ||
          '   max(cs.event_datetime)::time AS last_time, ' ||
          '   count(*)           AS cnt ' ||
          ' FROM ' || partition_name || ' AS cs INNER JOIN reports.domain_regex dr ' ||
          ' ON cs.hostname ~* dr.pattern ' ||
          ' GROUP BY 1, 2, 3, 4' ||
          ' ON CONFLICT (date, domain_id, address, login) DO UPDATE SET ' ||
          '   last_time = EXCLUDED.last_time, ' ||
          '   count = tdr.count + EXCLUDED.count ';

  GET DIAGNOSTICS rows_inserted = ROW_COUNT;
  RAISE NOTICE '% rows inserted', rows_inserted;
END;
$$
LANGUAGE plpgsql
SECURITY DEFINER;

/*
CREATE TABLE IF NOT EXISTS generic (
  octet_delta_count              DECIMAL,
  packet_delta_count             DECIMAL,
  protocol_identifier            SMALLINT,
  ip_class_of_service            SMALLINT,
  source_transport_port          INTEGER,
  source_ipv4_address            VARCHAR(15),
  destination_transport_port     INTEGER,
  destination_ipv4_address       VARCHAR(15),
  bgp_source_as_number           BIGINT,
  bgp_destination_as_number      BIGINT,
  flow_start_millisecond         TIMESTAMP,
  flow_end_millisecond           TIMESTAMP,
  input_snmp                     INTEGER,
  output_snmp                    INTEGER,
  ip_version                     SMALLINT,
  session_id                     DECIMAL,
  http_host                      VARCHAR(8000),
  dpi_protocol                   INTEGER,
  login                          VARCHAR(8000),
  post_nat_source_ipv4_address   VARCHAR(15),
  post_nat_source_transport_port INTEGER
);
*/