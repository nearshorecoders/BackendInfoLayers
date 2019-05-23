package nearshore.infolayers.backend.controllers;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	  
	  @CrossOrigin
	  @GetMapping(value = "/getPobrezaByState/{stateId}/{layerId}")
		public Object getLayerByState(@PathVariable String stateId,@PathVariable String layerId){
		try {
			if(stateId.equals("00")) {
				return new ResponseEntity<Object>(informationLayersService.getNationalLayerByName(layerId), HttpStatus.OK);
			}else {
				return new ResponseEntity<Object>(informationLayersService.getLayerByStateAndLayerName(stateId, layerId), HttpStatus.OK);	
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Object>("Error al obtener la capa", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	  }
}
