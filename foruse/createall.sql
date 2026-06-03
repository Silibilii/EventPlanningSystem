DROP SCHEMA IF EXISTS eventschema CASCADE;
CREATE SCHEMA eventschema;


CREATE TABLE eventschema.client (
    client_id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    is_legal BOOLEAN DEFAULT FALSE,
    organization_name VARCHAR(150),
    notes TEXT
);


CREATE TABLE eventschema.venue (
    venue_id BIGSERIAL PRIMARY KEY,
    venue_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    capacity INT NOT NULL,
    rental_cost DECIMAL(10,2) NOT NULL CHECK (rental_cost >= 0),
    contact_phone VARCHAR(20)
);


CREATE TABLE eventschema.contractor (
    contractor_id BIGSERIAL PRIMARY KEY,
    contractor_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    service_type VARCHAR(50) NOT NULL,
    price_list TEXT,
    notes TEXT,
    CONSTRAINT chk_service_type CHECK (service_type IN ('ведущий', 'декоратор', 'кейтеринг', 'фотограф', 'звукорежиссёр', 'прочее'))
);


CREATE TABLE eventschema.event (
    event_id BIGSERIAL PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    budget DECIMAL(12,2) NOT NULL CHECK (budget >= 0),
    status VARCHAR(30) NOT NULL DEFAULT 'планирование',
    feedback TEXT,
    client_id BIGINT NOT NULL,
    venue_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_event_status CHECK (status IN ('планирование', 'в процессе', 'завершено', 'отменено')),
    FOREIGN KEY (client_id) REFERENCES eventschema.client(client_id) ON DELETE RESTRICT,
    FOREIGN KEY (venue_id) REFERENCES eventschema.venue(venue_id) ON DELETE RESTRICT
);


CREATE TABLE eventschema.task (
    task_id BIGSERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    deadline DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ожидает',
    event_id BIGINT NOT NULL,
    responsible_contractor_id BIGINT NULL,
    CONSTRAINT chk_task_status CHECK (status IN ('ожидает', 'в работе', 'выполнено', 'просрочено')),
    FOREIGN KEY (event_id) REFERENCES eventschema.event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (responsible_contractor_id) REFERENCES eventschema.contractor(contractor_id) ON DELETE RESTRICT
);


CREATE TABLE eventschema.expense (
    expense_id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    expense_date DATE NOT NULL DEFAULT CURRENT_DATE,
    description VARCHAR(255),
    event_id BIGINT NOT NULL,
    contractor_id BIGINT NULL,
    category VARCHAR(50) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES eventschema.event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (contractor_id) REFERENCES eventschema.contractor(contractor_id) ON DELETE SET NULL
);


CREATE TABLE eventschema.guest (
    guest_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    invitation_status VARCHAR(20) NOT NULL DEFAULT 'приглашён',
    event_id BIGINT NOT NULL,
    CONSTRAINT chk_guest_status CHECK (invitation_status IN ('приглашён', 'подтвердил', 'оплатил', 'отказался')),
    FOREIGN KEY (event_id) REFERENCES eventschema.event(event_id) ON DELETE CASCADE
);


CREATE INDEX idx_event_client ON eventschema.event(client_id);
CREATE INDEX idx_event_date ON eventschema.event(event_date);
CREATE INDEX idx_task_event ON eventschema.task(event_id);
CREATE INDEX idx_task_deadline ON eventschema.task(deadline);
CREATE INDEX idx_expense_event ON eventschema.expense(event_id);
CREATE INDEX idx_expense_date ON eventschema.expense(expense_date);
CREATE INDEX idx_guest_event ON eventschema.guest(event_id);
CREATE INDEX idx_guest_status ON eventschema.guest(invitation_status);


CREATE OR REPLACE FUNCTION eventschema.get_remaining_budget(p_event_id BIGINT)
RETURNS DECIMAL(12,2)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    total_expenses DECIMAL(12,2);
    event_budget DECIMAL(12,2);
