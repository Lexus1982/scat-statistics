DROP TABLE IF EXISTS cs_req;
DROP TABLE IF EXISTS cs_resp;
DROP TABLE IF EXISTS generic;

CREATE TABLE cs_req (
  event_time TIMESTAMP,
  login      VARCHAR(8000),
  ip_src     CHAR(15),
  ip_dst     CHAR(15),
  hostname   VARCHAR(8000),
  path       VARCHAR(8000),
  refer      VARCHAR(8000),
  user_agent VARCHAR(8000),
  cookie     VARCHAR(8000),
  session_id DECIMAL,
  locked     DECIMAL,
  host_type  SMALLINT,
  method     SMALLINT
);

CREATE INDEX cs_req_timestamp_idx
  ON cs_req (event_time);
CREATE INDEX cs_req_ip_src_idx
  ON cs_req (ip_src);
CREATE INDEX cs_req_hostname_idx
  ON cs_req (hostname);

CREATE TABLE cs_resp (
  event_time     TIMESTAMP,
  login          VARCHAR(8000),
  ip_src         CHAR(15),
  ip_dst         CHAR(15),
  result_code    BIGINT,
  content_length DECIMAL,
  content_type   VARCHAR(8000),
  session_id     DECIMAL
);

CREATE TABLE generic (
  octet_delta_count              DECIMAL,
  packet_delta_count             DECIMAL,
  protocol_identifier            SMALLINT,
  ip_class_of_service            SMALLINT,
  source_transport_port          INTEGER,
  source_ipv4_address            CHAR(15),
  destination_transport_port     INTEGER,
  destination_ipv4_address       CHAR(15),
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
  post_nat_source_ipv4_address   CHAR(15),
  post_nat_source_transport_port INTEGER
);