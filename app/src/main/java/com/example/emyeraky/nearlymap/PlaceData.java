package com.example.emyeraky.nearlymap;

public class PlaceData {
    private String formatted_phone_number;
    private String email,name;
    private String formatted_address;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setFormatted_phone_number(String phone) {
        formatted_phone_number = phone;
    }


    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
