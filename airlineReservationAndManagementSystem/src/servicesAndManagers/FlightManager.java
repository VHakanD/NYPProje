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
	
	private final FlightFileHandler fileHandler = new FlightFileHandler();
	
	public FlightManager() {
		this.flights = new ArrayList<>();
		this.availablePlanes = new ArrayList<>();
		fileHandler.loadPlanes();
        fileHandler.loadFlights();
	}
	
	private class FlightFileHandler {
        private final String FLIGHTS_FILE = "flights.txt";
        private final String PLANES_FILE = "planes.txt";

        public void loadFlights() {
            File file = new File(FLIGHTS_FILE);
            if (!file.exists()) return;
            
            SeatManager seatInit = new SeatManager();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if(!line.trim().isEmpty()) {
                        Flight f = Flight.fromFileFormat(line);
                        // Outer class'ın listesine erişebiliyoruz
                        f.getPlane().setCapacity(180); 
                        seatInit.seatingArrangements(f.getPlane());
                        flights.add(f);
                    }
                }
            } catch (IOException e) {
                System.out.println("Uçuş dosyası okuma hatası: " + e.getMessage());
            }
        }

        public void saveFlights() {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FLIGHTS_FILE))) {
                for (Flight f : flights) {
                    writer.write(f.toFileFormat());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Uçuş dosyası yazma hatası: " + e.getMessage());
            }
        }

        public void loadPlanes() {
            File file = new File(PLANES_FILE);
            if (!file.exists()) return;

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
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
    }
	
	public ArrayList<Flight> getFlights() {
		return flights;
	}

	public void addFlight(Flight flight) {
		if(!(flights.contains(flight))) {
			flights.add(flight);
			System.out.println(flight.getFlightNum() + " numaralı uçuş sisteme eklendi");
		}else {
			System.out.println(flight.getFlightNum()
					+ " numaralı uçuş zaten var, ekleme yapılmadı!");
		}
		fileHandler.saveFlights();
	}
	
	public void deleteFlight(Flight flight) {
		if (flight == null) return;
	    
	    Flight toRemove = null;
	    boolean found = false;
	    int i = 0;
	   
	    while (i < flights.size() && !found) {
	        Flight f = flights.get(i);
	        if (f.getFlightNum().equals(flight.getFlightNum())) {
	            toRemove = f;
	            found = true;
	        }
	        i++;
	    }
	    
	    if (found) {
	        flights.remove(toRemove);
	        fileHandler.saveFlights();
	        System.out.println("Uçuş başarıyla silindi ve kaydedildi: " + toRemove.getFlightNum());
	    } else {
	        System.out.println("Silinecek uçuş listede bulunamadı.");
	    }
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
	    for (int i = 0; i < flights.size(); i++) {
	        if (flights.get(i).getFlightNum().equals(updatedFlight.getFlightNum())) {
	            flights.set(i, updatedFlight);
	            
	            fileHandler.saveFlights();
	            
	            return true;
	        }
	    }
	    return false;
	}
	
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
	
	
	public void removeExpiredFlights() {
	    LocalDateTime now = LocalDateTime.now();
	    flights.removeIf(flight -> flight.getDate().isBefore(now));
	    
	    fileHandler.saveFlights(); 
	    System.out.println("Tarihi geçen uçuşlar temizlendi.");
	}
}
