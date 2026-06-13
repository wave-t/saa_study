CREATE TABLE ai_chat_memory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    message_type VARCHAR(32) NOT NULL COMMENT 'USER/ASSISTANT/SYSTEM',
    content TEXT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_conversation (user_id, conversation_id)
);