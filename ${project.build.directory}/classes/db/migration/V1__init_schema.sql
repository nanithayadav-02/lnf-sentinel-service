-- =====================================================================
-- Production Issue Tracker (Sentinel) — Initial Schema (PostgreSQL)
-- Sentinel is the SINGLE SOURCE OF TRUTH (no external tracker).
-- Multi-tenant: shared database + shared schema, tenant_id discriminator.
-- =====================================================================

-- ---------- TENANTS ----------
-- One row per customer whose production environment we operate/support.
CREATE TABLE tenants (
    id              BIGSERIAL PRIMARY KEY,
    tenant_code     VARCHAR(40)  NOT NULL UNIQUE,         -- e.g. ACME, GLOBEX
    name            VARCHAR(160) NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',   -- ONBOARDING|ACTIVE|SUSPENDED|OFFBOARDED
    support_tier    VARCHAR(20)  NOT NULL DEFAULT 'STANDARD', -- BASIC|STANDARD|PREMIUM|ENTERPRISE
    region          VARCHAR(40),
    production_url  VARCHAR(255),
    primary_contact_name  VARCHAR(160),
    primary_contact_email VARCHAR(160),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------- USERS ----------
-- Internal ops/support/engineering staff (tenant_id NULL) and, optionally, tenant users.
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(160) NOT NULL UNIQUE,
    full_name   VARCHAR(160) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'ENGINEER', -- ADMIN|ENGINEER|SUPPORT|VIEWER
    tenant_id   BIGINT REFERENCES tenants(id),            -- NULL = internal staff
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_users_tenant ON users(tenant_id);

-- ---------- TENANT ENVIRONMENTS ----------
CREATE TABLE tenant_environments (
    id               BIGSERIAL PRIMARY KEY,
    tenant_id        BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    environment      VARCHAR(20) NOT NULL,                 -- PRODUCTION|STAGING|DR
    base_url         VARCHAR(255),
    health_check_url VARCHAR(255),
    region           VARCHAR(40),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, environment)
);

