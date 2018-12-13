package it.nextworks.composer.executor.repositories;

import it.nextworks.sdk.SdkServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Marco Capitani on 04/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface SdkServiceInstanceRepository extends JpaRepository<SdkServiceInstance, Long> {

    Optional<SdkServiceInstance> findById(Long id);

}
