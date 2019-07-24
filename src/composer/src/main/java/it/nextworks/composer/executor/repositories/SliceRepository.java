package it.nextworks.composer.executor.repositories;


import it.nextworks.composer.controller.elements.SliceResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SliceRepository extends JpaRepository<SliceResource, UUID> {

    Optional<SliceResource> findById(long id);

    Optional<SliceResource> findBySliceId(String sliceId);
}
