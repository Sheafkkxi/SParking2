package com.sheaf.sparking.beans;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by Sheaf on 2018/3/21.
 */

public class UserBean {

    private String nickName;
    private String UserName;
    private String carId;
    private String password;
    private String phoneNum;
    private String city;
    private LatLng tempPosition;

    public LatLng getTempPosition() {
        return tempPosition;
    }
    public void setTempPosition(LatLng tempPosition) {
        this.tempPosition = tempPosition;
    }
    public String getNickName() {
        return nickName;
    }

    public String getUserName() {
        return UserName;
    }

    public String getCarId() {
        return carId;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getCity() {
        return city;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
