package com.wave.mapper;

import com.wave.provider.ChatMemorySqlProvider;
import com.wave.vo.AIChatMemory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMemoryMapper {

    // 批量插入会话消息
    @InsertProvider(type = ChatMemorySqlProvider.class, method = "insertBatch")
    void insertBatch(@Param("list") List<AIChatMemory> chatMemoryList);

    // 根据会话ID查询消息列表
    @Select("select id,user_id,conversation_id,message_type As messageType,content,create_time" +
            " from ai_chat_memory where conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<AIChatMemory> queryByConversationId(@Param("conversationId") String conversationId);

    // 根据会话ID删除消息列表
    @Delete("delete from ai_chat_memory where conversation_id = #{conversationId}")
    void deleteByConversationId(@Param("conversationId") String conversationId);
}
