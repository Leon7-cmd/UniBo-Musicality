package it.unibo.musicality.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.model.MultimediaContent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContentDAO {
    public static boolean insertContent(MultimediaContent content) {
        boolean result = false;
        String sql = "INSERT INTO contenuto_multimediale (nome_contenuto, tipo_contenuto, descrizione, fk_autore, file_path) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content.getName());
            ps.setString(2, content.getType());
            ps.setString(3, content.getDescription());
            ps.setString(4, content.getEmail());
            ps.setString(5, content.getFile());
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int getContentId(MultimediaContent content) {
        int result = -1;
        String sql = "SELECT id_contenuto FROM contenuto_multimediale WHERE nome_contenuto = ? AND fk_autore = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content.getName());
            ps.setString(2, content.getEmail());
            var rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt("id_contenuto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getContentNameById(int id) {
        String result = null;
        String sql = "SELECT nome_contenuto FROM contenuto_multimediale WHERE id_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("nome_contenuto");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getAuthorEmailById(int id){
        String result = null;
        String sql = "SELECT fk_autore FROM contenuto_multimediale WHERE id_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("fk_autore");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ObservableList<MultimediaContent> getAllContentByTypeAndUser(String email, String type) {
        ObservableList<MultimediaContent> contents = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contenuto_multimediale WHERE fk_autore = ? AND tipo_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MultimediaContent c = new MultimediaContent(
                    rs.getString("id_contenuto"),
                    rs.getString("nome_contenuto"),
                    rs.getString("tipo_contenuto"),
                    rs.getString("descrizione"),
                    rs.getString("fk_autore"),
                    rs.getString("file_path")
                );
                contents.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static ObservableList<MultimediaContent> getAllContent() {
        ObservableList<MultimediaContent> contents = FXCollections.observableArrayList();
        String sql = "SELECT * FROM contenuto_multimediale";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MultimediaContent c = new MultimediaContent(
                    rs.getString("id_contenuto"),
                    rs.getString("nome_contenuto"),
                    rs.getString("tipo_contenuto"),
                    rs.getString("descrizione"),
                    rs.getString("fk_autore"),
                    rs.getString("file_path")
                );
                contents.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static MultimediaContent getContentById(String id){
        MultimediaContent content = new MultimediaContent();
        String sql = "SELECT * FROM contenuto_multimediale WHERE id_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                content = new MultimediaContent(
                    rs.getString("id_contenuto"),
                    rs.getString("nome_contenuto"),
                    rs.getString("tipo_contenuto"),
                    rs.getString("descrizione"),
                    rs.getString("fk_autore"),
                    rs.getString("file_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void deleteContent(String authorEmail, String contentName){
        String sql = "SELECT id_contenuto FROM contenuto_multimediale WHERE nome_contenuto = ? AND fk_autore = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentName);
            ps.setString(2, authorEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { 
                    String id = rs.getString("id_contenuto");
                    delete(id);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void delete(String id){
        String sql = "DELETE FROM contenuto_multimediale WHERE id_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}