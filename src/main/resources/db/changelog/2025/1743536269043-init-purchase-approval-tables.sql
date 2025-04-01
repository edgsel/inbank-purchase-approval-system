-- liquibase formatted sql logicalFilePath:classpath:/db/changelog/2025/1743536269043-init-purchase-approval-tables.sql 
-- changeset author:edgsel

CREATE TABLE profile (
    id SERIAL PRIMARY KEY,
    financial_capacity_factor INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    profile_id INTEGER NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer_profile FOREIGN KEY (profile_id) REFERENCES profile(id) ON DELETE CASCADE
);

CREATE TABLE purchase (
    id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    max_allowed_amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_purchase_customer FOREIGN KEY (customer_id) REFERENCES customer(id) ON DELETE CASCADE
);
