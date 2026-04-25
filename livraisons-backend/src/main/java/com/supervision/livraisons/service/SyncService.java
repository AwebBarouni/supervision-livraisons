package com.supervision.livraisons.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.dto.StartDaySyncResponse;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.MessageRepository;

@Service
public class SyncService {

    private static final String ROLE_LIVREUR = "LIVREUR";

    private final UserService userService;
    private final DeliveryService deliveryService;
    private final MessageRepository messageRepository;

    public SyncService(UserService userService,
                       DeliveryService deliveryService,
                       MessageRepository messageRepository) {
        this.userService = userService;
        this.deliveryService = deliveryService;
        this.messageRepository = messageRepository;
    }

    public StartDaySyncResponse getStartDaySync(String userId, String role) {
        if (!ROLE_LIVREUR.equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve aux livreurs");
        }

        User me = userService.getCurrentUser(userId);
        List<Delivery> deliveries = deliveryService.getTodayDeliveries(userId, role);

        List<Message> mergedMessages = new ArrayList<>();
        mergedMessages.addAll(messageRepository.findDirectMessagesForUser(
                userId,
                Sort.by(Sort.Direction.ASC, "timestamp")
        ));
        mergedMessages.addAll(messageRepository.findByReceiverIdIsNullOrderByTimestampDesc());
        mergedMessages.sort(Comparator.comparing(Message::getTimestamp, Comparator.nullsLast(Date::compareTo)));

        return new StartDaySyncResponse(new Date(), me, deliveries, mergedMessages);
    }
}
