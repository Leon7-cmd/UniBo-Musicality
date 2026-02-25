package it.unibo.musicality.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.util.Session;

public class ReviewDAO {
    public static boolean insertSingleReview(String evalType, int grade, MultimediaContent contentReviewed) {
        boolean result = false;
        String sql = "INSERT INTO valutazione (fk_nome_valutazione, fk_tipo_valutazione, voto, fk_utente_valutazione, data, fk_contenuto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, evalType);
            ps.setString(2, contentReviewed.getType());
            ps.setInt(3, grade);
            ps.setString(4, Session.getUtente().getEmail());
            ps.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setString(6, contentReviewed.getId());
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void deleteUserReviewByContent(String idContent, String user){
        String sql = "DELETE FROM valutazione WHERE fk_contenuto = ? AND fk_utente_valutazione = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idContent);
            ps.setString(2, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean insertReview(Map<String, Integer> reviewMap, MultimediaContent content) {
        boolean result = true;
        for (String evalType : reviewMap.keySet()) {
            result = result ? insertSingleReview(evalType, reviewMap.get(evalType), content) : false;
        }
        if (!result) {
            deleteUserReviewByContent(content.getId(), Session.getUtente().getEmail());
        }
        return result;
    }

    public static boolean hasUserReviewedContent(String user, String contentId){
        boolean result = false;
        String sql = "SELECT * FROM valutazione WHERE fk_utente_valutazione = ? AND fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, contentId);
            try (ResultSet rs = ps.executeQuery()) { 
                result = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getReviewCategoryByType(String contentType){
        List<String> names = new ArrayList<>();
        String sql = "SELECT nome FROM tipo_valutazione WHERE tipo_contenuto_multimediale = ?";
        try (Connection conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contentType); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    names.add(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }   

    public static void removeReviewOfContent(String id){
        String sql = "DELETE FROM valutazione WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}