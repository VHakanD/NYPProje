package servicesAndManagers;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import flightManagement.Flight;
import flightManagement.Plane;
import flightManagement.Seat;
import reservationAndTicketing.Passenger;
import reservationAndTicketing.Reservation;
import reservationAndTicketing.Ticket;

public class ReservationManager {
	//Making and canceling reservations. Concurrency will apply here.
	private List<Reservation> reservations;
    private FlightManager flightManager; 
    private List<Passenger> passengers;
    private CalculatePrice priceCalculator;
    
    private final ReservationIO ioHandler = new ReservationIO();
    
    public ReservationManager(FlightManager flightManager, List<Passenger> passengers) {
        this.flightManager = flightManager;
        this.passengers = passengers;
        this.reservations = new ArrayList<>();
        this.priceCalculator = new CalculatePrice();
        ioHandler.loadReservations();
    }
    
    private class ReservationIO {
        private final String RES_FILE = "reservations.txt";
        private final String TICKET_FILE = "tickets.txt";
        private final String PASSENGER_FILE = "passengers.txt";

        
        public void loadReservations() {
            try (BufferedReader reader = new BufferedReader(new FileReader(RES_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        Reservation r = Reservation.fromFileFormat(line, flightManager, passengers);
                        if (r != null) {
                            reservations.add(r);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Rezervasyon yükleme hatası: " + e.getMessage());
            }   
        }

        public void saveReservations() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RES_FILE))) {
                for (Reservation r : reservations) {
                    writer.write(r.toFileFormat());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Rezervasyon yazma hatası: " + e.getMessage());
            }
        }

