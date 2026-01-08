import flightManagement.*;
import servicesAndManagers.*;
import reservationAndTicketing.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   HAVAYOLU REZERVASYON SİSTEMİ - ANA TEST MODU   ");
        System.out.println("=================================================\n");

        // ----------------------------------------------------------------
        // BÖLÜM 1: TEMEL MANTIK TESTLERİ
        // ----------------------------------------------------------------
        System.out.println(">>> BÖLÜM 1: İş Mantığı ve Fiyatlandırma Testleri...\n");
        runLogicTests();

        System.out.println("\n-------------------------------------------------\n");

        // ----------------------------------------------------------------
        // BÖLÜM 2: MULTI-THREADING SENARYOLARI
        // ----------------------------------------------------------------
        System.out.println(">>> BÖLÜM 2: Eşzamanlılık (Concurrency) Senaryoları");
        System.out.println("    Senaryo: 180 Koltuklu uçak, 90 Yolcu aynı anda giriş yapıyor.\n");

        // --- SENARYO A: SENKRONİZE OLMAYAN DURUM ---
        System.out.println("--- SENARYO A: Senkronize OLMAYAN (Unsafe) Test Başlıyor... ");
        System.out.println("    (Beklenen: Tutarsız veriler, çifte rezervasyonlar)");
        try {
            runUnsafeConcurrencyTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n-------------------------------------------------\n");

        // --- SENARYO B: SENKRONİZE (DOĞRU) DURUM ---
        System.out.println("--- SENARYO B: Senkronize (Safe) Test Başlıyor... [cite: 29]");
        System.out.println("    (Beklenen: Tam 90 rezervasyon, 0 hata)");
        try {
            runSafeConcurrencyTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n=================================================");
        System.out.println("   TÜM SİMÜLASYONLAR TAMAMLANDI   ");
        System.out.println("=================================================");
    }

    /**
     * Mantık Testleri: Fiyat hesaplama, koltuk oluşturma vb.
     */
    public static void runLogicTests() {
        //FlightManager flightManager = new FlightManager();
        CalculatePrice calculator = new CalculatePrice();
        SeatManager seatManager = new SeatManager();

        Route route = new Route("Istanbul", "Ankara", 450);
        Flight flight = new Flight("TK-LOGIC", route, LocalDateTime.now().plusDays(10), 60);
        
        // Fiyat testi için Business sırası oluşsun diye kapasiteyi 180 yapıyoruz
        Plane plane = new Plane("P-LOGIC", "Boeing 737", 180); 
        
        seatManager.seatingArrangements(plane);
        flight.setPlane(plane);
        
        // Test 1: Koltuk Sayısı
        int initialSeats = seatManager.availableSeatCount(plane);
        System.out.println("[TEST 1] Başlangıç Boş Koltuk Sayısı: " + initialSeats + " (Beklenen:" + plane.getCapacity() + ")");
        
        if (initialSeats == plane.getCapacity()) System.out.println("   -> BAŞARILI");
        else System.out.println("   -> BAŞARISIZ");

        // Test 2: Fiyat Hesaplama
        Passenger p = new Passenger("1", "Ali", "Yilmaz", "05554443322");
        Seat businessSeat = plane.getSeatMatrix().get("1A"); // Business
        Seat economySeat = plane.getSeatMatrix().get("10A"); // Economy

        Reservation resBus = new Reservation("R1", flight, p, businessSeat);
        Reservation resEco = new Reservation("R2", flight, p, economySeat);

        double busPrice = calculator.calculateTicketPrice(resBus);
        double ecoPrice = calculator.calculateTicketPrice(resEco);

        System.out.println("\n[TEST 2] Fiyat Kontrolü:");
        System.out.println("   Business Fiyat: " + busPrice + " TL");
        System.out.println("   Economy Fiyat : " + ecoPrice + " TL");
        
        if (busPrice > ecoPrice) System.out.println("   -> BAŞARILI (Business daha pahalı)");
        else System.out.println("   -> BAŞARISIZ (Mantık hatası var)");
    }

    /**
     * SENKRONİZE OLMAYAN TEST (UNSAFE)
     * makeReservationUnsafe metodunu kullanır. Thread.sleep içerdiği için çakışma riski yüksektir.
     */
    public static void runUnsafeConcurrencyTest() throws InterruptedException {
        // Setup
        FlightManager fm = new FlightManager();
        fm.getFlights().clear(); // Temizle
        
        Route route = new Route("Izmir", "Antalya", 400);
        Flight flight = new Flight("UNSAFE-01", route, LocalDateTime.now().plusDays(5), 50);
        Plane plane = new Plane("P-UNSAFE", "Airbus A320", 180); // 180 Koltuk [cite: 24]
        
        SeatManager sm = new SeatManager();
        sm.seatingArrangements(plane);
        flight.setPlane(plane);

        ReservationManager resManager = new ReservationManager(fm, new ArrayList<>());

        int numberOfThreads = 90; // 90 Yolcu [cite: 24]
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        System.out.println("Simülasyon: 90 Yolcu aynı anda 'makeReservationUnsafe' ile saldırıyor...");

        for (int i = 1; i <= numberOfThreads; i++) {
            final int userId = i;
            executor.submit(() -> {
                Passenger p = new Passenger("ID" + userId, "User", "" + userId, "555");
                
                // Unsafe modda rastgele bir koltuk seçip almaya çalışır
                // Not: Unsafe modda "döngü" kurmuyoruz, çünkü amacımız hatayı (aynı koltuğun iki kere satılmasını) görmek.
                // Eğer döngü kurarsak hatayı yakalamak zorlaşabilir, direkt saldırı yapıyoruz.
                String randomSeat = getRandomSeat(30);
                
                // KORUMASIZ METOT ÇAĞRISI
                boolean success = resManager.makeReservationUnsafe(flight, p, randomSeat);
                
                if (success) {
                    // Konsolu kirletmemek için yazdırmıyoruz, hatayı sonda sayacağız.
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // --- SONUÇ ANALİZİ ---
        // 1. ReservationManager listesindeki kayıt sayısı (Kaç kişiye "Tamam" dedik?)
        long totalReservations = resManager.getReservationsByFlight("UNSAFE-01").size();
        
        // 2. Plane içindeki gerçek 'isReserved' sayısı
        int physicallyOccupied = 0;
        for(Seat s : plane.getSeatMatrix().values()) {
            if(s.isReserved()) physicallyOccupied++;
        }

        System.out.println("[SONUÇ] Verilen Rezervasyon Onayı Sayısı : " + totalReservations);
        System.out.println("[SONUÇ] Fiziksel Olarak Dolu Koltuk      : " + physicallyOccupied);
        
        // Eğer ReservationManager 90 kişiye bilet kesti ama uçakta 85 koltuk doluysa -> Veri kaybı/hata var.
        // Veya aynı koltuğa 2 kişi atandıysa hata var.
        
        if (totalReservations != physicallyOccupied || totalReservations > physicallyOccupied) {
            System.out.println(">> TESPİT: Veri tutarsızlığı oluştu! (Race Condition Başarılı)");
            System.out.println("   Bazı koltuklar birden fazla kişiye satılmış veya veriler karışmış.");
        } else {
            System.out.println(">> Şans eseri hata oluşmadı (Tekrar deneyin).");
        }
    }

    /**
     * SENKRONİZE TEST (SAFE)
     * synchronized makeReservation metodunu kullanır.
     * Yolcular rastgele koltuk seçer, doluysa başkasını dener (Retry Logic).
     */
    public static void runSafeConcurrencyTest() throws InterruptedException {
        // Setup
        FlightManager fm = new FlightManager();
        fm.getFlights().clear();
        
        Route route = new Route("Ankara", "Van", 1200);
        Flight flight = new Flight("SAFE-01", route, LocalDateTime.now().plusDays(5), 120);
        Plane plane = new Plane("P-SAFE", "Boeing 737", 180); // 180 Koltuk
        
        SeatManager sm = new SeatManager();
        sm.seatingArrangements(plane);
        flight.setPlane(plane);

        ReservationManager resManager = new ReservationManager(fm, new ArrayList<>());
        
        int numberOfThreads = 90; // 90 Yolcu
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        System.out.println("Simülasyon: 90 Yolcu aynı anda 'synchronized makeReservation' ile yerleşiyor...");

        for (int i = 1; i <= numberOfThreads; i++) {
            final int userId = i;
            executor.submit(() -> {
                //String threadName = "Yolcu-" + userId;
                Passenger p = new Passenger("ID"+userId, "User", ""+userId, "555");
                
                boolean isBooked = false;
                int attempts = 0;
                
                // PDF Gereği: Rastgele seçim ve doluysa tekrar deneme [cite: 25]
                while (!isBooked) {
                    attempts++;
                    String randomSeat = getRandomSeat(30); 
                    
                    // GÜVENLİ (SYNCHRONIZED) METOT ÇAĞRISI
                    boolean success = resManager.makeReservation(plane, flight, p, randomSeat);
                    
                    if (success) {
                        isBooked = true;
                        // System.out.println(">>> " + threadName + ": " + randomSeat + " aldı. (Deneme: " + attempts + ")");
                    }
                }
                System.out.println(attempts + " denemede sonuca ulaşıldı.");
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // --- SONUÇ KONTROLÜ ---
        
        long resCount = resManager.getReservationsByFlight("SAFE-01").size();
        
        int availableSeats = sm.availableSeatCount(plane);
        int totalSeats = plane.getCapacity();
        int occupiedSeats = totalSeats - availableSeats;

        System.out.println("[İSTATİSTİK] Toplam Kapasite        : " + totalSeats);
        System.out.println("[İSTATİSTİK] Kalan Boş Koltuk       : " + availableSeats);
        System.out.println("[İSTATİSTİK] Toplam Yerleşen Yolcu  : " + occupiedSeats);
        System.out.println("[SONUÇ] Rezervasyon Listesi Boyutu  : " + resCount + " (Beklenen: 90)");

        if (occupiedSeats == 90 && resCount == 90) {
            System.out.println(">> TEST BAŞARILI: Tüm yolcular güvenli bir şekilde yerleşti.");
        } else {
            System.out.println(">> TEST BAŞARISIZ: Bazı yolcular açıkta kaldı!");
        }
    }
    
    // Rastgele koltuk ID üretici (Örn: "1A", "25F" vb.)
    public static String getRandomSeat(int totalRows) {
        Random rand = new Random();
        int row = rand.nextInt(totalRows) + 1; // 1 ile 30 arası
        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'};
        char col = cols[rand.nextInt(cols.length)];
        return row + String.valueOf(col);
    }
}