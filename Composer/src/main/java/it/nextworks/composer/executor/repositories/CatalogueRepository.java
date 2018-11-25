package it.nextworks.composer.executor.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.nextworks.composer.plugins.catalogue.Catalogue;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long>{
	
	Optional<Catalogue> findByCatalogueId(String catalogueId);

}
