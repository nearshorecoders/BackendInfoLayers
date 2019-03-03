package nearshore.infolayers.backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nearshore.infolayers.backend.entities.User;
import nearshore.infolayers.backend.repositories.UserRepository;

@Service
public class UserService {

	public UserService() {}
	
	@Autowired
	UserRepository userRepository;
	
	public List<User> findAll(){
		return userRepository.findAll();
	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public User update(User user) {
		
		return userRepository.save(user);
	}
	
	public void delete(User user) {
		userRepository.delete(user);
	}
	
}
