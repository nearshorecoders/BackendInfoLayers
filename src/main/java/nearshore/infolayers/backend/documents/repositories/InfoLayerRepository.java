package nearshore.infolayers.backend.documents.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import nearshore.infolayers.backend.documents.InfoLayer;

public class InfoLayerRepository {

	@Autowired
	MongoTemplate mongoTemplate;
	
	public List<InfoLayer> getInfoLayerByName(String name){
		Query query = new Query(where("name").is(name));
		return mongoTemplate.find(query, InfoLayer.class);
	}
	
	
	public List<InfoLayer> getInfoLayerByNameAndState(String name,String stateId){
		Query query = new Query(where("name").is(name).and("stateId").is(stateId));
		return mongoTemplate.find(query, InfoLayer.class);
		
	}
	
	public List<InfoLayer> getInfoLayerByNameAndState(String name,String stateId,String municipio){
		
		Query query = new Query(where("name").is(name).and("stateId").is(stateId).and("municipio").is(municipio));
		return mongoTemplate.find(query, InfoLayer.class);
		
	}
	
}