BEGIN
    SELECT budget INTO event_budget FROM eventschema.event WHERE event_id = p_event_id;
    IF NOT FOUND THEN
        RETURN NULL;
    END IF;
    
    SELECT COALESCE(SUM(amount), 0) INTO total_expenses
    FROM eventschema.expense
    WHERE event_id = p_event_id;
    
    RETURN event_budget - total_expenses;
END;
$$;


CREATE TABLE eventschema.audit_log (
    audit_id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    operation CHAR(1) NOT NULL,
    old_data TEXT,
    new_data TEXT,
    changed_by VARCHAR(50) NOT NULL,
    changed_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE OR REPLACE FUNCTION eventschema.audit_trigger_func()
RETURNS TRIGGER
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO eventschema.audit_log (table_name, operation, old_data, new_data, changed_by)
        VALUES (TG_TABLE_NAME, 'I', NULL, NEW::TEXT, current_user);
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO eventschema.audit_log (table_name, operation, old_data, new_data, changed_by)
        VALUES (TG_TABLE_NAME, 'U', OLD::TEXT, NEW::TEXT, current_user);
    ELSIF TG_OP = 'DELETE' THEN
        INSERT INTO eventschema.audit_log (table_name, operation, old_data, new_data, changed_by)
        VALUES (TG_TABLE_NAME, 'D', OLD::TEXT, NULL, current_user);
    END IF;
    RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION eventschema.get_expense_summary(p_event_id BIGINT)
RETURNS TABLE(category_name VARCHAR, total_amount DECIMAL(10,2))
LANGUAGE plpgsql
STABLE
AS $$
BEGIN
    RETURN QUERY
    SELECT e.category, SUM(e.amount) AS total
    FROM eventschema.expense e
    WHERE e.event_id = p_event_id
    GROUP BY e.category
    ORDER BY e.category;
END;
$$;

-- Триггеры
CREATE TRIGGER audit_client AFTER INSERT OR UPDATE OR DELETE ON eventschema.client
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_venue AFTER INSERT OR UPDATE OR DELETE ON eventschema.venue
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_contractor AFTER INSERT OR UPDATE OR DELETE ON eventschema.contractor
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_event AFTER INSERT OR UPDATE OR DELETE ON eventschema.event
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_task AFTER INSERT OR UPDATE OR DELETE ON eventschema.task
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_expense AFTER INSERT OR UPDATE OR DELETE ON eventschema.expense
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();
CREATE TRIGGER audit_guest AFTER INSERT OR UPDATE OR DELETE ON eventschema.guest
    FOR EACH ROW EXECUTE FUNCTION eventschema.audit_trigger_func();

	DO $$
DECLARE
    rec RECORD;
BEGIN
    FOR rec IN SELECT usename FROM pg_user WHERE usename == 'manager'
    LOOP
        EXECUTE 'REVOKE ALL PRIVILEGES ON DATABASE events FROM ' || quote_ident(rec.usename);

        EXECUTE 'REVOKE ALL PRIVILEGES ON SCHEMA eventschema FROM ' || quote_ident(rec.usename);
        EXECUTE 'REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA eventschema FROM ' || quote_ident(rec.usename);

        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA eventschema REVOKE ALL ON TABLES FROM ' || quote_ident(rec.usename);

        EXECUTE 'SELECT pg_terminate_backend(pg_stat_activity.pid) FROM pg_stat_activity WHERE usename = ' || quote_literal(rec.usename);

        EXECUTE 'DROP USER IF EXISTS ' || quote_ident(rec.usename);
    END LOOP;
END $$;







CREATE USER manager WITH PASSWORD 'manager';

GRANT CONNECT ON DATABASE events TO manager;

GRANT USAGE ON SCHEMA eventschema TO manager;

GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA eventschema TO manager;

ALTER DEFAULT PRIVILEGES IN SCHEMA eventschema GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO manager;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA eventschema TO manager;

ALTER DEFAULT PRIVILEGES IN SCHEMA eventschema GRANT USAGE, SELECT ON SEQUENCES TO manager;


