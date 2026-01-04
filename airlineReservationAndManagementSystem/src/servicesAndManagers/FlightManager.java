package servicesAndManagers;

import java.io.*;
import java.util.ArrayList;
import java.time.LocalDateTime;

import flightManagement.Flight;

public class FlightManager {
	//Creating new flights, updating/deleting existing flights.
	private ArrayList<Flight> flights;
	private final String FILE_NAME = "flights.txt";
	
	public FlightManager() {
		this.flights = new ArrayList<>();
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

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(!line.trim().isEmpty()) {
                    Flight f = Flight.fromFileFormat(line);
                    flights.add(f);
                }
            }
        } catch (IOException e) {
            System.out.println("Dosya okuma hatası: " + e.getMessage());
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
	
	public boolean updateFlight(Flight flight) {
		
		if (flight == null || flight.getFlightNum() == null || flights == null) {
	        return false;
	    }
		
		for(Flight aFlight: flights) {
			if(aFlight != null && aFlight.getFlightNum().equals(flight.getFlightNum())) {
				aFlight.setDate(flight.getDate());
				aFlight.setDuration(flight.getDuration());
				saveFlights();
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
