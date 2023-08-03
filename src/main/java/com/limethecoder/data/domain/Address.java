package com.limethecoder.data.domain;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Address {
    private String zip;
    @NotNull
    @NotEmpty
    @Size(min = 3, max=50)
    private String country;
    private String city;
    private String street;
    private String building;

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (!zip.equals(address.zip)) return false;
        if (!country.equals(address.country)) return false;
        if (!city.equals(address.city)) return false;
        if (!street.equals(address.street)) return false;
        return building.equals(address.building);
    }

    @Override
    public int hashCode() {
        int result = zip.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + street.hashCode();
        result = 31 * result + building.hashCode();
        return result;
    }
}
