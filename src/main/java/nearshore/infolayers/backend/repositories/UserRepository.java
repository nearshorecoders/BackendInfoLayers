package nearshore.infolayers.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nearshore.infolayers.backend.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

}
