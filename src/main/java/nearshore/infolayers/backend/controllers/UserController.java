package nearshore.infolayers.backend.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;

import nearshore.infolayers.backend.entities.User;
import nearshore.infolayers.backend.services.UserService;
//@CrossOrigin(origins = "http://domain2.com", maxAge = 3600) used to allow request of a specific domain
//@CrossOrigin(maxAge = 3600) used to allow request of other domains
@RestController
@RequestMapping("/api/v1")
public class UserController {
	
	@Autowired
	UserService userService;

	  /**
	   * Get all users list.
	   *
	   * @return the list
	   */
	//@CrossOrigin(origins = "http://domain2.com") we can mix crossorigins to allow certain methods to be requested by specific domains
		  @CrossOrigin
		  @GetMapping("/users") 
		  public ResponseEntity<?>getAllUsers() {
		    return ResponseEntity.ok().body(userService.findAll());
		  }
		  /**
		   * Gets users by id.
		   *
		   * @param userId the user id
		   * @return the users by id
		   * @throws ResourceNotFoundException the resource not found exception
		   */
		  @CrossOrigin
		  @GetMapping("/user/{id}")
		  public ResponseEntity<User> getUsersById(@PathVariable(value = "id") Long userId) {
		    User user =userService.findById(userId).orElseThrow(()-> new ResourceAccessException("User not found on :: " + userId));
		    return ResponseEntity.ok().body(user);
		  }
		  /**
		   * Create user user.
		   *
		   * @param user the user
		   * @return the user
		   */
		  @CrossOrigin
		  @PostMapping("/user")
		  public User createUser(@Valid @RequestBody User user) {
		    return userService.save(user);
		  }
		  /**
		   * Update user response entity.
		   *
		   * @param userId the user id
		   * @param userDetails the user details
		   * @return the response entity
		   * @throws ResourceNotFoundException the resource not found exception
		   */
		  @CrossOrigin
		  @PutMapping("/user/{id}")
		  public ResponseEntity<User> updateUser(
		      @PathVariable(value = "id") Long userId, @Valid @RequestBody User userDetails)
		      throws ResourceAccessException {
		    User user =
		        userService
		            .findById(userId)
		            .orElseThrow(() -> new ResourceAccessException("User not found on :: " + userId));
		    user.setEmail(userDetails.getEmail());
		    user.setLastName(userDetails.getLastName());
		    user.setFirstName(userDetails.getFirstName());
		    user.setUpdatedAt(new Date());
		    final User updatedUser = userService.save(user);
		    return ResponseEntity.ok(updatedUser);
		  }
		  /**
		   * Delete user map.
		   *
		   * @param userId the user id
		   * @return the map
		   * @throws Exception the exception
		   */
		  @CrossOrigin
		  @DeleteMapping("/user/{id}")
		  public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception {
		    User user =
		        userService
		            .findById(userId)
		            .orElseThrow(() -> new ResourceAccessException("User not found on :: " + userId));
		    userService.delete(user);
		    Map<String, Boolean> response = new HashMap<>();
		    response.put("deleted", Boolean.TRUE);
		    return response;
		  }
	
}
