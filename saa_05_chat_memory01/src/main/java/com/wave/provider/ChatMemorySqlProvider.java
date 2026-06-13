package com.wave.provider;

import com.wave.vo.AIChatMemory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public class ChatMemorySqlProvider {

    //构建批量插入SQL语句
    public String insertBatch(@Param("list") List<AIChatMemory> chatMemoryList) {
        if (chatMemoryList == null || chatMemoryList.size() == 0){
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ai_chat_memory (user_id, conversation_id, message_type, content, create_time) values ");
        //循环拼接SQL，只需要拼接values的值
        for (int i = 0; i < chatMemoryList.size(); i++) {
            sql.append("(#{list[").append(i).append("].userId},");
            sql.append("#{list[").append(i).append("].conversationId},");
            sql.append("#{list[").append(i).append("].messageType},");
            sql.append("#{list[").append(i).append("].content},");
            sql.append("#{list[").append(i).append("].createTime})");
            if (i != chatMemoryList.size() - 1) {
                sql.append(",");
            }
        }
        return sql.toString();
    }
}
