INSERT INTO reports.click_count (date, count) VALUES
  ('2018-07-25', 20180725),
  ('2018-07-26', 20180726),
  ('2018-07-27', 20180727),
  ('2018-07-28', 20180728),
  ('2018-07-29', 20180729),
  ('2018-07-30', 20180730),
  ('2018-07-31', 20180731),
  ('2018-08-01', 20180801),
  ('2018-08-02', 20180802),
  ('2018-08-03', 20180803),
  ('2018-08-04', 20180804),
  ('2018-08-05', 20180805);

INSERT INTO reports.domain_regex (id, pattern, date_added, is_active) VALUES
  (1, '.*vk\.com$', '2018-01-01 01:01:01.000000', TRUE),
  (2, '.*mail\.ru$', '2018-01-01 01:01:01.000000', TRUE),
  (3, '.*delete\.ru$', '2018-01-01 01:01:01.000000', TRUE);

INSERT INTO reports.tracked_domain_requests (date, domain_id, address, login, first_time, last_time, count) VALUES
  ('2018-08-01', 1, '127.0.0.1', 'login1', '00:40:43', '00:40:43', 2),
  ('2018-08-01', 1, '127.0.0.2', 'login2', '00:40:48', '23:46:15', 66),
  ('2018-08-02', 2, '127.0.0.1', 'login1', '00:41:26', '23:05:16', 55),
  ('2018-08-03', 2, '127.0.0.3', 'login3', '00:41:34', '23:39:20', 143),
  ('2018-08-05', 1, '127.0.0.5', '', '00:41:13', '21:40:32', 208);

INSERT INTO log.collectors_history (uuid,
                                    address,
                                    port,
                                    started,
                                    last_update,
                                    period,
                                    processors_threads_count,
                                    packets_received_count,
                                    packets_processed_count,
                                    packets_parse_failed_count,
                                    input_queue_overflow_count,
                                    output_queue_overflow_count,
                                    records_exported_count)
VALUES
  ('7fc93183-4760-4295-87d4-2f5ade5fef0b', 'localhost', 9996, '2019-01-01 00:00:00', '2019-01-01 00:01:00', 30, 1,
                                           250000, 300000, 0, 0, 0, 350000),
  ('7fc93183-4760-4295-87d4-2f5ade5fef0c', 'localhost', 9997, '2019-01-02 00:00:00', '2019-01-02 00:01:00', 30, 1,
                                           250000, 300000, 0, 0, 0, 350000);