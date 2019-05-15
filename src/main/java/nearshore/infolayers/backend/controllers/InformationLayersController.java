package nearshore.infolayers.backend.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nearshore.infolayers.backend.services.InformationLayersService;

@RestController
@RequestMapping("/api/v1/informationlayers")
public class InformationLayersController {

	@Autowired
	InformationLayersService informationLayersService;
	
	  @CrossOrigin
	  @GetMapping("/electoralZones") 
	  public ResponseEntity<?>getAllElectoralZones() {
	    return ResponseEntity.ok().body(informationLayersService.getElectoralZone());
	  }
	
	  
	  @CrossOrigin
	  @GetMapping("/states") 
	  public ResponseEntity<?>getStates() {
	    return ResponseEntity.ok().body(informationLayersService.getStates());
	  }
	  
	  @CrossOrigin
	  @GetMapping("/pobreza") 
	  public ResponseEntity<?>getPobreza() {
	    return ResponseEntity.ok().body(informationLayersService.getPobrezaLayer());
	  }
}