        public void appendTicket(Ticket ticket) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(TICKET_FILE, true))) { 
                writer.write(ticket.toFileFormat());
                writer.newLine();
            } catch (IOException e) {
                System.out.println("Bilet kaydedilemedi: " + e.getMessage());
            }
        }

        public void appendPassengerIfNew(Passenger passenger) {
            boolean exists = passengers.stream().anyMatch(p -> p.getPassengerID().equals(passenger.getPassengerID()));
            
            if (!exists) {
                passengers.add(passenger);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSENGER_FILE, true))) {
                    writer.write(passenger.toFileFormat()); // Passenger.toFileFormat() kullanılması daha temiz
                    writer.newLine();
                    System.out.println("Yeni yolcu sisteme kaydedildi: " + passenger.getName());
                } catch (IOException e) {
                    System.out.println("Yolcu kayıt hatası: " + e.getMessage());
                }
            }
        }

        public void updateTicketSeatInFile(String resCode, String newSeatNum) {
            File file = new File(TICKET_FILE);
            if (!file.exists()) return;

            List<String> lines = new ArrayList<>();
            boolean updated = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6 && parts[3].equals(resCode)) {
                        parts[5] = newSeatNum; 
                        String newLine = String.join(",", parts); 
                        lines.add(newLine);
                        updated = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Bilet güncelleme okuma hatası: " + e.getMessage());
                return;
            }

            if (updated) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.out.println("Bilet güncelleme yazma hatası: " + e.getMessage());
                }
            }
        }
    }
    
    public Reservation findReservationByCode(String reservationCode) {
    	int i = 0;
        boolean found = false;
        Reservation foundRes = null;
        
        while(i < reservations.size() && !found) {
        	if(reservations.get(i) != null && reservations.get(i).getReservationCode().equals(reservationCode)) {
        		foundRes = reservations.get(i);
        		found = true;
        	}
        	i++;
        }
        
        return foundRes;
    }
    
    public ArrayList<Reservation> getReservationsByPassenger(String passengerID){
    	ArrayList<Reservation> passengersReservations = new ArrayList<>();
    	
    	for(Reservation res: reservations) {
    		if(res != null && res.getPassenger().getPassengerID().equals(passengerID)) {
    			passengersReservations.add(res);
    		}
    	}
    	
    	return passengersReservations;
    }
    
    public ArrayList<Reservation> getReservationsByFlight(String flightNum){
    	ArrayList<Reservation> flightsReservations = new ArrayList<>();
    	
    	for(Reservation res: reservations) {
    		if(res != null && res.getFlight().getFlightNum().equals(flightNum)) {
    			flightsReservations.add(res);
    		}
    	}
    	
    	return flightsReservations;
    }
    
    public double calculateOccupancyRate(Flight flight) {
        if (flight == null || flight.getPlane() == null) return 0.0;

        int totalCapacity = flight.getPlane().getCapacity();
        if (totalCapacity == 0) return 0.0;

        long occupiedCount = reservations.stream()
                .filter(r -> r.getFlight().getFlightNum().equals(flight.getFlightNum()))
                .count();

        return (double) occupiedCount / totalCapacity * 100.0;
    }
    
    public boolean hasPassengerAlreadyBooked(String flightNum, String passengerID) {
    	boolean found = false;
        int i = 0;
        
        while(i < reservations.size() && !found) {
        	if(reservations.get(i) != null && 
        			reservations.get(i).getFlight().getFlightNum().equals(flightNum) &&
        			reservations.get(i).getPassenger().getPassengerID().equals(passengerID)) {
        		found = true;
        	}
        	i++;
        }
    	
    	return found;
    }
    
    public ArrayList<Reservation> getReservationsByBooker(String bookerID){
        ArrayList<Reservation> bookerReservations = new ArrayList<>();
        for(Reservation res: reservations) {
            if(res != null && res.getBookerID().equals(bookerID)) {
                bookerReservations.add(res);
            }
        }
        return bookerReservations;
    }
    
    public synchronized boolean makeReservation(Plane plane, Flight flight, Passenger passenger, String seatNum, String bookerID) {
    	ioHandler.appendPassengerIfNew(passenger);
    	
    	if (!plane.hasSeat(seatNum)) {
            System.out.println("Hata: Böyle bir koltuk yok: " + seatNum);
            return false;
        }
    	
    	Seat seat = plane.getSeatMatrix().get(seatNum);
    	
    	if (seat == null) {
    	    System.out.println("Hata: Böyle bir koltuk uçakta mevcut değil: " + seatNum);
    	    return false;
    	}
    	
    	if (seat.isReserved()) {
    	    System.out.println("Hata: Koltuk zaten başkası tarafından alınmış: " + seatNum);
    	    return false;
    	}
    	
    	String resCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    	Reservation newRes = new Reservation(resCode, flight, passenger, seat, bookerID);
    	
        reservations.add(newRes);
        seat.setReserveStatus(true);
        
        ioHandler.saveReservations();
        
        System.out.println("Rezervasyon Başarılı! PNR: " + resCode);
        return true; 	
    }
    
    public boolean makeReservationUnsafe(Flight flight, Passenger passenger, String seatNum) {        
        Plane plane = flight.getPlane();
        Seat seat = plane.getSeatMatrix().get(seatNum);
        
        if (seat == null || seat.isReserved()) {
            return false;
        }
        
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        String resCode = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Reservation newRes = new Reservation(resCode, flight, passenger, seat);
        
        reservations.add(newRes);
        seat.setReserveStatus(true);
        
        ioHandler.saveReservations();
        
        return true;
    }
    
    public boolean makeReservation(Plane plane, Flight flight, Passenger passenger, String seatNum) {
        return makeReservation(plane, flight, passenger, seatNum, passenger.getPassengerID());
    }
    
    
    public boolean changeSeat(String resCode, String newSeatNum) {
    	Reservation res = findReservationByCode(resCode);
        if (res == null) return false;
        
        Plane plane = res.getFlight().getPlane();
        Seat newSeat = plane.getSeatMatrix().get(newSeatNum);

        if (newSeat == null || newSeat.isReserved()) {
            System.out.println("Yeni koltuk müsait değil.");
            return false;
        }

        res.getSeat().setReserveStatus(false);
        
        newSeat.setReserveStatus(true);
        
        res.setSeat(newSeat);

        ioHandler.saveReservations();
        ioHandler.updateTicketSeatInFile(resCode, newSeatNum);
        return true;
    }
    
    public boolean cancelReservation(String reservationCode) {
    	Reservation targetRes = this.findReservationByCode(reservationCode);
    	
        if (targetRes == null) {
            System.out.println("Hata: Rezervasyon bulunamadı.");
            return false;
        }
        
        LocalDateTime flightDate = targetRes.getFlight().getDate(); 
        LocalDateTime now = LocalDateTime.now();
        
        long hoursUntilFlight = ChronoUnit.HOURS.between(now, flightDate);
        
        if(hoursUntilFlight < 0) {
        	System.out.println("Hata: Uçuş zaten gerçekleşmiş!" +
                    "İptal işlemi yapılamaz.");
        	return false;
        }
        
        if (hoursUntilFlight < 24) {
            System.out.println("Hata: Uçuşa " + hoursUntilFlight + " saat kaldı. " +
                               "Son 24 saat kala iptal işlemi yapılamaz.");
            return false;
        }
        
        Seat seat = targetRes.getSeat();
        if (seat != null) {
            seat.setReserveStatus(false);
        }
        
        reservations.remove(targetRes);
        
        ioHandler.saveReservations();
        
        System.out.println("Başarılı: Rezervasyon iptal edildi. Ücret iadeniz bankanıza bağlı olarak "
        		+ "1-7 iş günü içide hesabınıza yansıyacaktır...");
        return true;
    }
    
    
    public Ticket generateTicket(Reservation res) {

    	double basePrice = priceCalculator.calculateTicketPrice(res);

        Ticket ticket = new Ticket(res, basePrice);
        
        double totalPayment = priceCalculator.calculateTotalPayment(ticket);
        ticket.setPrice(totalPayment);

        ioHandler.appendTicket(ticket);

        return ticket;

    } 
    
    public void cancelReservationsByFlightID(String flightNum) {
        boolean removed = false;
        for (int i = reservations.size() - 1; i >= 0; i--) {
            Reservation r = reservations.get(i);
            if (r.getFlight().getFlightNum().equals(flightNum)) {
                if (r.getSeat() != null) {
                    r.getSeat().setReserveStatus(false);
                }
                reservations.remove(i);
                removed = true;
            }
        }
        if (removed) {
            ioHandler.saveReservations();
            System.out.println(flightNum + " uçuşuna ait tüm rezervasyonlar silindi.");
        }
    }

    
    public void cleanUpOrphanReservations() {
        boolean removed = false;
        ArrayList<Flight> activeFlights = flightManager.getFlights();
        
        for (int i = reservations.size() - 1; i >= 0; i--) {
            Reservation r = reservations.get(i);
            boolean flightStillExists = false;
            
            int j = 0;
            while (j < activeFlights.size() && !flightStillExists) {
                Flight f = activeFlights.get(j);
                
                if (f.getFlightNum().equals(r.getFlight().getFlightNum())) {
                    flightStillExists = true;
                }
                j++;
            }
            
            if (!flightStillExists) {
                reservations.remove(i);
                removed = true;
            }
        }
        
        if (removed) {
            ioHandler.saveReservations();
            System.out.println("Uçuşu silinmiş rezervasyonlar temizlendi.");
        }
    }

}
