-- Database initialization script for PG CRUD with Indexing

-- Create a function to safely create indexes
CREATE OR REPLACE FUNCTION create_index_if_not_exists()
RETURNS void AS $$
BEGIN
    -- Users table indexes (only if table exists)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_user_email ON users(email)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_user_username ON users(username)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_user_created_at ON users(created_at)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_user_status ON users(status)';
        
        -- Composite indexes for users
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_user_status_created_at ON users(status, created_at)';
    END IF;
    
    -- Orders table indexes (only if table exists)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'orders') THEN
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_user_id ON orders(user_id)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_created_at ON orders(created_at)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_order_number ON orders(order_number)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_total_amount ON orders(total_amount)';
        
        -- Composite indexes for orders
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_user_status ON orders(user_id, status)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_order_status_created_at ON orders(status, created_at)';
    END IF;
    
    -- Activity log table indexes (only if table exists)
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'activity_log') THEN
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_user_id ON activity_log(user_id)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_action ON activity_log(action)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_entity_type ON activity_log(entity_type)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_created_at ON activity_log(created_at)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_ip_address ON activity_log(ip_address)';
        
        -- Composite indexes for activity log
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_user_action ON activity_log(user_id, action)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_activity_entity_created_at ON activity_log(entity_type, created_at)';
    END IF;
END;
$$ LANGUAGE plpgsql;

-- Execute the function
SELECT create_index_if_not_exists();

-- Drop the function (optional)
DROP FUNCTION IF EXISTS create_index_if_not_exists();

-- Note: Sample data will be created by the application after tables are created
-- This avoids conflicts during database initialization

-- Create a function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at (only if tables exist)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'users') THEN
        EXECUTE 'CREATE TRIGGER IF NOT EXISTS update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column()';
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'orders') THEN
        EXECUTE 'CREATE TRIGGER IF NOT EXISTS update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column()';
    END IF;
END $$;

-- Grant necessary permissions
GRANT USAGE ON SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO postgres;
