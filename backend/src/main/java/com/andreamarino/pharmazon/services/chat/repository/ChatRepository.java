package com.andreamarino.pharmazon.services.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.andreamarino.pharmazon.services.chat.model.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

        @Query("SELECT c FROM Chat c WHERE c.client.username = :username AND c.activeChat = true AND c.accepted = true")
        Optional<Chat> findChatAccepted(@Param("username") String username);

        @Query("SELECT c FROM Chat c WHERE c.activeChat = true AND c.accepted = false ORDER BY c.dateTimeCreation ASC LIMIT 1")
        Optional<Chat> findLastAcceptedChat();

        @Query("SELECT c FROM Chat c WHERE c.activeChat = true AND c.accepted = true AND c.pharmacist.username = :username")
        Optional<Chat> findLastAcceptedChat(@Param("username") String username);
        
        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Chat c SET c.accepted = true " +
                "WHERE c.activeChat = true")
        void updateChatWaiting(@Param("username") String username);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Chat c SET c.activeChat = false, c.accepted = false " +
                "WHERE c.pharmacist.username = :username AND c.activeChat = true")
        void closeChat(@Param("username") String username);

        @Modifying(clearAutomatically = true, flushAutomatically = true)
        @Query("UPDATE Chat c SET c.activeChat = false, c.accepted = false " +
                "WHERE c.client.username = :username AND c.activeChat = true")
        void closeChatClient(@Param("username") String username);

        @Query("SELECT c FROM Chat c WHERE c.code = :code")
        Optional<Chat> findByCode(@Param("code") String code);
}
