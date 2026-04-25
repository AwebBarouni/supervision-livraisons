package com.supervision.livraisons.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.supervision.livraisons.dto.DeliveryStatsResponse;
import com.supervision.livraisons.dto.UpdateStatusRequest;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.repository.DeliveryRepository;

@Service
public class DeliveryService {

    private static final String ROLE_CONTROLEUR = "CONTROLEUR";
    private static final String ROLE_LIVREUR = "LIVREUR";

    private static final String STATUS_EN_ATTENTE = "EN_ATTENTE";
    private static final String STATUS_EN_COURS = "EN_COURS";
    private static final String STATUS_LIVRE = "LIVRE";
    private static final String STATUS_ECHOUE = "ECHOUE";

    private static final Set<String> ALLOWED_STATUS = Set.of(
            STATUS_EN_ATTENTE,
            STATUS_EN_COURS,
            STATUS_LIVRE,
            STATUS_ECHOUE
    );

    private final DeliveryRepository deliveryRepository;

    public DeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    public List<Delivery> getDeliveriesForUser(String userId, String role) {
        if (ROLE_CONTROLEUR.equals(role)) {
            return deliveryRepository.findAllByOrderByScheduledTimeAsc();
        }
        return deliveryRepository.findByAssignedLivreurIdOrderByScheduledTimeAsc(userId);
    }

    public Delivery createDelivery(String role, Delivery request) {
        ensureController(role);

        if (request == null || !StringUtils.hasText(request.getClientName()) || !StringUtils.hasText(request.getAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les informations de livraison sont incompletes");
        }

        Delivery delivery = new Delivery();
        delivery.setClientName(request.getClientName().trim());
        delivery.setClientPhone(request.getClientPhone());
        delivery.setAddress(request.getAddress().trim());
        delivery.setLat(request.getLat());
        delivery.setLng(request.getLng());
        delivery.setOrderDetails(request.getOrderDetails());
        delivery.setNotes(request.getNotes());
        delivery.setAssignedLivreurId(request.getAssignedLivreurId());
        delivery.setScheduledTime(request.getScheduledTime());

        String status = StringUtils.hasText(request.getStatus())
                ? request.getStatus().trim().toUpperCase()
                : STATUS_EN_ATTENTE;
        if (!ALLOWED_STATUS.contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide");
        }
        delivery.setStatus(status);
        delivery.setFailureReason(STATUS_ECHOUE.equals(status) ? request.getFailureReason() : null);

        Date now = new Date();
        delivery.setCreatedAt(now);
        delivery.setUpdatedAt(now);

        return deliveryRepository.save(delivery);
    }

    public Delivery updateDelivery(String deliveryId, String role, Delivery request) {
        ensureController(role);

        Delivery existing = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable"));

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corps de requete invalide");
        }

        if (StringUtils.hasText(request.getClientName())) {
            existing.setClientName(request.getClientName().trim());
        }
        if (StringUtils.hasText(request.getClientPhone())) {
            existing.setClientPhone(request.getClientPhone().trim());
        }
        if (StringUtils.hasText(request.getAddress())) {
            existing.setAddress(request.getAddress().trim());
        }
        if (StringUtils.hasText(request.getOrderDetails())) {
            existing.setOrderDetails(request.getOrderDetails().trim());
        }

        existing.setLat(request.getLat() != null ? request.getLat() : existing.getLat());
        existing.setLng(request.getLng() != null ? request.getLng() : existing.getLng());
        existing.setNotes(request.getNotes() != null ? request.getNotes().trim() : existing.getNotes());
        existing.setAssignedLivreurId(request.getAssignedLivreurId() != null ? request.getAssignedLivreurId() : existing.getAssignedLivreurId());
        existing.setScheduledTime(request.getScheduledTime() != null ? request.getScheduledTime() : existing.getScheduledTime());

        if (StringUtils.hasText(request.getStatus())) {
            String status = request.getStatus().trim().toUpperCase();
            if (!ALLOWED_STATUS.contains(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide");
            }
            existing.setStatus(status);
            if (STATUS_ECHOUE.equals(status)) {
                existing.setFailureReason(request.getFailureReason());
            } else {
                existing.setFailureReason(null);
            }
        }

        existing.setUpdatedAt(new Date());
        return deliveryRepository.save(existing);
    }

    public void deleteDelivery(String deliveryId, String role) {
        ensureController(role);

        Delivery existing = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable"));
        deliveryRepository.delete(existing);
    }

    public List<Delivery> getTodayDeliveries(String userId, String role) {
        if (!ROLE_LIVREUR.equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve aux livreurs");
        }

        LocalDate today = LocalDate.now();
        Date start = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return deliveryRepository.findByAssignedLivreurIdAndScheduledTimeBetweenOrderByScheduledTimeAsc(userId, start, end);
    }

    public Delivery getDeliveryById(String deliveryId, String userId, String role) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livraison introuvable"));

        ensureAccess(delivery, userId, role);
        return delivery;
    }

    public Delivery updateStatus(String deliveryId, String userId, String role, UpdateStatusRequest request) {
        if (request == null || !StringUtils.hasText(request.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le statut est obligatoire");
        }

        String normalizedStatus = request.getStatus().trim().toUpperCase();
        if (!ALLOWED_STATUS.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Statut invalide");
        }

        Delivery delivery = getDeliveryById(deliveryId, userId, role);
        delivery.setStatus(normalizedStatus);
        delivery.setUpdatedAt(new Date());

        if (STATUS_ECHOUE.equals(normalizedStatus)) {
            String reason = StringUtils.hasText(request.getFailureReason())
                    ? request.getFailureReason().trim().toUpperCase()
                    : "AUTRE";
            delivery.setFailureReason(reason);
        } else {
            delivery.setFailureReason(null);
        }

        if (StringUtils.hasText(request.getNotes())) {
            delivery.setNotes(request.getNotes().trim());
        }

        return deliveryRepository.save(delivery);
    }

    public DeliveryStatsResponse getStats(String userId, String role) {
        List<Delivery> deliveries = getDeliveriesForUser(userId, role);

        int delivered = 0;
        int inProgress = 0;
        int failed = 0;

        for (Delivery delivery : deliveries) {
            String status = delivery.getStatus();
            if (STATUS_LIVRE.equals(status)) {
                delivered++;
            } else if (STATUS_EN_COURS.equals(status)) {
                inProgress++;
            } else if (STATUS_ECHOUE.equals(status)) {
                failed++;
            }
        }

        return new DeliveryStatsResponse(deliveries.size(), delivered, inProgress, failed);
    }

    private void ensureController(String role) {
        if (!ROLE_CONTROLEUR.equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces reserve au controleur");
        }
    }

    private void ensureAccess(Delivery delivery, String userId, String role) {
        if (ROLE_CONTROLEUR.equals(role)) {
            return;
        }

        if (!ROLE_LIVREUR.equals(role) || !userId.equals(delivery.getAssignedLivreurId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces refuse pour cette livraison");
        }
    }
}
