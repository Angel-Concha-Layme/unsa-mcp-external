package pe.unsa.mcp.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unsa.mcp.model.Speaker;

@Repository
public interface SpeakerRepository extends JpaRepository<Speaker, UUID> {

  Optional<Speaker> findByFullNameIgnoreCase(String fullName);

  @Query(
      value =
          "SELECT * FROM speakers WHERE full_name % :name ORDER BY similarity(full_name, :name)"
              + " DESC LIMIT :limit",
      nativeQuery = true)
  List<Speaker> findByNameSimilarity(@Param("name") String name, @Param("limit") int limit);

  @Query(
      "SELECT s FROM Speaker s WHERE "
          + "LOWER(s.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
          + "LOWER(s.orgName) LIKE LOWER(CONCAT('%', :query, '%')) OR "
          + "LOWER(s.jobTitle) LIKE LOWER(CONCAT('%', :query, '%'))")
  List<Speaker> searchByKeyword(@Param("query") String query);

  @Query(
      value =
          """
          SELECT *
          FROM speakers
          WHERE similarity(unaccent(full_name), unaccent(:q)) > :threshold
          ORDER BY similarity(unaccent(full_name), unaccent(:q)) DESC
          LIMIT :limit
          """,
      nativeQuery = true)
  List<Speaker> findByNameTrigram(
      @Param("q") String q, @Param("threshold") double threshold, @Param("limit") int limit);
}
