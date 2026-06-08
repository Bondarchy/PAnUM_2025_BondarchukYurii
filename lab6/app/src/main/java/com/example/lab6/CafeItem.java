package com.example.lab6;

public class CafeItem {

    public int id;
    public String type;
    public String name;
    public String description;
    public String address;
    public String openingHours;
    public int imageId;
    public double price;

    @Override
    public String toString() {
        return name;
    }
}