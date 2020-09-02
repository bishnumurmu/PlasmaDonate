package com.project.plasmadonate.authentication;

public class patientHelperActivity {

    String name, address, email, password, contact;

    public patientHelperActivity() {
        //for firebase
    }

    public patientHelperActivity(String name, String address, String email, String password, String contact) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.password = password;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

