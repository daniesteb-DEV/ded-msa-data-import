package dev.daniesteb.ded.data.imports.repository;

import dev.daniesteb.ded.data.imports.repository.entity.BtCarTrn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BtCarTrnRepository extends ReactiveCrudRepository<BtCarTrn, Long> {
}
