package com.example.ashen.csmanager.models;

public class PurchaseOrder
{
    private String vehicle;
    private String vehicleNumber;
    private String fuelStation;
    private String fuelType;;
    private String litres;
    private String price;
    private String enteredBy;
    private String customer;
    private String createdAt;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String vehicle, String vehicleNumber, String fuelType, String litres, String price, String enteredBy, String customer, String createdAt, String fuelStation) {
        this.vehicle = vehicle;
        this.vehicleNumber = vehicleNumber;
        this.fuelStation = fuelStation;
        this.fuelType = fuelType;
        this.litres = litres;
        this.price = price;
        this.enteredBy = enteredBy;
        this.customer = customer;
        this.createdAt = createdAt;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getFuelStation() {
        return fuelStation;
    }

    public void setFuelStation(String fuelStation) {
        this.fuelStation = fuelStation;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getLitres() {
        return litres;
    }

    public void setLitres(String litres) {
        this.litres = litres;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
