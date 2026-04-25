package com.supervision.livraisons.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.MessageRepository;
import com.supervision.livraisons.repository.UserRepository;
import com.supervision.livraisons.websocket.EmergencyWebSocketHandler;

@Service
public class MessageService {

    private static final String ROLE_CONTROLEUR = "CONTROLEUR";

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EmergencyWebSocketHandler emergencyWebSocketHandler;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          EmergencyWebSocketHandler emergencyWebSocketHandler) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.emergencyWebSocketHandler = emergencyWebSocketHandler;
    }

    public List<Map<String, Object>> getConversations(String currentUserId) {
        List<Message> directMessages = messageRepository.findDirectMessagesForUser(
                currentUserId,
                Sort.by(Sort.Direction.DESC, "timestamp")
        );
        List<Message> broadcastMessages = messageRepository.findByReceiverIdIsNullOrderByTimestampDesc();

        Map<String, User> userCache = new HashMap<>();
        Map<String, Map<String, Object>> conversations = new HashMap<>();

        for (Message message : directMessages) {
            String partnerId = currentUserId.equals(message.getSenderId()) ? message.getReceiverId() : message.getSenderId();
            if (!StringUtils.hasText(partnerId) || currentUserId.equals(partnerId)) {
                continue;
            }
            boolean unreadIncoming = currentUserId.equals(message.getReceiverId()) && !message.isRead();
            applyConversationUpdate(conversations, userCache, partnerId, message, unreadIncoming);
        }

        for (Message message : broadcastMessages) {
            String partnerId = message.getSenderId();
            if (!StringUtils.hasText(partnerId) || currentUserId.equals(partnerId)) {
                continue;
            }
            boolean unreadIncoming = !message.isRead();
            applyConversationUpdate(conversations, userCache, partnerId, message, unreadIncoming);
        }

        List<Map<String, Object>> result = new ArrayList<>(conversations.values());
        result.sort((a, b) -> {
            Date left = (Date) a.get("lastTimestamp");
            Date right = (Date) b.get("lastTimestamp");
            if (left == null && right == null) {
                return 0;
            }
            if (left == null) {
                return 1;
            }
            if (right == null) {
                return -1;
            }
            return right.compareTo(left);
        });

        return result;
    }

    public List<Message> getThread(String currentUserId, String partnerId) {
        List<Message> thread = new ArrayList<>(messageRepository.findThreadMessages(
                currentUserId,
                partnerId,
                Sort.by(Sort.Direction.ASC, "timestamp")
        ));

        List<Message> broadcastFromPartner = messageRepository.findBySenderIdAndReceiverIdIsNullOrderByTimestampAsc(partnerId);
        thread.addAll(broadcastFromPartner);
        thread.sort(Comparator.comparing(Message::getTimestamp));

        return thread;
    }

    public Message sendMessage(String currentUserId, SendMessageRequest request) {
        if (request == null || !StringUtils.hasText(request.getContent())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le contenu du message est obligatoire");
        }

        User sender = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expediteur introuvable"));

        String receiverId = request.getReceiverId();
        if (!StringUtils.hasText(receiverId)) {
            if (!ROLE_CONTROLEUR.equals(sender.getRole())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul un controleur peut envoyer une diffusion");
            }
            receiverId = null;
        } else {
            userRepository.findById(receiverId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destinataire introuvable"));
        }

        Message message = new Message();
        message.setSenderId(currentUserId);
        message.setReceiverId(receiverId);
        message.setContent(request.getContent().trim());
        message.setEmergency(request.isEmergency());
        message.setRead(false);
        message.setTimestamp(new Date());

        Message saved = messageRepository.save(message);

        if (saved.isEmergency()) {
            emergencyWebSocketHandler.pushEmergencyMessage(saved);
        }

        return saved;
    }

    public void markAsRead(String messageId, String currentUserId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message introuvable"));

        if (currentUserId.equals(message.getSenderId())) {
            return;
        }

        if (StringUtils.hasText(message.getReceiverId()) && !currentUserId.equals(message.getReceiverId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse");
        }

        if (message.isRead()) {
            return;
        }

        message.setRead(true);
        messageRepository.save(message);
    }

    private void applyConversationUpdate(Map<String, Map<String, Object>> conversations,
                                         Map<String, User> userCache,
                                         String partnerId,
                                         Message message,
                                         boolean unreadIncoming) {
        Map<String, Object> conversation = conversations.computeIfAbsent(partnerId, id -> {
            Map<String, Object> map = new HashMap<>();
            map.put("partnerId", id);
            map.put("partnerName", resolveUserName(id, userCache));
            map.put("lastMessage", "");
            map.put("lastTimestamp", new Date(0));
            map.put("unreadCount", 0);
            return map;
        });

        Date currentLast = (Date) conversation.get("lastTimestamp");
        Date msgTime = message.getTimestamp() != null ? message.getTimestamp() : new Date(0);

        if (currentLast == null || msgTime.after(currentLast)) {
            conversation.put("lastMessage", message.getContent());
            conversation.put("lastTimestamp", msgTime);
        }

        if (unreadIncoming) {
            Integer unread = (Integer) conversation.get("unreadCount");
            conversation.put("unreadCount", unread + 1);
        }
    }

    private String resolveUserName(String userId, Map<String, User> userCache) {
        User cached = userCache.get(userId);
        if (cached != null) {
            return cached.getName();
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "Utilisateur";
        }

        userCache.put(userId, user);
        return user.getName();
    }
}
