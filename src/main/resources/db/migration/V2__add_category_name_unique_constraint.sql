-- V2: Ensure category names are unique

ALTER TABLE categories
ADD CONSTRAINT uk_categories_name UNIQUE (name);