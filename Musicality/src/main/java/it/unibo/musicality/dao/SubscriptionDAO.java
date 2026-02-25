package it.unibo.musicality.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.util.Session;

public class SubscriptionDAO {
    public static List<String> getAllPlanTypes() {
        List<String> planTypes = new ArrayList<>();
        String sql = "SELECT id_tipo_abbonamento FROM tipo_abbonamento";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                planTypes.add(rs.getString("id_tipo_abbonamento"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planTypes;
    }

    public static double[] getPlanPrices(String planType) {
        double[] prices = new double[2];
        String sql = "SELECT prezzo, sconto FROM tipo_abbonamento WHERE id_tipo_abbonamento = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prices[0] = rs.getDouble("prezzo");
                    prices[1] = rs.getDouble("sconto");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prices;
    }

    public static boolean updatePlanPrices(String planType, double planPrice, double discountPrice) {
        boolean result = false;
        String sql = "UPDATE tipo_abbonamento SET prezzo = ?, sconto = ? WHERE id_tipo_abbonamento = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, planPrice);
            ps.setDouble(2, discountPrice);
            ps.setString(3, planType);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void changeUserPlan(String selectedPlan) {
        String sql = "UPDATE abbonamento SET fk_tipo_abbonamento = ? WHERE fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, selectedPlan);
            ps.setString(2, Session.getUtente().getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setDefaultPlan(String email) {
        String sql = "INSERT INTO abbonamento (dataAcquisto, fk_tipo_abbonamento, fk_utente) VALUES (?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            ps.setInt(2, 1);
            ps.setString(3, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getUserPlan(String email) {
        String planType = "-1"; // Default value if no subscription found
        String sql = "SELECT fk_tipo_abbonamento FROM abbonamento WHERE fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    planType = rs.getString("fk_tipo_abbonamento");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planType;
    }
}