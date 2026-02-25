package it.unibo.musicality.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.musicality.database.Database;

public class TagDAO {
    public static boolean createTag(String tag) {
        boolean result = false;
        String sql = "INSERT INTO tag (nome_tag) VALUES (?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tag);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean linkTagToContent(int contentId, String tag) {
        boolean result = false;
        String sql = "INSERT INTO tag_contenuto (fk_contenuto, fk_tag) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            ps.setString(2, tag);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        String sql = "SELECT nome_tag FROM tag";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tags.add(rs.getString("nome_tag"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tags;
    }

    public static void removeTagFromAllContents(String tag) {
        String sql = "DELETE FROM tag_contenuto WHERE fk_tag = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tag);
            ps.executeUpdate();
            deleteTag(tag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTagsOfContent(String id){
        List<String> list = new ArrayList<String>();
        String sql = "SELECT fk_tag FROM tag_contenuto WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
             ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("fk_tag"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void deleteTag(String tag) {
        String sql = "DELETE FROM tag WHERE nome_tag = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tag);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTagFromContent(String id) {
        String sql = "DELETE FROM tag_contenuto WHERE fk_contenuto = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}