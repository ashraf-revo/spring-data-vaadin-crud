
package org.revo.repository;

import org.revo.domain.Person;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface PersonRepository extends CrudRepository<Person, Long> {
    List<Person> findAllBy(Pageable pageable);
}
