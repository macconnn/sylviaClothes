package com.sylvia.clothes.jpa;

import com.sylvia.clothes.entity.TripDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <pre>
 * TripDayRepository
 * </pre>
 *
 * @author Eden
 */
@Repository
public interface TripDayRepository extends JpaRepository<TripDay, String> {

  List<TripDay> findByTripIdOrderByDayIndexAsc(String tripId);
}
