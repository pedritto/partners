package partners.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import partners.model.Partner;

@RepositoryRestResource(collectionResourceRel = "partners", path = "partners")
public interface PartnerRepository extends PagingAndSortingRepository<Partner, Long> {

	List<Partner> findByName(@Param("name") String name);

}
