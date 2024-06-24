package com.example.webserver;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;

@Getter
@Repository
@Slf4j
public class DataBaseModel {
    private Connection connection;
    private final Object dataBaseLock = new Object();
    private final String url = "jdbc:mariadb://localhost:3306/http";
    private final String user = "root";
    private final String password = "3353";

    @PostConstruct
    public void startConnection() {
        log.info("start connection!!");
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            log.info("connecting success");
        } catch (SQLException e) {
            log.error("<<DataBaseManger>>\n - sql err: " + e.getMessage() + "\n");
        }
    }
    public boolean putText(String key, String value) {
        String sql = "INSERT INTO texts (text_id, text_value) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);

            int effectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (effectedRows > 0) {
                log.info("A new row has been inserted successfully.");
                return true;
            } else {
                log.error("A new row insertion failed");
                return false;
            }
        } catch (SQLException e) {
            log.error("DataBase Manger: putText err: " + e.getMessage());
            return false;
        }
    }

    public boolean putImage(byte[] bytes) {
        String sql = "INSERT INTO images (img_bytes) values (?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBytes(1, bytes);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                log.info("Image successfully inserted.");
                return true;
            } else {
                log.info("No rows affected.");
                return false;
            }
        } catch (SQLException e) {
            log.error("Database Manager: putImage error: " + e.getMessage());
        }
        return false;
    }

    public byte[] getImage(int key) {
        String sql = "SELECT img_bytes FROM images WHERE img_num = (?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, key);

            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();
            if (resultSet.next()) {
                return resultSet.getBytes("img_bytes");
            }
            resultSet.close();
            return null;
        } catch (SQLException e) {
            System.out.println("DataBase Manger: getImage err: " + e.getMessage());
        }
        return  null;
    }

    public String getText(String key) {
        String sql = "SELECT text_value FROM texts WHERE text_id = (?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);

            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.close();

            if (resultSet.next()) {
                return resultSet.getString("text_value");
            }
            resultSet.close();

        } catch (SQLException e) {
            log.error("DataBase Manger: getText err: " + e.getMessage());
        }
        return null;
    }

    public HashMap<String, String> getTextAll() {
        // return Json
        String sql = "SELECT * FROM texts";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            HashMap<String, String > results = new HashMap<>();
            while (resultSet.next()) {
                String key = resultSet.getString("text_id");
                String value = resultSet.getString("text_value");
                results.put(key, value);
            }

            return results;
        } catch (SQLException e) {
            log.error("Database Manager: getTextAll error: " + e.getMessage());
        }
        return null;
    }


    public boolean deleteText(String key) {
        String sql = "DELETE FROM texts WHERE text_id = (?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);

            int effectedRows = preparedStatement.executeUpdate();
            preparedStatement.close();
            if (effectedRows > 0) {
                log.info("key: " + key + " row was deleted!");
                return true;
            } else {
                log.info("delete failed");
                return false;
            }
        } catch (SQLException e) {
            log.error("DataBase Manger: deleteText err: " + e.getMessage());
        }
        return false;
    }
}
