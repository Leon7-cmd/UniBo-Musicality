package it.unibo.musicality.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.model.Lyrics;
import javafx.collections.ObservableList;

public class LyricsDAO {

    public static boolean insertLyrics(Lyrics lyrics) {
        boolean result = false;
        String sql = "INSERT INTO testo (testo, fk_contenuto) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lyrics.getText());
            ps.setInt(2, lyrics.getIdContent());
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ObservableList<Lyrics> searchNotReviewedLyrics() {
        String sql = "SELECT * FROM testo WHERE fk_revisione IS NULL";
        ObservableList<Lyrics> idList = javafx.collections.FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            var rs = ps.executeQuery();
            while (rs.next()) {
                int idLyrics = rs.getInt("id_testo");
                String text = rs.getString("testo");
                int idContent = rs.getInt("fk_contenuto");
                Lyrics lyrics = new Lyrics(idLyrics, text, idContent);
                idList.add(lyrics);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }

    public static String getLyricsByContentId(String id) {
        String result = null;
        String sql = "SELECT testo FROM testo WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("testo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean approveLyrics(int id, String adminCode) {
        boolean result = false;
        String sql = "UPDATE testo SET fk_revisione = ? WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminCode);
            ps.setInt(2, id);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteLyrics(String idContent) {
        String sql = "DELETE FROM testo WHERE id_testo = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idContent);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLyricsApproved(String id){
        boolean result = false;
        String sql = "SELECT fk_revisione FROM testo WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            var rs = ps.executeQuery();
            result = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void updateLyrics(String text, String idContent) {
        String sql = "UPDATE testo SET testo = ? WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, text);
            ps.setString(2, idContent);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}