package it.nextworks.composer.executor.repositories;

import it.nextworks.sdk.SdkServiceDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Marco Capitani on 04/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public interface SdkServiceDescriptorRepository extends JpaRepository<SdkServiceDescriptor, Long> {

    Optional<SdkServiceDescriptor> findById(Long id);

    List<SdkServiceDescriptor> findByTemplateId(Long id);
}