-- ---------- ISSUES ----------
-- The core record: one production issue belonging to one tenant.
-- This IS the engineering record of truth: triage, fix, resolution all live here.
CREATE TABLE issues (
    id                BIGSERIAL PRIMARY KEY,
    issue_key         VARCHAR(20)  NOT NULL UNIQUE,        -- human key, e.g. PRD-1042
    tenant_id         BIGINT NOT NULL REFERENCES tenants(id),
    title             VARCHAR(240) NOT NULL,
    description       TEXT,
    severity          VARCHAR(20)  NOT NULL DEFAULT 'S3_MEDIUM', -- S1_CRITICAL|S2_HIGH|S3_MEDIUM|S4_LOW
    priority          VARCHAR(10)  NOT NULL DEFAULT 'P3',  -- P1|P2|P3|P4
    status            VARCHAR(20)  NOT NULL DEFAULT 'NEW',
        -- NEW|TRIAGED|IN_PROGRESS|AWAITING_TENANT|RESOLVED|CLOSED|REOPENED
    category          VARCHAR(20)  NOT NULL DEFAULT 'BUG',
        -- OUTAGE|BUG|PERFORMANCE|DATA|SECURITY|CONFIG|OTHER
    environment       VARCHAR(20)  NOT NULL DEFAULT 'PRODUCTION', -- PRODUCTION|STAGING|DR
    affected_service  VARCHAR(120),                        -- component / microservice name
    reported_by       BIGINT REFERENCES users(id),
    assignee_id       BIGINT REFERENCES users(id),
    -- Resolution (filled when work completes) -----------------------
    resolution        VARCHAR(20),                         -- FIXED|WONT_FIX|DUPLICATE|CANNOT_REPRODUCE|CONFIG_CHANGE
    root_cause        TEXT,                                -- post-fix root-cause note
    fix_version       VARCHAR(60),                         -- release/build that carries the fix
    -- SLA / timing --------------------------------------------------
    detected_at       TIMESTAMPTZ,
    sla_due_at        TIMESTAMPTZ,
    resolved_at       TIMESTAMPTZ,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_issues_tenant        ON issues(tenant_id);
CREATE INDEX idx_issues_status        ON issues(status);
CREATE INDEX idx_issues_severity      ON issues(severity);
CREATE INDEX idx_issues_assignee      ON issues(assignee_id);
CREATE INDEX idx_issues_tenant_status ON issues(tenant_id, status);

-- ---------- ISSUE COMMENTS ----------
CREATE TABLE issue_comments (
    id          BIGSERIAL PRIMARY KEY,
    issue_id    BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    author_id   BIGINT REFERENCES users(id),
    body        TEXT NOT NULL,
    internal    BOOLEAN NOT NULL DEFAULT TRUE,             -- TRUE = ops-only note
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_comments_issue ON issue_comments(issue_id);

-- ---------- ISSUE ATTACHMENTS ----------
CREATE TABLE issue_attachments (
    id            BIGSERIAL PRIMARY KEY,
    issue_id      BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    file_name     VARCHAR(255) NOT NULL,
    storage_key   VARCHAR(512) NOT NULL,                   -- S3/blob object key
    content_type  VARCHAR(100),
    size_bytes    BIGINT,
    uploaded_by   BIGINT REFERENCES users(id),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_attachments_issue ON issue_attachments(issue_id);

-- ---------- ISSUE STATUS HISTORY (audit trail) ----------
CREATE TABLE issue_status_history (
    id          BIGSERIAL PRIMARY KEY,
    issue_id    BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    from_status VARCHAR(20),
    to_status   VARCHAR(20) NOT NULL,
    changed_by  BIGINT REFERENCES users(id),
    note        VARCHAR(500),
    changed_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_status_history_issue ON issue_status_history(issue_id);

-- ---------- ISSUE LINKS (native cross-issue relationships) ----------
-- Replaces what Jira links gave us: blocks / duplicates / relates / caused-by.
CREATE TABLE issue_links (
    id               BIGSERIAL PRIMARY KEY,
    source_issue_id  BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    target_issue_id  BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    link_type        VARCHAR(20) NOT NULL,                 -- BLOCKS|BLOCKED_BY|DUPLICATES|RELATES_TO|CAUSED_BY
    created_by       BIGINT REFERENCES users(id),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_not_self CHECK (source_issue_id <> target_issue_id),
    UNIQUE (source_issue_id, target_issue_id, link_type)
);
CREATE INDEX idx_links_source ON issue_links(source_issue_id);
CREATE INDEX idx_links_target ON issue_links(target_issue_id);

-- ---------- ISSUE WATCHERS (subscribe to updates) ----------
CREATE TABLE issue_watchers (
    id         BIGSERIAL PRIMARY KEY,
    issue_id   BIGINT NOT NULL REFERENCES issues(id) ON DELETE CASCADE,
    user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (issue_id, user_id)
);
CREATE INDEX idx_watchers_issue ON issue_watchers(issue_id);

-- =====================================================================
-- Seed data (handy for local dev / demos)
-- =====================================================================
INSERT INTO tenants (tenant_code, name, status, support_tier, region, production_url, primary_contact_name, primary_contact_email) VALUES
 ('ACME',   'Acme Retail Group',  'ACTIVE', 'ENTERPRISE', 'eu-west-1', 'https://acme.app.example.com',   'Priya Nair',    'priya.nair@acme.example.com'),
 ('GLOBEX', 'Globex Logistics',   'ACTIVE', 'PREMIUM',    'us-east-1', 'https://globex.app.example.com', 'Tom Adeyemi',   'tom.adeyemi@globex.example.com'),
 ('INITECH','Initech Financial',  'ACTIVE', 'STANDARD',   'eu-west-2', 'https://initech.app.example.com','Sara Lindqvist','sara.l@initech.example.com');

INSERT INTO users (email, full_name, role, tenant_id) VALUES
 ('ops.lead@ourcompany.com',  'Dana Whitfield', 'ADMIN',    NULL),
 ('eng1@ourcompany.com',      'Marcus Bell',    'ENGINEER', NULL),
 ('eng2@ourcompany.com',      'Aiko Tanaka',    'ENGINEER', NULL),
 ('support1@ourcompany.com',  'Leo Moretti',    'SUPPORT',  NULL);
