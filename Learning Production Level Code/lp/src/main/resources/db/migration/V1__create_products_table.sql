CREATE TABLE products
(
    product_id        BIGSERIAL PRIMARY KEY,
    title             VARCHAR(200)   NOT NULL,
    description       TEXT,
    short_description VARCHAR(500),
    price             NUMERIC(10, 2) NOT NULL,
    live              BOOLEAN        NOT NULL,
    out_of_stock      BOOLEAN        NOT NULL,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);

CREATE INDEX idx_product_live_price
    ON products (live, price);

CREATE INDEX idx_product_title
    ON products (title);