package it.nextworks.composer.executor.repositories;

import it.nextworks.composer.plugins.catalogue.Catalogue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long> {

    Optional<Catalogue> findByCatalogueId(String catalogueId);

}
