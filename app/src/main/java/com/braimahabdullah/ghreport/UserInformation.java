package com.braimahabdullah.ghreport;

/**
 * Created by Ibrahim-Abdullah on 12/2/2017.
 */

public class UserInformation {

    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private String Password;

    public UserInformation(String username, String firstname, String lastname, String email, String phoneNumber, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        Password = password;
    }

    public UserInformation(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

}
