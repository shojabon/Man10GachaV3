package com.shojabon.man10gachav3.ToolPackages;

import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Created by sho on 2018/06/29.
 */

enum DatabaseType{
    MYSQL,
    SQLITE,
    NONE,
}

public class DatabaseConnector {
    DatabaseType type = DatabaseType.NONE;
    Connection connection = null;
    Statement statement = null;

    public DatabaseConnector(File file){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getPath());
            type = DatabaseType.SQLITE;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DatabaseConnector(String host, int port, String userName, String password, String database){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, userName, password);
            type = DatabaseType.MYSQL;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean execute(String query){
        if(!connectable()){
            return false;
        }
        try {
            statement = connection.createStatement();
            boolean res = statement.execute(query);
            statement.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet query(String query){
        ResultSet rs = null;
        if(!connectable()){
            return null;
        }
        try {
            statement = connection.createStatement();
            statement.setQueryTimeout(10);
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public long executeGetId(String query){
        int key = -1;
        try {
            PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pstmt.executeUpdate();
            pstmt.setQueryTimeout(10);
            ResultSet keys = pstmt.getGeneratedKeys();
            keys.next();
            key = keys.getInt(1);
            keys.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return key;
    }

    public boolean connectable(){
        return connection != null;
    }
}
