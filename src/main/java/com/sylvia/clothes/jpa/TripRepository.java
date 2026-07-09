package com.sylvia.clothes.jpa;

import com.sylvia.clothes.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 * TripRepository
 * </pre>
 *
 * @author Eden
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, String> {
}
