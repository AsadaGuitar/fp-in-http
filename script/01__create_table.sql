CREATE TABLE IF NOT EXISTS users(
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS chatrooms(
    id VARCHAR(255) NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS user_chatrooms(
    id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    chatroom_id VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (chatroom_id) REFERENCES chatrooms(id)
);

CREATE TABLE IF NOT EXISTS messages(
    id VARCHAR(255) NOT NULL,
    chatroom_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    reply_to VARCHAR(255),
    text VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (chatroom_id) REFERENCES chatrooms(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (reply_to) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS follows(
    id VARCHAR(255) NOT NULL,
    from VARCHAR(255) NOT NULL,
    to VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (from) REFERENCES users(id),
    FOREIGN KEY (to) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS favorites(
    id VARCHAR(255) NOT NULL,
    message_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (message_id) REFERENCES messages(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS invitations(
    id VARCHAR(255) NOT NULL,
    chatroomId VARCHAR(255) NOT NULL,
    from VARCHAR(255) NOT NULL,
    to VARCHAR(255) NOT NULL,
    is_closed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    modified_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id),
    FOREIGN KEY (chatroomId) REFERENCES chatrooms(id),
    FOREIGN KEY (from) REFERENCES users(id),
    FOREIGN KEY (to) REFERENCES users(id)
);