package com.example.asus.penabuk.Model;

public class Address {
    private Integer id;
    private Integer district_id;
    private String zip_code;
    private String address_line;
    private String district;
    private String city;
    private String province;
    private boolean is_primary;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(Integer district_id) {
        this.district_id = district_id;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getAddress_line() {
        return address_line;
    }

    public void setAddress_line(String address_line) {
        this.address_line = address_line;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public boolean isIs_primary() {
        return is_primary;
    }

    public void setIs_primary(boolean is_primary) {
        this.is_primary = is_primary;
    }

    @Override
    public String toString(){
        return this.address_line+" "+this.district+" "+this.city+" "+this.zip_code+" "+this.province;
    }
}
