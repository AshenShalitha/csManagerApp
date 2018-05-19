package com.example.ashen.csmanager.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Customer
{
    private String _id;
    private String name;
    private String type;;
    private String contactPersonName;
    private String phone;
    private int allocatedLimit;
    private int currentLimit;
    private List<String> fuelStationIds;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int _v;

    private Address address;

    public Customer() {
        fuelStationIds = new ArrayList<String>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAllocatedLimit() {
        return allocatedLimit;
    }

    public void setAllocatedLimit(int allocatedLimit) {
        this.allocatedLimit = allocatedLimit;
    }

    public int getCurrentLimit() {
        return currentLimit;
    }

    public void setCurrentLimit(int currentLimit) {
        this.currentLimit = currentLimit;
    }

    public List<String> getFuelStationIds() {
        return fuelStationIds;
    }

    public void setFuelStationIds(List<String> fuelStationIds) {
        this.fuelStationIds = fuelStationIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int get_v() {
        return _v;
    }

    public void set_v(int _v) {
        this._v = _v;
    }
}
