package pe.unsa.mcp.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.unsa.mcp.model.Session;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

  List<Session> findByEventIdOrderBySeqAsc(UUID eventId);

  List<Session> findBySpeakerId(UUID speakerId);

  List<Session> findByEventIdAndDay(UUID eventId, LocalDate day);

  List<Session> findByEventIdAndStartsAtBetween(
      UUID eventId, OffsetDateTime start, OffsetDateTime end);

  @Query(
      "SELECT s FROM Session s WHERE s.event.id = :eventId AND s.startsAt <= :time AND s.endsAt >"
          + " :time")
  Optional<Session> findByEventIdAndAtTime(
      @Param("eventId") UUID eventId, @Param("time") OffsetDateTime time);

  @Query(
      "SELECT s FROM Session s WHERE s.event.id = :eventId AND s.seq > :seq ORDER BY s.seq ASC"
          + " LIMIT 1")
  Optional<Session> findNextByEventIdAndSeq(
      @Param("eventId") UUID eventId, @Param("seq") Integer seq);

  @Query(
      "SELECT s FROM Session s WHERE s.event.id = :eventId AND s.startsAt > :time ORDER BY"
          + " s.startsAt ASC LIMIT 1")
  Optional<Session> findNextByEventIdAndTime(
      @Param("eventId") UUID eventId, @Param("time") OffsetDateTime time);

  @Query(
      "SELECT s FROM Session s WHERE s.event.id = :eventId AND "
          + "(LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR "
          + "LOWER(s.abstractText) LIKE LOWER(CONCAT('%', :query, '%')))")
  List<Session> searchByKeyword(@Param("eventId") UUID eventId, @Param("query") String query);

  boolean existsByEventIdAndSpeakerId(UUID eventId, UUID speakerId);

  boolean existsByEventIdAndSeq(UUID eventId, Integer seq);

  @Query("select s from Session s join fetch s.speaker sp where s.id in :ids")
  List<Session> findAllWithSpeakerByIdIn(@Param("ids") List<UUID> ids);
}
