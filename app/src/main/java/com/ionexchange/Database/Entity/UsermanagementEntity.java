package com.ionexchange.Database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"user_id"})
public class UsermanagementEntity {

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "user_name")
    public String userName;

    @ColumnInfo(name = "user_role")
    public int userRole;

    @ColumnInfo(name = "user_password")
    public String userPassword;


    public UsermanagementEntity(int userId, String userName, int userRole, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.userPassword = userPassword;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
}
