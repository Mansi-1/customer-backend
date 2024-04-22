-- changeset postgres:1
CREATE TABLE customers
(
    id uuid primary key,
    customer_id int,
    customer_name varchar(50)
);

CREATE TABLE books
(
    id uuid primary key,
    book_id uuid,
    author_name varchar(50),
    book_name varchar(50)
);

CREATE TABLE lending_history
(
    id uuid primary key,
    customer_id int,
    book_id uuid,
    lend_date date,
    lent_till date
);
