-- Create an initial table and populate it with some data.

CREATE TABLE public.scratch_test_table
(
  some_pkey integer NOT NULL,
  some_text text,
  some_integer integer,
  some_boolean boolean NOT NULL DEFAULT false,
  CONSTRAINT some_pkey PRIMARY KEY (some_pkey)
)
WITH (
  OIDS=FALSE
);



INSERT INTO scratch_test_table(some_text, some_integer, some_boolean)
VALUES
(NULL, 1234, TRUE),
('Hello, world', NULL, FALSE),
('Needs moar cowbell', 42, TRUE)
;
