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

        User sana = new User(null, "Sana Mrad", "sana@livraison.com",
                passwordEncoder.encode("admin123"), "CONTROLEUR",
                "https://ui-avatars.com/api/?name=Sana+Mrad&background=1A73E8&color=fff", now);
        User ahmed = new User(null, "Ahmed Ben Ali", "ahmed@livraison.com",
                passwordEncoder.encode("livreur123"), "LIVREUR",
                "https://ui-avatars.com/api/?name=Ahmed+Ben+Ali&background=34A853&color=fff", now);
        User mohamed = new User(null, "Mohamed Kchaou", "mohamed@livraison.com",
                passwordEncoder.encode("livreur123"), "LIVREUR",
                "https://ui-avatars.com/api/?name=Mohamed+Kchaou&background=FBBC04&color=fff", now);

        sana = userRepository.save(sana);
        ahmed = userRepository.save(ahmed);
        mohamed = userRepository.save(mohamed);

        List<Delivery> deliveries = new ArrayList<>();

        deliveries.add(createDelivery("Mme Trabelsi", "+216 22 345 678", "12 Rue de la Liberte, Tunis", 36.8190, 10.1658,
                "3x Colis standard", "Client prefere appel avant livraison", "EN_ATTENTE", null,
                ahmed.getId(), timeToday(9, 0)));
        deliveries.add(createDelivery("M. Ben Youssef", "+216 98 112 334", "45 Avenue Habib Bourguiba, Tunis", 36.8008, 10.1802,
                "1x Document urgent", "Remettre a la reception", "EN_COURS", null,
                ahmed.getId(), timeToday(10, 30)));
        deliveries.add(createDelivery("Mme Jaziri", "+216 24 555 890", "22 Rue de Carthage, La Marsa", 36.8782, 10.3247,
                "2x Colis fragile", "", "LIVRE", null,
                ahmed.getId(), timeToday(11, 0)));
        deliveries.add(createDelivery("M. Hammami", "+216 20 300 101", "7 Rue Ibn Khaldoun, Ariana", 36.8625, 10.1956,
                "1x Electro-menager", "", "ECHOUE", "ABSENT",
                ahmed.getId(), timeToday(12, 15)));
        deliveries.add(createDelivery("Mme Khadraoui", "+216 55 777 222", "3 Rue de Paris, Tunis", 36.8001, 10.1850,
                "5x Colis e-commerce", "Priorite moyenne", "EN_ATTENTE", null,
                ahmed.getId(), timeToday(14, 0)));
        deliveries.add(createDelivery("M. Mansour", "+216 29 431 650", "18 Rue de l'Industrie, Ben Arous", 36.7538, 10.2270,
                "1x Piece automobile", "Contact via WhatsApp", "LIVRE", null,
                ahmed.getId(), timeToday(15, 30)));
        deliveries.add(createDelivery("Mme Chebbi", "+216 27 654 321", "5 Rue de Kairouan, Tunis", 36.8145, 10.1701,
                "4x Colis textile", "", "EN_COURS", null,
                ahmed.getId(), timeToday(16, 0)));
        deliveries.add(createDelivery("M. Dridi", "+216 21 908 777", "90 Rue El Jazira, Tunis", 36.8054, 10.1722,
                "1x Colis premium", "Reception avant 18h", "EN_ATTENTE", null,
                ahmed.getId(), timeToday(17, 20)));

        deliveries.add(createDelivery("Mme Saidi", "+216 23 109 450", "11 Rue Mongi Slim, Sousse", 35.8256, 10.6369,
                "2x Colis standard", "", "EN_ATTENTE", null,
                mohamed.getId(), timeToday(9, 45)));
        deliveries.add(createDelivery("M. Tounsi", "+216 94 245 991", "33 Avenue Yasser Arafet, Sfax", 34.7406, 10.7603,
                "1x Equipement medical", "Urgent", "LIVRE", null,
                mohamed.getId(), timeToday(11, 20)));
        deliveries.add(createDelivery("Mme Feki", "+216 28 000 444", "64 Rue Farhat Hached, Nabeul", 36.4510, 10.7350,
                "3x Colis alimentaire", "", "ECHOUE", "REFUS",
                mohamed.getId(), timeToday(13, 10)));
        deliveries.add(createDelivery("M. Ayadi", "+216 50 100 990", "8 Rue Ali Belhouane, Hammamet", 36.4000, 10.6167,
                "1x Smartphone", "Verification piece d'identite", "EN_COURS", null,
                mohamed.getId(), timeToday(15, 0)));
        deliveries.add(createDelivery("Mme Baccar", "+216 26 876 532", "17 Rue de Monastir, Monastir", 35.7643, 10.8113,
                "2x Colis fragile", "", "EN_ATTENTE", null,
                mohamed.getId(), timeToday(17, 0)));

        deliveryRepository.saveAll(deliveries);

        List<Message> messages = List.of(
                createMessage(sana.getId(), ahmed.getId(), "Bonjour Ahmed, tout va bien?", false, false, minusMinutes(90)),
                createMessage(ahmed.getId(), sana.getId(), "Bonjour Sana, oui tout est en ordre.", false, true, minusMinutes(80)),
                createMessage(sana.getId(), ahmed.getId(), "N'oublie pas la livraison prioritaire de 14h.", false, false, minusMinutes(55)),
                createMessage(sana.getId(), mohamed.getId(), "Mohamed, pense a confirmer les echecs.", false, false, minusMinutes(40)),
                createMessage(sana.getId(), ahmed.getId(), "ALERTE: verification immediate de l'adresse de M. Hammami.", true, false, minusMinutes(20))
        );

                messageRepository.saveAll(messages);
    }

    private Delivery createDelivery(String clientName,
                                    String clientPhone,
                                    String address,
                                    double lat,
                                    double lng,
                                    String orderDetails,
                                    String notes,
                                    String status,
                                    String failureReason,
                                    String assignedLivreurId,
                                    Date scheduledTime) {
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

    private Message createMessage(String senderId,
                                  String receiverId,
                                  String content,
                                  boolean emergency,
                                  boolean read,
                                  Date timestamp) {
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
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, minute));
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Date minusMinutes(int minutes) {
        return Date.from(LocalDateTime.now().minusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
    }
}
