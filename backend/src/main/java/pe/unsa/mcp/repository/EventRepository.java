package pe.unsa.mcp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.unsa.mcp.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Optional<Event> findByYear(Integer year);

    Optional<Event> findByNameAndYear(String name, Integer year);

    boolean existsByNameAndYear(String name, Integer year);

    @Query("SELECT DISTINCT e.year FROM Event e ORDER BY e.year DESC")
    List<Integer> findAllDistinctYears();
}

