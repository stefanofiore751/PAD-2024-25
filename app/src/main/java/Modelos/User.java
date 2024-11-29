package Modelos;

import java.util.List;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String address;
    private String city;
    private String hashedPassword;
    private List<String> reservedEvents;

    // Constructor vacío requerido por Firebase
    public User() {
    }

    public User(String id, String fullName, String email, String address, String hashedPassword, String city, List<String> reservedEvents) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.hashedPassword = hashedPassword;
        this.city = city;
        this.reservedEvents = reservedEvents;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // Getters y Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<String> getReservedEvents() {
        return reservedEvents;
    }

    public void setReservedEvents(List<String> reservedEvents) {
        this.reservedEvents = reservedEvents;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
