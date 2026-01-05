CREATE TABLE IF NOT EXISTS expenses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    incurred_date DATE NOT NULL,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_expense_user_id ON expenses(user_id);
CREATE INDEX idx_expense_incurred_date ON expenses(incurred_date);
CREATE INDEX idx_expense_category ON expenses(category);
