package servicesAndManagers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import flightManagement.Flight;
import flightManagement.Plane;

public class FlightManager {
	//Creating new flights, updating/deleting existing flights.
	private ArrayList<Flight> flights;
	private List<Plane> availablePlanes;
	private final String FILE_NAME = "flights.txt";
	private final String PLANES_FILE = "planes.txt";
	
	public FlightManager() {
		this.flights = new ArrayList<>();
		this.availablePlanes = new ArrayList<>();
		loadPlanes();
		loadFlights();
	}
	
	public ArrayList<Flight> getFlights() {
		return flights;
	}

	private void loadFlights() {
		File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        
        SeatManager seatInit = new SeatManager();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    Flight f = Flight.fromFileFormat(line);
                    
                    f.getPlane().setCapacity(180); 
                    seatInit.seatingArrangements(f.getPlane());
                    
                    flights.add(f);
                }
            }
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
        }
	}
	
	public void loadPlanes() {
		File file = new File(PLANES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Plane.java'ya eklediğiniz fromFileFormat metodunu kullanıyoruz
                    Plane p = Plane.fromFileFormat(line);
                    if (p != null) {
                        availablePlanes.add(p);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Uçak listesi yüklenemedi: " + e.getMessage());
        }
	}
	
	private void saveFlights() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Flight f : flights) {
                writer.write(f.toFileFormat());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Dosya yazma hatası: " + e.getMessage());
        }
    }
	
	public void addFlight(Flight flight) {
		if(!(flights.contains(flight))) {
			flights.add(flight);
			System.out.println(flight.getFlightNum() + " numaralı uçuş sisteme eklendi");
		}else {
			System.out.println(flight.getFlightNum()
					+ " numaralı uçuş zaten var, ekleme yapılmadı!");
		}
		saveFlights();
	}
	
	public void deleteFlight(Flight flight) {
		if(!(flights.contains(flight))) {
			flights.remove(flight);
			System.out.println(flight.getFlightNum() + " numaralı uçuş sistemden silindi");
		}else {
			System.out.println(flight.getFlightNum() + " numaralı uçuş bulunamadı!");
		}
		saveFlights();
	}
	
	public void listAllFlights() {
		System.out.println("Sistemdeki bütün uçuşlar:"
				+ "\n----------------------------------------------------------");
		for(Flight flight: flights) {
			System.out.println(flight.toString());
		}
	}
	
	public void searchFlight(Flight flight) {
		if(flights.contains(flight)) {
			System.out.println(flight.getFlightNum() + " numaralı uçuş sistemde bulundu."
					+ "\n-------------------" + flight.toString());
		}else {
			System.out.println("Numaralı uçuş sistemde bulunamadı!");
		}
	}
	
	public boolean updateFlight(Flight updatedFlight) {
	    // 1. Önce listedeki (RAM'deki) eski kaydı bulup yenisiyle değiştirelim
	    for (int i = 0; i < flights.size(); i++) {
	        if (flights.get(i).getFlightNum().equals(updatedFlight.getFlightNum())) {
	            
	            // Listeyi güncelle (Eski objeyi sil, yenisini aynı yere koy)
	            flights.set(i, updatedFlight);
	            
	            // 2. Şimdi dosyaya kaydet
	            saveFlights();
	            
	            return true; // İşlem başarılı
	        }
	    }
	    return false; // Uçuş bulunamadı
	}
	
	/*public boolean updateFlight(Flight flight) {
		
		if (flight == null || flight.getFlightNum() == null || flights == null) {
	        return false;
	    }
		
		for(Flight aFlight: flights) {
			if(aFlight != null && aFlight.getFlightNum().equals(flight.getFlightNum())) {
				aFlight.setDate(flight.getDate());
				aFlight.setDuration(flight.getDuration());
				aFlight.setRoute(flight.getRoute());
				if (flight.getRoute() != null) {
                    aFlight.setRoute(flight.getRoute());
                }
				if (flight.getPlane() != null) {
                    aFlight.setPlane(flight.getPlane());
                }
				
				flights.set(i, flight);
				saveFlights();
				return true;
			}
		}
		
		return false;
	}*/
	
	public Flight getFlightByID(String flightNum) {
		Flight searchedFlight = null;
		
		for(Flight flight: flights) {
			if(flight.getFlightNum().equalsIgnoreCase(flightNum)) {
				searchedFlight = flight;
			}
		}
		
		return searchedFlight;
	}
	
	public Plane getPlaneTemplateByID(String planeID) {
        for (Plane p : availablePlanes) {
            if (p.getPlaneID().equalsIgnoreCase(planeID)) {
                // Referansı değil, yeni bir kopyasını döndürüyoruz:
                return new Plane(p.getPlaneID(), p.getPlaneModel(), p.getCapacity());
            }
        }
        return null;
    }
	
	public ArrayList<Flight> flightsByDepartureCity(String city){
		ArrayList<Flight> filteredFlights = new ArrayList<>();
		if(flights == null)
			return null;
		
		for(Flight flight: flights) {
			if(flight != null && flight.getRoute().getDepartureCity().equals(city)) {
				filteredFlights.add(flight);
			}
		}
		
		return filteredFlights;
	}
	
	//JUnit için removeExpiredFlights metodu tanımlanmalı
	public void removeExpiredFlights() {
	    LocalDateTime now = LocalDateTime.now();
	    //Tarihi geçenleri siler.
	    flights.removeIf(flight -> flight.getDate().isBefore(now));
	    
	    // Dosyayı günceller.
	    saveFlights(); 
	    System.out.println("Tarihi geçen uçuşlar temizlendi.");
	}
	/*calculateTotalOccupancyRate asenkron raporlama için bu tarz bir metot eklememiz lazım sanırım
	 tüm uçuşlar için doluluk oranını hesaplayan bir metod*/
	
	/*GUI için uçuşları ucuzdan-pahalıya, erken tarihliden ileri tarihliye şeklinde sıralama için
	 sortFlights diye bir metod ekleyebiliriz */
}
