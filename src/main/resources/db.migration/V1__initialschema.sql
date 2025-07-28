CREATE SEQUENCE product_id_seq START 1;

CREATE TABLE product (
                         id BIGINT DEFAULT nextval('product_id_seq') PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         price NUMERIC(19, 2) NOT NULL,
                         description TEXT,
                         category VARCHAR(255) NOT NULL,
                         active BOOLEAN NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP
);

CREATE SEQUENCE product_image_id_seq START 1;

CREATE TABLE product_image (
                               id BIGINT DEFAULT nextval('product_image_id_seq') PRIMARY KEY,
                               product_id BIGINT NOT NULL,
                               url VARCHAR(255) NOT NULL,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP,
                               CONSTRAINT fk_product_image FOREIGN KEY (product_id) REFERENCES product (id)
);