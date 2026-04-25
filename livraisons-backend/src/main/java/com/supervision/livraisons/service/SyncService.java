package com.supervision.livraisons.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.dto.SyncUpdateStatusRequest;
import com.supervision.livraisons.dto.StartDaySyncResponse;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.DeliveryRepository;
import com.supervision.livraisons.repository.MessageRepository;

@Service
public class SyncService {

    private static final String ROLE_LIVREUR = "LIVREUR";
    private static final String ROLE_CONTROLEUR = "CONTROLEUR";
    private static final String STATUS_EN_ATTENTE = "EN_ATTENTE";
    private static final String STATUS_EN_COURS = "EN_COURS";
    private static final String STATUS_LIVRE = "LIVRE";
    private static final String STATUS_ECHOUE = "ECHOUE";

    private final UserService userService;
    private final DeliveryService deliveryService;
    private final DeliveryRepository deliveryRepository;
    private final MessageRepository messageRepository;

    public SyncService(UserService userService,
                       DeliveryService deliveryService,
                       DeliveryRepository deliveryRepository,
                       MessageRepository messageRepository) {
        this.userService = userService;
        this.deliveryService = deliveryService;
        this.deliveryRepository = deliveryRepository;
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

    public List<Delivery> getDailyDeliveries(String driverId, LocalDate date, String requesterId, String role) {
        if (!StringUtils.hasText(driverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le driverId est obligatoire");
        }

        if (ROLE_LIVREUR.equals(role) && !driverId.equals(requesterId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse pour ce livreur");
        }

        if (!ROLE_LIVREUR.equals(role) && !ROLE_CONTROLEUR.equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse");
        }

        LocalDate targetDate = date != null ? date : LocalDate.now();
        Date start = Date.from(targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(targetDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return deliveryRepository.findByAssignedLivreurIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(driverId, start, end);
    }

    public Delivery updateStatusFromSync(SyncUpdateStatusRequest request, String requesterId, String role) {
        if (request == null || !StringUtils.hasText(request.getDeliveryId()) || !StringUtils.hasText(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "deliveryId et status sont obligatoires");
        }

        Delivery delivery = deliveryRepository.findById(request.getDeliveryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable"));

        if (ROLE_LIVREUR.equals(role) && !requesterId.equals(delivery.getAssignedLivreurId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse pour cette livraison");
        }

        delivery.setStatus(normalizeIncomingStatus(request.getStatus()));
        delivery.setUpdatedAt(parseTimestampOrNow(request.getTimestamp()));

        if (STATUS_ECHOUE.equals(delivery.getStatus())) {
            delivery.setFailureReason("CLIENT_NOT_FOUND");
        } else {
            delivery.setFailureReason(null);
        }

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> emergencyClientSearch(String query) {
        if (!StringUtils.hasText(query)) {
            return List.of();
        }
        return deliveryRepository.findByClientNameOrClientPhoneLikeIgnoreCase(query.trim());
    }

    private String normalizeIncomingStatus(String status) {
        String normalized = status.trim().toLowerCase();
        switch (normalized) {
            case "done":
            case "livre":
                return STATUS_LIVRE;
            case "client_not_found":
            case "echoue":
                return STATUS_ECHOUE;
            case "pending":
                return STATUS_EN_ATTENTE;
            case "en_cours":
                return STATUS_EN_COURS;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide");
        }
    }

    private Date parseTimestampOrNow(String timestamp) {
        if (!StringUtils.hasText(timestamp)) {
            return new Date();
        }

        try {
            return Date.from(Instant.parse(timestamp));
        } catch (Exception ignored) {
            return new Date();
        }
    }
}
