package com.taller.app.repository;

import com.taller.app.domain.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Cliente entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("select c from Cliente c where lower(c.nombre) like lower(concat('%', :query, '%'))")
    List<Cliente> searchByNombre(@Param("query") String query);
}
