package com.supervision.livraisons.seeder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.repository.DeliveryRepository;
import com.supervision.livraisons.repository.MessageRepository;
import com.supervision.livraisons.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DeliveryRepository deliveryRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      DeliveryRepository deliveryRepository,
                      MessageRepository messageRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.deliveryRepository = deliveryRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedIfEmpty();
    }

    @Transactional
    public boolean seedIfEmpty() {
        if (userRepository.count() > 0) {
            return false;
        }
        seedDemoData();
        return true;
    }

    @Transactional
    public void resetAndSeed() {
        messageRepository.deleteAll();
        deliveryRepository.deleteAll();
        userRepository.deleteAll();
        seedDemoData();
    }

    private void seedDemoData() {
        Date now = new Date();

        // ── Users ──────────────────────────────────────────────────────────────────
        User sana = new User(null, "Sana Mrad", "sana@livraison.com",
                passwordEncoder.encode("admin123"), "CONTROLEUR",
                "https://ui-avatars.com/api/?name=Sana+Mrad&background=1A73E8&color=fff", now);
        User ahmed = new User(null, "Ahmed Ben Ali", "ahmed@livraison.com",
                passwordEncoder.encode("livreur123"), "LIVREUR",
                "https://ui-avatars.com/api/?name=Ahmed+Ben+Ali&background=34A853&color=fff", now);
        User mohamed = new User(null, "Mohamed Kchaou", "mohamed@livraison.com",
                passwordEncoder.encode("livreur123"), "LIVREUR",
                "https://ui-avatars.com/api/?name=Mohamed+Kchaou&background=FBBC04&color=fff", now);
        User walid = new User(null, "Walid", "walid@livraison.com",
                passwordEncoder.encode("livreur123"), "LIVREUR",
                "https://ui-avatars.com/api/?name=Walid&background=EA4335&color=fff", now);

        sana   = userRepository.save(sana);
        ahmed  = userRepository.save(ahmed);
        mohamed = userRepository.save(mohamed);
        walid  = userRepository.save(walid);

        List<Delivery> deliveries = new ArrayList<>();

        // ── Ahmed – today ──────────────────────────────────────────────────────────
        deliveries.add(createDelivery("Mme Trabelsi", "+216 22 345 678", "12 Rue de la Liberte, Tunis",
                36.8190, 10.1658, "3x Colis standard", "Client prefere appel avant livraison",
                "EN_ATTENTE", null, ahmed.getId(), timeToday(9, 0)));
        deliveries.add(createDelivery("M. Ben Youssef", "+216 98 112 334", "45 Avenue Habib Bourguiba, Tunis",
                36.8008, 10.1802, "1x Document urgent", "Remettre a la reception",
                "EN_COURS", null, ahmed.getId(), timeToday(10, 30)));
        deliveries.add(createDelivery("Mme Jaziri", "+216 24 555 890", "22 Rue de Carthage, La Marsa",
                36.8782, 10.3247, "2x Colis fragile", "",
                "LIVRE", null, ahmed.getId(), timeToday(11, 0)));
        deliveries.add(createDelivery("M. Hammami", "+216 20 300 101", "7 Rue Ibn Khaldoun, Ariana",
                36.8625, 10.1956, "1x Electro-menager", "",
                "ECHOUE", "ABSENT", ahmed.getId(), timeToday(12, 15)));
        deliveries.add(createDelivery("Mme Khadraoui", "+216 55 777 222", "3 Rue de Paris, Tunis",
                36.8001, 10.1850, "5x Colis e-commerce", "Priorite moyenne",
                "EN_ATTENTE", null, ahmed.getId(), timeToday(14, 0)));
        deliveries.add(createDelivery("M. Mansour", "+216 29 431 650", "18 Rue de l'Industrie, Ben Arous",
                36.7538, 10.2270, "1x Piece automobile", "Contact via WhatsApp",
                "LIVRE", null, ahmed.getId(), timeToday(15, 30)));
        deliveries.add(createDelivery("Mme Chebbi", "+216 27 654 321", "5 Rue de Kairouan, Tunis",
                36.8145, 10.1701, "4x Colis textile", "",
                "EN_COURS", null, ahmed.getId(), timeToday(16, 0)));
        deliveries.add(createDelivery("M. Dridi", "+216 21 908 777", "90 Rue El Jazira, Tunis",
                36.8054, 10.1722, "1x Colis premium", "Reception avant 18h",
                "EN_ATTENTE", null, ahmed.getId(), timeToday(17, 20)));

        // ── Ahmed – tomorrow ───────────────────────────────────────────────────────
        deliveries.add(createDelivery("M. Tlili", "+216 22 111 333", "14 Rue Bab El Khadra, Tunis",
                36.8220, 10.1680, "2x Colis standard", "",
                "EN_ATTENTE", null, ahmed.getId(), timeTomorrow(9, 0)));
        deliveries.add(createDelivery("Mme Laabidi", "+216 98 500 612", "8 Avenue Mohamed V, Tunis",
                36.8100, 10.1750, "1x Ordinateur portable", "Fragile",
                "EN_ATTENTE", null, ahmed.getId(), timeTomorrow(10, 30)));
        deliveries.add(createDelivery("M. Touati", "+216 24 321 654", "35 Rue des Jasmins, La Soukra",
                36.8640, 10.1980, "3x Colis textile", "",
                "EN_ATTENTE", null, ahmed.getId(), timeTomorrow(12, 0)));
        deliveries.add(createDelivery("Mme Belhaj", "+216 55 432 111", "10 Rue Tahar Haddad, Tunis",
                36.8175, 10.1720, "1x Smartphone", "Verification piece d'identite",
                "EN_ATTENTE", null, ahmed.getId(), timeTomorrow(14, 0)));
        deliveries.add(createDelivery("M. Chakroun", "+216 29 765 432", "22 Rue Marseille, Tunis",
                36.8050, 10.1780, "4x Colis alimentaire", "Livraison urgente",
                "EN_ATTENTE", null, ahmed.getId(), timeTomorrow(16, 30)));

        // ── Mohamed – today ────────────────────────────────────────────────────────
        deliveries.add(createDelivery("Mme Saidi", "+216 23 109 450", "11 Rue Mongi Slim, Sousse",
                35.8256, 10.6369, "2x Colis standard", "",
                "EN_ATTENTE", null, mohamed.getId(), timeToday(9, 45)));
        deliveries.add(createDelivery("M. Tounsi", "+216 94 245 991", "33 Avenue Yasser Arafet, Sfax",
                34.7406, 10.7603, "1x Equipement medical", "Urgent",
                "LIVRE", null, mohamed.getId(), timeToday(11, 20)));
        deliveries.add(createDelivery("Mme Feki", "+216 28 000 444", "64 Rue Farhat Hached, Nabeul",
                36.4510, 10.7350, "3x Colis alimentaire", "",
                "ECHOUE", "REFUS", mohamed.getId(), timeToday(13, 10)));
        deliveries.add(createDelivery("M. Ayadi", "+216 50 100 990", "8 Rue Ali Belhouane, Hammamet",
                36.4000, 10.6167, "1x Smartphone", "Verification piece d'identite",
                "EN_COURS", null, mohamed.getId(), timeToday(15, 0)));
        deliveries.add(createDelivery("Mme Baccar", "+216 26 876 532", "17 Rue de Monastir, Monastir",
                35.7643, 10.8113, "2x Colis fragile", "",
                "EN_ATTENTE", null, mohamed.getId(), timeToday(17, 0)));

        // ── Mohamed – tomorrow ─────────────────────────────────────────────────────
        deliveries.add(createDelivery("Mme Kooli", "+216 22 300 500", "12 Avenue Bourguiba, Monastir",
                35.7760, 10.8130, "3x Colis standard", "",
                "EN_ATTENTE", null, mohamed.getId(), timeTomorrow(9, 30)));
        deliveries.add(createDelivery("M. Jelassi", "+216 98 700 120", "45 Route de Sfax, Sfax",
                34.7320, 10.7600, "1x Equipement bureau", "",
                "EN_ATTENTE", null, mohamed.getId(), timeTomorrow(11, 0)));
        deliveries.add(createDelivery("Mme Zribi", "+216 24 450 780", "18 Avenue des Roses, Hammamet",
                36.4010, 10.6180, "2x Colis fragile", "Fragile, manipuler avec soin",
                "EN_ATTENTE", null, mohamed.getId(), timeTomorrow(13, 0)));
        deliveries.add(createDelivery("M. Haddad", "+216 55 210 330", "7 Rue des Martyrs, Sousse",
                35.8246, 10.6350, "1x Colis medical", "Urgent",
                "EN_ATTENTE", null, mohamed.getId(), timeTomorrow(15, 0)));
        deliveries.add(createDelivery("Mme Ghanem", "+216 29 510 870", "33 Rue Ibn Sina, Monastir",
                35.7750, 10.8250, "4x Colis textile", "",
                "EN_ATTENTE", null, mohamed.getId(), timeTomorrow(17, 0)));

        // ── Walid – today ──────────────────────────────────────────────────────────
        deliveries.add(createDelivery("Mme Hammami", "+216 22 678 901", "15 Route de la Marsa, Tunis",
                36.8520, 10.2180, "2x Colis standard", "Sonner deux fois",
                "EN_ATTENTE", null, walid.getId(), timeToday(9, 30)));
        deliveries.add(createDelivery("M. Ben Salem", "+216 98 234 567", "7 Rue de France, Tunis",
                36.8120, 10.1800, "1x Document urgent", "Remettre en mains propres",
                "EN_ATTENTE", null, walid.getId(), timeToday(10, 45)));
        deliveries.add(createDelivery("Mme Dridi", "+216 24 890 123", "22 Avenue des Arts, Ennasr",
                36.8420, 10.2070, "3x Colis e-commerce", "Priorite haute",
                "EN_ATTENTE", null, walid.getId(), timeToday(12, 0)));
        deliveries.add(createDelivery("M. Guermazi", "+216 20 456 789", "8 Rue du Lac, Lac 2",
                36.8250, 10.2370, "1x Electro-menager", "Livraison lourde, aide necessaire",
                "EN_ATTENTE", null, walid.getId(), timeToday(14, 30)));
        deliveries.add(createDelivery("Mme Boughanmi", "+216 55 012 345", "5 Rue Principale, Riadh El Andalous",
                36.8640, 10.2450, "2x Colis fragile", "Fragile",
                "EN_ATTENTE", null, walid.getId(), timeToday(16, 0)));

        deliveryRepository.saveAll(deliveries);

        // ── Messages ───────────────────────────────────────────────────────────────
        List<Message> messages = List.of(
                createMessage(sana.getId(), ahmed.getId(), "Bonjour Ahmed, tout va bien?", false, false, minusMinutes(90)),
                createMessage(ahmed.getId(), sana.getId(), "Bonjour Sana, oui tout est en ordre.", false, true, minusMinutes(80)),
                createMessage(sana.getId(), ahmed.getId(), "N'oublie pas la livraison prioritaire de 14h.", false, false, minusMinutes(55)),
                createMessage(sana.getId(), mohamed.getId(), "Mohamed, pense a confirmer les echecs.", false, false, minusMinutes(40)),
                createMessage(sana.getId(), null, "ALERTE: verification immediate des adresses suspectes.", true, false, minusMinutes(20))
        );
        messageRepository.saveAll(messages);
    }

    private Delivery createDelivery(String clientName, String clientPhone, String address,
                                    double lat, double lng, String orderDetails, String notes,
                                    String status, String failureReason,
                                    String assignedLivreurId, Date scheduledTime) {
        Date now = new Date();
        Delivery delivery = new Delivery();
        delivery.setClientName(clientName);
        delivery.setClientPhone(clientPhone);
        delivery.setAddress(address);
        delivery.setLat(lat);
        delivery.setLng(lng);
        delivery.setOrderDetails(orderDetails);
        delivery.setNotes(notes);
        delivery.setStatus(status);
        delivery.setFailureReason(failureReason);
        delivery.setAssignedLivreurId(assignedLivreurId);
        delivery.setScheduledTime(scheduledTime);
        delivery.setCreatedAt(now);
        delivery.setUpdatedAt(now);
        return delivery;
    }

    private Message createMessage(String senderId, String receiverId, String content,
                                  boolean emergency, boolean read, Date timestamp) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setEmergency(emergency);
        message.setRead(read);
        message.setTimestamp(timestamp);
        return message;
    }

    private Date timeToday(int hour, int minute) {
        LocalDateTime dt = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date timeTomorrow(int hour, int minute) {
        LocalDateTime dt = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(hour, minute));
        return Date.from(dt.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date minusMinutes(int minutes) {
        return Date.from(LocalDateTime.now().minusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
    }
}
