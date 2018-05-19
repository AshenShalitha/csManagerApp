package com.example.ashen.csmanager.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class FuelStation
{
    private String _id;
    private String name;
    private String phone;;
    List<String> customers;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int _v;

    public Address address;

    public FuelStation() {
        customers = new ArrayList<String>();
        address = new Address();
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getCustomers() {
        return customers;
    }

    public void setCustomers(List<String> customers) {
        this.customers = customers;
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
