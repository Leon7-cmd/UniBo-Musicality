package it.unibo.musicality.dao;

import it.unibo.musicality.database.Database;
import it.unibo.musicality.model.MultimediaContent;
import it.unibo.musicality.model.Playlist;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlaylistDAO {

    public static boolean createPlaylist(String name, String description, String visibility, String email) {
        boolean result = false;
        String sql = "INSERT INTO playlist (nome_playlist, descrizione, visibilita, fk_utente) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, visibility);
            pstmt.setString(4, email);
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ObservableList<String> getUserPlaylists(String email) {
        ObservableList<String> playlists = javafx.collections.FXCollections.observableArrayList();
        String sql = "SELECT nome_playlist FROM playlist WHERE fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    playlists.add(rs.getString("nome_playlist"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlists;
    }

    public static ObservableList<MultimediaContent> getPlaylistContent(String playlistName, String email) {
        ObservableList<MultimediaContent> contents = javafx.collections.FXCollections.observableArrayList();
        String sql = "SELECT * FROM contenuto_multimediale cm " +
                     "JOIN playlist_contenuto pc ON cm.id_contenuto = pc.fk_contenuto " +
                     "JOIN playlist p ON pc.fk_playlist = p.id_playlist " +
                     "WHERE p.nome_playlist = ? AND fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistName);
            pstmt.setString(2, email);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MultimediaContent content = new MultimediaContent(
                        rs.getString("id_contenuto"), 
                        rs.getString("nome_contenuto"), 
                        rs.getString("tipo_contenuto"), 
                        rs.getString("descrizione"), 
                        rs.getString("fk_autore"), 
                        rs.getString("file_path"));
                    contents.add(content);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static void addContentToPlaylist(String value, String item, String email, String authorEmail) {
        String sql = "INSERT INTO playlist_contenuto (fk_playlist, fk_contenuto) " +
                     "VALUES ((SELECT id_playlist FROM playlist WHERE nome_playlist = ? AND fk_utente = ?), " +
                     "(SELECT id_contenuto FROM contenuto_multimediale WHERE nome_contenuto = ? AND fk_autore = ?))";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setString(2, email);
            pstmt.setString(3, item);
            pstmt.setString(4, authorEmail);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeContentFromPlaylist(String playlistName, String contentName, String email, String authorEmail) {
        String sql = "DELETE FROM playlist_contenuto WHERE fk_playlist = " +
                     "(SELECT id_playlist FROM playlist WHERE nome_playlist = ? AND fk_utente = ?) " +
                     "AND fk_contenuto = (SELECT id_contenuto FROM contenuto_multimediale WHERE nome_contenuto = ? AND fk_autore = ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlistName);
            pstmt.setString(2, email);
            pstmt.setString(3, contentName);
            pstmt.setString(4, authorEmail);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPlaylistDescription(String name, String email) {
        String result = null;
        String sql = "SELECT descrizione FROM playlist WHERE nome_playlist = ? AND fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("descrizione");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPlaylistNameById(String id) {
        String result = null;
        String sql = "SELECT nome_playlist FROM playlist WHERE id_playlist = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("nome_playlist");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPlaylistAuthorById(String id){
        String result = null;
        String sql = "SELECT fk_utente FROM playlist WHERE id_playlist = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("fk_utente");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int numberOfPlaylists(String email){
        String sql = "SELECT nome_playlist FROM playlist WHERE fk_utente = ?";
        int totalPlaylist = 0;
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (var rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    totalPlaylist++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalPlaylist;
    }

    public static ObservableList<Playlist> getAllPublicPlaylists(){
        ObservableList<Playlist> contents = javafx.collections.FXCollections.observableArrayList();
        String sql = "SELECT * FROM playlist WHERE visibilita = ?";
        try (Connection conn = Database.getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "pubblica");
            try (var rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id_playlist");
                String name = rs.getString("nome_playlist");
                String description = rs.getString("descrizione");
                String owner = rs.getString("fk_utente");
                Playlist content = new Playlist(id, name, description, true, owner); 
                contents.add(content);
            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contents;
    }

    public static void removeFromPlaylist(String idContent){
        String sql = "DELETE FROM playlist_contenuto WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idContent);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deletePlaylist(String selectedPlaylist, String email) {
        boolean result = false;
        String sql = "DELETE FROM playlist WHERE nome_playlist = ? AND fk_utente = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selectedPlaylist);
            pstmt.setString(2, email);
            int rowsAffected = pstmt.executeUpdate();
            result = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}