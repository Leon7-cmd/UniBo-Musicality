package it.unibo.musicality.dao;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.model.User;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static boolean createUser(User u) {
        boolean result = false;
        String hashedPwd = BCrypt.hashpw(u.getPassword(), BCrypt.gensalt());
        String sql = "INSERT INTO utente (email, nome_utente, password, nome, cognome, tipo_utente) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getUsername());
            ps.setString(3, hashedPwd);
            ps.setString(4, u.getName());
            ps.setString(5, u.getSurname());
            ps.setString(6, u.getUserType());
            int rows = ps.executeUpdate();
            result = rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean isUserAdmin(String email, String adminCode) {
        boolean result = false;
        String sql = "SELECT codice_amministratore FROM utente WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = adminCode.equals(rs.getString("codice_amministratore"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean createAdmin(String email, String adminCode) {
        boolean result = false;
        String sql = "UPDATE utente SET codice_amministratore = ? WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminCode);
            ps.setString(2, email);
            int rows = ps.executeUpdate();
            result = rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getNotAdminUsers(String excludeEmail) {
        List<String> emails = new ArrayList<>();
        String sql = "SELECT email FROM utente WHERE codice_amministratore IS NULL OR codice_amministratore = ''";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String newEmail = rs.getString("email");
                if(!newEmail.equals(excludeEmail)){
                    emails.add(newEmail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    public static boolean checkPassword(String email, String password) {
        boolean result = false;
        String sql = "SELECT password FROM utente WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbPass = rs.getString("password");
                    result = BCrypt.checkpw(password, dbPass);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean existsByEmail(String email) {
        boolean result = false;
        String sql = "SELECT 1 FROM utente WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                result = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static User getByEmail(String email) {
        User u = new User();
        String sql = "SELECT * FROM utente WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u.setEmail(rs.getString("email"));
                    u.setUsername(rs.getString("nome_utente"));
                    u.setPassword(rs.getString("password"));
                    u.setName(rs.getString("nome"));
                    u.setSurname(rs.getString("cognome"));
                    u.setAdminCode(rs.getString("codice_amministratore"));
                    u.setUserType(rs.getString("tipo_utente"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public static boolean userIsBlocked(String selectedUser) {
        boolean result = false;
        String sql = "SELECT blocco_utente FROM utente WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, selectedUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbCodice = rs.getString("blocco_utente");
                    result = dbCodice != null && !dbCodice.isEmpty();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;   
    }

    public static boolean blockUser(String selectedUser, String adminCode) {
        boolean result = false;
        String sql = "UPDATE utente SET blocco_utente = ? WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminCode);
            ps.setString(2, selectedUser);
            int rows = ps.executeUpdate();
            result = rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean unlockUser(String selectedUser) {
        boolean result = false;
        String sql = "UPDATE utente SET blocco_utente = NULL WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, selectedUser);
            int rows = ps.executeUpdate();
            result = rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void changeUserProfileType(String email) {
        String sql = "UPDATE utente SET tipo_utente = CASE WHEN tipo_utente = 'ascoltatore' THEN 'autore' ELSE 'ascoltatore' END WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean updateProfile(String email, String newUsername) {
        boolean result = false;
        String sql = "UPDATE utente SET nome_utente = ? WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newUsername);
            ps.setString(2, email);
            int rows = ps.executeUpdate();
            result = rows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}