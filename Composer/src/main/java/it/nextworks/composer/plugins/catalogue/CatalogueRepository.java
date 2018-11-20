package it.nextworks.composer.plugins.catalogue;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogueRepository extends JpaRepository<Catalogue, Long>{
	
	Optional<Catalogue> findByCatalogueId(String catalogueId);

}
