ALTER TABLE books ADD COLUMN type VARCHAR(50);

WITH numbered_books AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS row_num
    FROM books
)
UPDATE books
SET type = 'Regular'
WHERE id IN (SELECT id FROM numbered_books WHERE row_num BETWEEN 1 AND 24);

WITH numbered_books AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS row_num
    FROM books
)
UPDATE books
SET type = 'Fiction'
WHERE id IN (SELECT id FROM numbered_books WHERE row_num BETWEEN 25 AND 76);

WITH numbered_books AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS row_num
    FROM books
)
UPDATE books
SET type = 'Normal'
WHERE id IN (SELECT id FROM numbered_books WHERE row_num >= 77);
