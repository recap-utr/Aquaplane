package de.seanbri.aquaplane.repository;

import de.seanbri.aquaplane.model.ArgumentPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArgumentPairRepository
        extends JpaRepository<ArgumentPair, Integer> { }
