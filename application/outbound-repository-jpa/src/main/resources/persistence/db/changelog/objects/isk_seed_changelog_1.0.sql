-- MEMBERS
INSERT INTO member (first_name, last_name, email, member_role, expiry_date, membership_status)
SELECT 'Admin', 'Librarian', 'admin@isk.sk', 'LIBRARIAN', NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'admin@isk.sk');

INSERT INTO member (first_name, last_name, email, member_role, expiry_date, membership_status)
SELECT 'Jan', 'Novak', 'jan.novak@isk.sk', 'MEMBER', '2027-04-05', 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'jan.novak@isk.sk');

INSERT INTO member (first_name, last_name, email, member_role, expiry_date, membership_status)
SELECT 'Eva', 'Kovacova', 'eva.kovacova@isk.sk', 'MEMBER', '2027-04-05', 'ACTIVE'
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'eva.kovacova@isk.sk');

INSERT INTO member (first_name, last_name, email, member_role, expiry_date, membership_status)
SELECT 'Peter', 'Horak', 'peter.horak@isk.sk', 'MEMBER', '2024-01-01', 'EXPIRED'
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'peter.horak@isk.sk');

INSERT INTO member (first_name, last_name, email, member_role, expiry_date, membership_status)
SELECT 'Maria', 'Balazova', 'maria.balazova@isk.sk', 'MEMBER', NULL, NULL
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'maria.balazova@isk.sk');

-- BOOKS
INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780306406157', 'Clean Code', 'Robert C. Martin', 'TECHNOLOGY', 'Prentice Hall', 2008, 3, 3
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780306406157');

INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780132350884', 'The Pragmatic Programmer', 'David Thomas', 'TECHNOLOGY', 'Addison-Wesley', 1999, 2, 2
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780132350884');

INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780201633610', 'Design Patterns', 'Gang of Four', 'TECHNOLOGY', 'Addison-Wesley', 1994, 1, 0
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780201633610');

INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 'FICTION', 'Scribner', 1925, 4, 4
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780743273565');

INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780061965487', 'To Kill a Mockingbird', 'Harper Lee', 'FICTION', 'HarperCollins', 1960, 2, 1
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780061965487');

-- FINES
INSERT INTO fine (member_id, amount, currency, reason, status)
SELECT (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk'),
       2.50, 'EUR', 'Oneskorené vrátenie o 5 dní', 'PENDING'
    WHERE NOT EXISTS (
    SELECT 1 FROM fine f
    WHERE f.member_id = (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk')
    AND f.reason = 'Oneskorené vrátenie o 5 dní'
);

INSERT INTO fine (member_id, amount, currency, reason, status)
SELECT (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk'),
       1.00, 'EUR', 'Oneskorené vrátenie o 2 dni', 'PAID'
    WHERE NOT EXISTS (
    SELECT 1 FROM fine f
    WHERE f.member_id = (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk')
    AND f.reason = 'Oneskorené vrátenie o 2 dni'
);

-- RESERVATIONS
INSERT INTO reservation (member_id, isbn, created_on, status, position_in_queue)
SELECT (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk'),
       '9780201633610', CURRENT_DATE, 'PENDING', 1
    WHERE NOT EXISTS (
    SELECT 1 FROM reservation r
    WHERE r.member_id = (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk')
    AND r.isbn = '9780201633610'
);

INSERT INTO reservation (member_id, isbn, created_on, status, position_in_queue)
SELECT (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk'),
       '9780201633610', CURRENT_DATE, 'PENDING', 2
    WHERE NOT EXISTS (
    SELECT 1 FROM reservation r
    WHERE r.member_id = (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk')
    AND r.isbn = '9780201633610'
);

INSERT INTO reservation (member_id, isbn, created_on, status, position_in_queue)
SELECT (SELECT member_id FROM member WHERE email = 'peter.horak@isk.sk'),
       '9780061965487', CURRENT_DATE - INTERVAL '5 days', 'READY_FOR_PICKUP', 1
WHERE NOT EXISTS (
    SELECT 1 FROM reservation r
    WHERE r.member_id = (SELECT member_id FROM member WHERE email = 'peter.horak@isk.sk')
  AND r.isbn = '9780061965487'
    );

-- LOANS
INSERT INTO loan (member_id, isbn, created_by_id, loan_date, due_date, renewal_count, status)
SELECT (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk'),
       '9780061965487',
       (SELECT member_id FROM member WHERE email = 'admin@isk.sk'),
       CURRENT_DATE - INTERVAL '10 days',
    CURRENT_DATE + INTERVAL '4 days',
    0, 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1 FROM loan l
    WHERE l.member_id = (SELECT member_id FROM member WHERE email = 'jan.novak@isk.sk')
  AND l.isbn = '9780061965487'
    );

INSERT INTO loan (member_id, isbn, created_by_id, loan_date, due_date, return_date, renewal_count, status)
SELECT (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk'),
       '9780306406157',
       (SELECT member_id FROM member WHERE email = 'admin@isk.sk'),
       CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE - INTERVAL '6 days',
    CURRENT_DATE - INTERVAL '1 day',
    0, 'RETURNED'
WHERE NOT EXISTS (
    SELECT 1 FROM loan l
    WHERE l.member_id = (SELECT member_id FROM member WHERE email = 'eva.kovacova@isk.sk')
  AND l.isbn = '9780306406157'
    );

INSERT INTO loan (member_id, isbn, created_by_id, loan_date, due_date, renewal_count, status)
SELECT (SELECT member_id FROM member WHERE email = 'peter.horak@isk.sk'),
       '9780201633610',
       (SELECT member_id FROM member WHERE email = 'admin@isk.sk'),
       CURRENT_DATE - INTERVAL '20 days',
    CURRENT_DATE - INTERVAL '6 days',
    0, 'OVERDUE'
WHERE NOT EXISTS (
    SELECT 1 FROM loan l
    WHERE l.member_id = (SELECT member_id FROM member WHERE email = 'peter.horak@isk.sk')
  AND l.isbn = '9780201633610'
    );

-- RESET SEQUENCES
SELECT setval(pg_get_serial_sequence('member', 'member_id'),
              GREATEST((SELECT COALESCE(MAX(member_id), 1) FROM member), 1), true);

SELECT setval(pg_get_serial_sequence('reservation', 'reservation_id'),
              GREATEST((SELECT COALESCE(MAX(reservation_id), 1) FROM reservation), 1), true);

SELECT setval(pg_get_serial_sequence('loan', 'loan_id'),
              GREATEST((SELECT COALESCE(MAX(loan_id), 1) FROM loan), 1), true);