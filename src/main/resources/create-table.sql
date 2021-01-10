CREATE TABLE IF NOT EXISTS order_table (
    id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    ship_date VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL,
    complete BOOLEAN NOT NULL
)

CREATE TABLE IF NOT EXISTS pet_table (
    id BIGINT NOT NULL,
    name VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL,
    category_id BIGINT NOT NULL,
    category_name VARCHAR(32) NULL,
    tag_id INT NOT NULL,
    tag_name VARCHAR(32) NOT NULL,
    photo_url VARCHAR(256) NOT NULL
)



