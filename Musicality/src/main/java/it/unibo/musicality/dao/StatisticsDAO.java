package it.unibo.musicality.dao;

import it.unibo.musicality.model.MultimediaContent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.unibo.musicality.database.Database;

public class StatisticsDAO {

    // List of "special" rankings that are not voting categories
    private static final List<String> SPECIAL_RANKINGS = List.of(
        "Newest", "Top Rated", "Most Reviewed", 
        "Top Songs", "Most Reviewed Songs", 
        "Top Podcasts", "Most Reviewed Podcasts", 
        "Recent Reviews"
    );

    /**
     * Returns the list of content based on the ranking type.
     * @param rankingType the type of ranking (e.g., "Top Rated" or a category like "Melodia")
     * @param limit maximum number of results
     * @return list of MultimediaContent
     */
    public static List<MultimediaContent> getRanking(String rankingType, int limit) {
        List<MultimediaContent> contents = new ArrayList<>();
        String query = getRankingQuery(rankingType);

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            if (SPECIAL_RANKINGS.contains(rankingType)) {
                ps.setInt(1, limit);
            } else {
                ps.setString(1, rankingType);
                ps.setInt(2, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MultimediaContent content = new MultimediaContent(
                            rs.getString("id_contenuto"),
                            rs.getString("nome_contenuto"),
                            rs.getString("descrizione"),
                            rs.getString("tipo_contenuto"),
                            rs.getString("fk_autore"),
                            rs.getString("file_path")
                    );
                    contents.add(content);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public static String getRankingQuery(String rankingType) {
        // Minimum vote to enter the ranking (e.g., 3 votes)
        int minVotes = 3; 

        return switch (rankingType) {
            case "Newest" -> 
                "SELECT * FROM contenuto_multimediale ORDER BY id_contenuto DESC LIMIT ?";
            
            case "Top Rated" -> 
                "SELECT c.*, AVG(v.voto) AS avg_score FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "GROUP BY c.id_contenuto " +
                "HAVING COUNT(v.id_valutazione) >= " + minVotes + " " +
                "ORDER BY avg_score DESC LIMIT ?";
                
            case "Most Reviewed" -> 
                "SELECT c.*, COUNT(v.id_valutazione) AS review_count FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "GROUP BY c.id_contenuto ORDER BY review_count DESC LIMIT ?";
            
            case "Top Songs" -> 
                "SELECT c.*, AVG(v.voto) AS avg_score FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "WHERE tipo_contenuto='canzone' " +
                "GROUP BY c.id_contenuto " +
                "HAVING COUNT(v.id_valutazione) >= " + minVotes + " " +
                "ORDER BY avg_score DESC LIMIT ?";

            case "Most Reviewed Songs" -> 
                "SELECT c.*, COUNT(v.id_valutazione) AS review_count FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "WHERE tipo_contenuto='canzone' " +
                "GROUP BY c.id_contenuto ORDER BY review_count DESC LIMIT ?";
            
            case "Top Podcasts" -> 
                "SELECT c.*, AVG(v.voto) AS avg_score FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "WHERE tipo_contenuto='podcast' " +
                "GROUP BY c.id_contenuto " +
                "HAVING COUNT(v.id_valutazione) >= " + minVotes + " " +
                "ORDER BY avg_score DESC LIMIT ?";

            case "Most Reviewed Podcasts" -> 
                "SELECT c.*, COUNT(v.id_valutazione) AS review_count FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "WHERE tipo_contenuto='podcast' " +
                "GROUP BY c.id_contenuto ORDER BY review_count DESC LIMIT ?";
                
            case "Recent Reviews" -> 
                "SELECT c.*, MAX(v.data) AS last_review FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "GROUP BY c.id_contenuto ORDER BY last_review DESC LIMIT ?";
            
            // DEFAULT CASE: Handles "Melodia", "Testo", "Voce" and any other future category.
            default -> 
                "SELECT c.*, AVG(v.voto) AS avg_cat FROM contenuto_multimediale c " +
                "LEFT JOIN valutazione v ON c.id_contenuto = v.fk_contenuto " +
                "AND v.fk_nome_valutazione = ? " + // Parameter 1: Category Name
                "GROUP BY c.id_contenuto " +
                "HAVING COUNT(v.id_valutazione) >= " + minVotes + " " + // Threshold filter
                "ORDER BY avg_cat DESC LIMIT ?"; // Parameter 2: Limit
        };
    }
}