INSERT INTO member (first_name, last_name, email, member_role)
SELECT 'Admin', 'Librarian', 'admin@isk.sk', 'LIBRARIAN'
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'admin@isk.sk');

INSERT INTO member (first_name, last_name, email, member_role)
SELECT 'Jan', 'Novak', 'jan.novak@isk.sk', 'MEMBER'
    WHERE NOT EXISTS (SELECT 1 FROM member WHERE email = 'jan.novak@isk.sk');

INSERT INTO book (isbn, title, author, genre, publisher, publication_year, total_copies, available_copies)
SELECT '9780306406157', 'Clean Code', 'Robert C. Martin', 'TECHNOLOGY', 'Prentice Hall', 2008, 3, 3
    WHERE NOT EXISTS (SELECT 1 FROM book WHERE isbn = '9780306406157');

SELECT setval(pg_get_serial_sequence('member', 'member_id'),
              GREATEST((SELECT COALESCE(MAX(member_id), 1) FROM member), 1), true);