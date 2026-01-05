package tests;

import flightManagement.*;
import servicesAndManagers.*;
import reservationAndTicketing.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestMain {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   HAVAYOLU SİSTEMİ - KONSOL TEST MODU   ");
        System.out.println("==========================================\n");

        // 1. TEMEL KURULUM VE JUNIT BENZERİ MANTIK TESTLERİ
        System.out.println(">>> 1. BÖLÜM: İş Mantığı (JUnit Simülasyonu) Testleri Başlıyor...\n");
        runLogicTests();

        System.out.println("\n------------------------------------------\n");

        // 2. MULTI-THREADING (CONCURRENCY) TESTLERİ
        System.out.println(">>> 2. BÖLÜM: Multi-Threading (Eşzamanlılık) Testleri Başlıyor...");
        System.out.println("   (90 Yolcu aynı anda rastgele koltuk kapmaya çalışacak!)\n");
        
        try {
            runConcurrencyTest();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("\n==========================================");
        System.out.println("   TÜM TESTLER TAMAMLANDI   ");
        System.out.println("==========================================");
    }

    /**
     * JUnit testlerindeki mantığı (Fiyat hesaplama, Koltuk düşme, Rota filtreleme) simüle eder.
     */
    public static void runLogicTests() {
        // --- Setup ---
        FlightManager flightManager = new FlightManager();
        CalculatePrice calculator = new CalculatePrice();
        SeatManager seatManager = new SeatManager();

        Route route = new Route("Istanbul", "Ankara", 450); // Kısa mesafe
        Flight flight = new Flight("TK-TEST", route, LocalDateTime.now().plusDays(10), 60);
        
        // Fiyat testi için Business sırası oluşsun diye kapasiteyi 180 yapıyoruz
        Plane plane = new Plane("P-TEST", "Boeing 737", 180); 
        
        // Koltukları oluştur
        seatManager.seatingArrangements(plane);
        flight.setPlane(plane);
        
        // --- Test 1: Koltuk Sayısı Kontrolü ---
        int initialSeats = seatManager.availableSeatCount(plane);
        System.out.println("[TEST 1] Başlangıç Boş Koltuk Sayısı: " + initialSeats + " (Beklenen:" + plane.getCapacity() + ")");
        
        if (initialSeats == plane.getCapacity()) System.out.println("   -> BAŞARILI");
        else System.out.println("   -> BAŞARISIZ");

        // --- Test 2: Fiyat Hesaplama ---
        Passenger p = new Passenger("1", "Ali", "Yilmaz", "05554443322");
        
        // DÜZELTME: Biri Business (1. Sıra), Biri Economy (10. Sıra) seçildi
        Seat businessSeat = plane.getSeatMatrix().get("1A"); 
        Seat economySeat = plane.getSeatMatrix().get("10A"); 

        Reservation resBus = new Reservation("R1", flight, p, businessSeat);
        Reservation resEco = new Reservation("R2", flight, p, economySeat);

        double busPrice = calculator.calculateTicketPrice(resBus);
        double ecoPrice = calculator.calculateTicketPrice(resEco);

        System.out.println("\n[TEST 2] Fiyat Kontrolü:");
        System.out.println("   Business Fiyat: " + busPrice);
        System.out.println("   Economy Fiyat : " + ecoPrice);
        
        if (busPrice > ecoPrice) System.out.println("   -> BAŞARILI (Business daha pahalı)");
        else System.out.println("   -> BAŞARISIZ (Mantık hatası var)");

        // --- Test 3: Rota Filtreleme ---
        flightManager.addFlight(flight);
        ArrayList<Flight> istFlights = flightManager.flightsByDepartureCity("Istanbul");
        
        System.out.println("\n[TEST 3] Rota Filtreleme (Kalkış: Istanbul):");
        boolean found = false;
        for (Flight f : istFlights) {
            if (f.getFlightNum().equals("TK-TEST")) found = true;
        }
        
        if (found) System.out.println("   -> BAŞARILI (Uçuş bulundu)");
        else System.out.println("   -> BAŞARISIZ");
    }

    /**
     * Multi-threading testi: Aynı koltuğa aynı anda birden fazla thread üzerinden rezervasyon yapmaya çalışır.
     */
    public static void runConcurrencyTest() throws InterruptedException {
        // --- Setup ---
        FlightManager fm = new FlightManager();
        // Dosyadan okumasın, temiz liste kullanalım
        fm.getFlights().clear(); 
        
        Route route = new Route("Izmir", "Antalya", 400);
        Flight flight = new Flight("MT-001", route, LocalDateTime.now().plusDays(5), 50);
        
        // 180 Kişilik Uçak (PDF Senaryosu Gereği)
        Plane plane = new Plane("PL-001", "Airbus A320", 180);
        
        SeatManager sm = new SeatManager();
        sm.seatingArrangements(plane); // Koltukları doldur (1A, 1B...)
        flight.setPlane(plane);

        ReservationManager resManager = new ReservationManager(fm, new ArrayList<>());
        
        // Thread Havuzu (90 adet eşzamanlı yolcu)
        int numberOfThreads = 90;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        System.out.println("Senaryo: " + numberOfThreads + " yolcu uçağa biniyor ve rastgele koltuk seçiyor...");
        System.out.println("Kullanılan Metot: synchronized makeReservation()");

        for (int i = 1; i <= numberOfThreads; i++) {
            final int userId = i;
            executor.submit(() -> {
                String threadName = "Yolcu-" + userId;
                Passenger p = new Passenger("ID"+userId, "User", ""+userId, "555");
                
                boolean isBooked = false;
                int attempts = 0;
                
                // Yolcu koltuk bulana kadar dener (PDF Senaryosu: Rastgele seçim)
                while (!isBooked) {
                    attempts++;
                    String randomSeat = getRandomSeat(30); // 30 Sıra var
                    
                    // NOT: Burada 'makeReservation' (güvenli) kullanıyoruz.
                    boolean success = resManager.makeReservation(plane, flight, p, randomSeat);
                    
                    if (success) {
                        isBooked = true;
                        System.out.println(">>> " + threadName + ": BAŞARILI! " + randomSeat + " koltuğunu aldı. (Deneme: " + attempts + ")");
                    } else {
                        // Konsol kirliliğini önlemek için burayı yorum satırı yapabilirsin
                        // System.out.println("--- " + threadName + ": " + randomSeat + " dolu, tekrar deniyor...");
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // --- Sonuç Kontrolü ve İstatistikler ---
        
        long resCount = resManager.getReservationsByFlight("MT-001").size();
        
        // Dolu koltuk sayısını SeatManager ile hesaplayalım
        int availableSeats = sm.availableSeatCount(plane);
        int totalSeats = plane.getCapacity();
        int occupiedSeats = totalSeats - availableSeats;

        System.out.println("\n------------------------------------------");
        System.out.println("[İSTATİSTİK] Toplam Kapasite        : " + totalSeats);
        System.out.println("[İSTATİSTİK] Kalan Boş Koltuk       : " + availableSeats);
        System.out.println("[İSTATİSTİK] Toplam Yerleşen Yolcu  : " + occupiedSeats);
        System.out.println("[SONUÇ] Rezervasyon Listesi Boyutu  : " + resCount + " (Beklenen: 90)");

        if (occupiedSeats == 90 && resCount == 90) {
            System.out.println(">> TEST BAŞARILI: Tüm yolcular rastgele koltuklara (Race Condition olmadan) yerleşti.");
        } else {
            System.out.println(">> TEST BAŞARISIZ: Bazı yolcular açıkta kaldı veya hesap hatası var!");
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