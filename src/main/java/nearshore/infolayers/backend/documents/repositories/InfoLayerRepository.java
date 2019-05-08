package nearshore.infolayers.backend.documents.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import nearshore.infolayers.backend.documents.InfoLayer;

public class InfoLayerRepository {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public List<InfoLayer> getInfoLayerByState(String stateId){
		
		mongoTemplate.
		
	}
	
}
