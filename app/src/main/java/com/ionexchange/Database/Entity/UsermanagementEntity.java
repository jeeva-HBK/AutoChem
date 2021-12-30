package com.ionexchange.Database.Entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"user_id"})
public class UsermanagementEntity {
    @NonNull
    @ColumnInfo(name = "user_id")
    public String userId;

    @ColumnInfo(name = "user_name")
    public String userName;

    @ColumnInfo(name = "user_role")
    public int userRole;

    @ColumnInfo(name = "user_password")
    public String userPassword;

    @ColumnInfo(name = "contact")
    public String contact;

    @ColumnInfo(name = "userUpdated")
    public String userUpdated;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "lOGINSTATUS")
    public String loginStatus;

    public UsermanagementEntity(@NonNull String userId, String userName, int userRole, String userPassword,
                                String contact, String userUpdated, String time, String loginStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
        this.contact = contact;
        this.userUpdated = userUpdated;
        this.time = time;
        this.loginStatus = loginStatus;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(String userUpdated) {
        this.userUpdated = userUpdated;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }
}