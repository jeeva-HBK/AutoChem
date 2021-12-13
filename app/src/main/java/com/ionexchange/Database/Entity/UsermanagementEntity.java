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
    public String userRole;

    @ColumnInfo(name = "user_password")
    public String userPassword;

    @ColumnInfo(name = "contact")
    public String contact;

    @ColumnInfo(name = "lOGINSTATUS")
    public String loginStatus;


    public UsermanagementEntity(String userId, String userName, String userRole, String userPassword, String contact, String loginStatus) {
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
        this.contact = contact;
        this.loginStatus = loginStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
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

    public String getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        this.loginStatus = loginStatus;
    }
}
