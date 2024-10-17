package de.twaslowski.moodtracker.repository;

import de.twaslowski.moodtracker.entity.Record;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {

  List<Record> findByTelegramId(long id);
}
