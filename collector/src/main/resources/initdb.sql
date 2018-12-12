CREATE SCHEMA IF NOT EXISTS ipfix_data;

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
  PARTITION BY RANGE (date_trunc('day', event_datetime))
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
) PARTITION BY RANGE (date_trunc('day', event_datetime))
WITHOUT OIDS;

CREATE TABLE IF NOT EXISTS ipfix_data.partitions (
  id           SERIAL PRIMARY KEY,
  basename     TEXT UNIQUE NOT NULL,
  history_days SMALLINT    NOT NULL DEFAULT 2
);

INSERT INTO ipfix_data.partitions (basename, history_days) VALUES ('cs_req', 2), ('cs_resp', 2) ON CONFLICT DO NOTHING;

-- Функция для создания новых секций, как правило для следующего дня

CREATE OR REPLACE FUNCTION ipfix_data.create_partitions(day DATE DEFAULT (CURRENT_DATE + '1 day' :: INTERVAL))
  RETURNS VOID
LANGUAGE plpgsql
AS $$
DECLARE
  p                RECORD;
  start_date       DATE := day;
  end_date         DATE := day + INTERVAL '1 day';
  partition_suffix TEXT := to_char(day, 'YYYYMMDD');
  schema_name      TEXT := 'ipfix_data';
  pattern          TEXT := 'CREATE UNLOGGED TABLE IF NOT EXISTS %s.%s_%s PARTITION OF %s.%s' ||
                           ' FOR VALUES FROM (%s) TO (%s) WITH (AUTOVACUUM_ENABLED = FALSE)';
BEGIN
  RAISE NOTICE 'create new partitions for % ...', day;

  FOR p IN SELECT basename FROM ipfix_data.partitions LOOP
    EXECUTE format(pattern, schema_name, p.basename, partition_suffix, schema_name, p.basename,
                   quote_literal(start_date), quote_literal(end_date));
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для отсоединения секций, как правило для вчерашнего дня

CREATE OR REPLACE FUNCTION ipfix_data.detach_partitions(day date DEFAULT (CURRENT_DATE - INTERVAL '1 day')::date)
  RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  p RECORD;
  sname text := 'ipfix_data';
  partition_name text;
BEGIN
  FOR p IN SELECT basename FROM ipfix_data.partitions LOOP
    partition_name = format('%s.%s_%s', sname, p.basename, to_char(day, 'YYYYMMDD'));
    RAISE NOTICE 'detach section: %', partition_name;
    EXECUTE format('ALTER TABLE %s.%s DETACH PARTITION %s', sname, p.basename, partition_name);
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для удаления старых секций

CREATE OR REPLACE FUNCTION ipfix_data.drop_old_partitions(day date DEFAULT CURRENT_DATE)
  RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  p RECORD;
  sname text := 'ipfix_data';
  partition_name text;
BEGIN
  FOR p IN SELECT basename, history_days FROM ipfix_data.partitions LOOP
    partition_name = format('%s.%s_%s', sname, p.basename, to_char(day - INTERVAL '1 day' * p.history_days , 'YYYYMMDD'));
    RAISE NOTICE 'drop section: %', partition_name;
    EXECUTE format('DROP TABLE IF EXISTS %s', partition_name);
  END LOOP;
END;
$$
SECURITY DEFINER;

-- Функция для агрегация данных

CREATE OR REPLACE FUNCTION ipfix_data.evaluate_data(day DATE DEFAULT current_date - INTERVAL '1 day')
  RETURNS VOID AS $$
DECLARE
  partition_name TEXT := format('ipfix_data.cs_req_%s', to_char(day, 'YYYYMMDD'));
  rows_inserted  BIGINT;
BEGIN
  -- cоздаем в этой секции индекс по hostname
  RAISE NOTICE 'create index in section: %', partition_name;
  EXECUTE 'CREATE INDEX ON ' || partition_name || ' USING GIN (hostname gin_trgm_ops)';

  -- общее количество запросов
  RAISE NOTICE 'evaluate click count from %', partition_name;
  EXECUTE 'INSERT INTO reports.click_count SELECT event_datetime::date, count(*) FROM ' || partition_name || ' GROUP BY 1';
  GET DIAGNOSTICS rows_inserted = ROW_COUNT;
  RAISE NOTICE '% rows inserted', rows_inserted;

  -- статистика по отслеживаемым доменам
  RAISE NOTICE 'evaluate tracked domain requests from %', partition_name;
  EXECUTE 'INSERT INTO reports.tracked_domain_requests' ||
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
          ' GROUP BY 1, 2, 3, 4';

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