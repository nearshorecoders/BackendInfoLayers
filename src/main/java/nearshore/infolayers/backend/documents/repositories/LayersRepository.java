package nearshore.infolayers.backend.documents.repositories;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.geoNear;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.when;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import nearshore.infolayers.backend.documents.GeoLayer;
import nearshore.infolayers.backend.documents.Geometry;
import nearshore.infolayers.backend.documents.LayersDocument;
import nearshore.infolayers.backend.documents.MultiPolygon;



@Repository
public class LayersRepository extends BaseMongoRepository{

	String identifierLayer;
	
	public List<LayersDocument> getLayersByState(Integer stateId, Integer layerType) throws MongoException {

		try {
			Query query = new Query(where("stateId").is(stateId).and("type").is(layerType));

			query.fields().exclude("layerName");
			query.fields().exclude("stateId");
			query.with( new Sort( Sort.Direction.ASC, "layerAlias" ));

			return mongoTemplate.find(query, LayersDocument.class);

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<LayersDocument> getLayersToProcess(Integer stateId, Integer layerType) throws MongoException {

		try {
			Query query = new Query(where("stateId").is(stateId).and("type").is(layerType).and("type_g").is("Point"));

			query.fields().exclude("layerName");
			query.fields().exclude("stateId");

			return mongoTemplate.find(query, LayersDocument.class);

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<Integer> getLayersWithPoints() throws MongoException {

		try {
			Query query = new Query(where("type_g").is("Point"));

			query.fields().exclude("layerName");
			query.fields().exclude("stateId");
			query.fields().exclude("_id");
			query.fields().exclude("layerAlias");
			query.fields().exclude("type");
			query.fields().exclude("type_g");

			List<LayersDocument> r = mongoTemplate.find(query, LayersDocument.class);
			List<Integer> ids = new ArrayList<>();
			for(LayersDocument e : r) {
				ids.add(e.getLayerId());
			}
			
			return ids;

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}

	public LayersDocument getLayerByLayerId(Integer layerId) throws MongoException {

		try {

			Query query = new Query(where("layerId").is(layerId));

			return mongoTemplate.findOne(query, LayersDocument.class);

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}

	public List<Map<String, Object>> getLayerByIdAndStateId(Integer layerId, Integer stateId) throws MongoException {
		try {

			LayersDocument layer = getLayerByLayerId(layerId);

			String layerName = layer.getLayerName();

			return getByParameterAndValue(layerName, stateId, "stateId");

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<Map<String, Object>> getLayerByStateIdAndLayerName(String stateId,String layerName) throws MongoException {
		try {

			return getByParameterAndValue(layerName, stateId, "properties.CVE_ENT");

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public String getTotDiscapasitados(Integer stateId, String level) {
		
		try {

			Criteria criteriaFilter = Criteria.where("stateId").is(stateId);

			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteriaFilter),
					//Aggregation.group("properties.NOM_MPIO")
					Aggregation.group("properties."+level)
					.sum("properties.DISC1").as("total")
					.sum("properties.DISC2").as("hombres")
					.sum("properties.DISC3").as("mujeres")
					.sum("properties.DISC4").as("tot_0_14")
					.sum("properties.DISC5").as("tot_15_59")
					.sum("properties.DISC6").as("tot_60_mas"));

			AggregationResults<String> result = mongoOps.aggregate(aggregation,
					"Capa_Discapacitados",String.class);

			if (result.getMappedResults() == null || result.getMappedResults().isEmpty())
				return "";

			return result.getMappedResults().toString();

		} catch (Exception e) {

			log.error("{}", e);
			return "";
		}
	}
	
	@SuppressWarnings({ "static-access" })
	public GeoResults<String> getNearLayerNew(Double lng,Double lat, int layerId) {
		String layerName = "";
		switch (layerId) {
		case 53:
			layerName = "Capa_Oxxos";
			break;
		case 54:
			layerName = "Capa_Seven";
			break;	
		case 55:
			layerName = "Capa_Bansefi";
			break;
		case 56:
			layerName = "Capa_Banamex";
			break;	
			
		default:
			return null;
		}
		
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("type").is("Feature"));
		query2.limit(1);
		
		Point point = new Point(lng,lat);
		
		NearQuery q = NearQuery.near(point).maxDistance(new Distance(1000, Metrics.KILOMETERS));
		
		q.spherical(true);
		
		q.query(query2);
 		
 		GeoResults<String> r = mongoOps.geoNear(q,String.class,layerName);
 		
 		return r;
	}
	public String getTotEscolaridad(Integer stateId, String level) {
		
		try {
			
			Criteria criteriaFilter = Criteria.where("stateId").is(stateId);
			
			Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteriaFilter),
					//Aggregation.group("properties.NOM_MPIO")
					Aggregation.group("properties."+level)
					.sum("properties.EDU1").as("asiste_3_5")
					.sum("properties.EDU7").as("asiste_6_11")
					.sum("properties.EDU13").as("asiste_12_15")
					.sum("properties.EDU40").as("asiste_15_mas"));
			
			AggregationResults<String> result = mongoOps.aggregate(aggregation,
					"Capa_Escolaridad",String.class);
			
			if (result.getMappedResults() == null || result.getMappedResults().isEmpty())
				return "";
			
			return result.getMappedResults().toString();
			
		} catch (Exception e) {
			
			log.error("{}", e);
			return "";
		}
	}
	
	public String getLayerByIdAndStateId(Integer layerId, Integer stateId, Geometry currentGeometry) throws MongoException {
		try {
			
			LayersDocument layer = getLayerByLayerId(layerId);
			
			String layerName = layer.getLayerName();
			
			try {
				Document query = new Document(
						"geometry", new Document(
								"$geoWithin", new Document(
										"$geometry", new Document(
												"coordinates", currentGeometry.getCoordinates()
												).append("type", currentGeometry.getType()))));
				
				return mongoTemplate.getCollection(layerName).find(query,List.class).toString();
				
				//return mongoTemplate.getCollection(layerName).find(query, new Document("_id", false))
				//							.toArray()
				//							.toString();
			} catch (Exception e) {
				return null;
			}
			
		} catch (Exception e) {
			
			log.error("{}", e);
			
			throw new MongoException("No se pudo consultar la información", e);
		}
	}

	
	public List<Map<String, Object>> getStatesZonesLayers() throws MongoException {
		try {

			return executeCommand("cat_estados", new Query());

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<Map<String, Object>> getStates() throws MongoException {
		try {

			return executeCommand("Capa_Estados", new Query());

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}

	public List<Map<String, Object>> getElectoralZones() throws MongoException {
		try {

			return executeCommand("ElectoralZone", new Query());

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<Map<String, Object>> getPobrezaLayer() throws MongoException {
		try {

			return executeCommand("Capa_pobreza", new Query());

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	public List<Map<String, Object>> getNationalLayerByName(String layerName) throws MongoException {
		try {

			return executeCommand(layerName, new Query());

		} catch (Exception e) {

			log.error("{}", e);

			throw new MongoException("No se pudo consultar la información nacional "+layerName, e);
		}
	}
	
//	public List<FederalDistrictLayerDocument> getFederalDistrictsLayerByState(Integer stateId, List<String> partiesList, Integer skip, Integer limit){
//		try {
//
//			Query query = createQueryForFederalAndLocalDistricts(stateId, partiesList, skip, limit);
//
//			return mongoTemplate.find(query, FederalDistrictLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}

//	public List<LocalDistrictLayerDocument> getLocalDistrictsLayerByState(Integer stateId, List<String> partiesList, Integer skip, Integer limit){
//		try {
//
//			Query query = createQueryForFederalAndLocalDistricts(stateId, partiesList, skip, limit);
//
//			return mongoTemplate.find(query, LocalDistrictLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}

//	public List<MunicipalityLayerDocument> getMunicipalityLayerByState(Integer stateId, List<Long> partiesList){
//		try {
//
//			Query query = new Query(where("stateId").is(stateId).and("properties.MUNICIPIO").in(partiesList));
//
//			return mongoTemplate.find(query, MunicipalityLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}

//	public List<SectionsLayerDocument> getSectionsLayerByState(Integer stateId, List<String> partiesList){
//		try {
//
//			Query query = new Query(where("stateId").is(stateId).and("properties.SS_ID").in(partiesList));
//
//			return mongoTemplate.find(query, SectionsLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}

	private Query createQueryForFederalAndLocalDistricts(Integer stateId, List<String> partiesList, Integer skip, Integer limit){
		Query query;

		if (partiesList != null && partiesList.size() > 0)
			query = new Query(where("stateId").is(stateId).and("properties.df_GL").in(partiesList));
		else
			query = new Query(where("stateId").is(stateId));

		if (skip != null && limit != null){
			query.skip(skip);
			query.limit(limit);
		}

		return query;
	}

//	public List<PoliticPartiesDocument> getPoliticPartiesByDivision(String collection, Integer stateId) throws MongoException {
//		try {
//
//			Criteria criteriaFilter1 = Criteria.where("stateId").is(stateId).and("properties.df_GL").ne(null);
//			Criteria criteriaFilter2 = Criteria.where("party").ne("");
//
//			Aggregation aggregation =  Aggregation.newAggregation(
//					Aggregation.match(criteriaFilter1),
//					Aggregation.group("properties.df_GL").count().as("count").first("properties.df_GL").as("party"),
//					Aggregation.match(criteriaFilter2)
//			);
//
//			AggregationResults<PoliticPartiesDocument> result = mongoTemplate.aggregate(aggregation,collection, PoliticPartiesDocument.class);
//
//			return result.getMappedResults();
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}

//	public List<PollingStationLayerDocument> getPollingStationsLayerByDivisionAndState(Divisions divisions, Integer stateId, Integer selectedId) {
//		try {
//
//			Query query;
//
//			switch (divisions){
//				case DISTRITOS_FEDERALES:
//
//					query = new Query(where("stateId").is(stateId).and("properties.DTTO_FED").in(selectedId));
//
//					break;
//				case DISTRITOS_LOCALES:
//
//					query = new Query(where("stateId").is(stateId).and("properties.DTTO_FED").in(selectedId));
//
//					break;
//				case MUNICIPIOS:
//
//					query = new Query(where("stateId").is(stateId).and("properties.DTTO_FED").in(selectedId));
//
//					break;
//				default:
//					throw new InvalidParameterException("Error al leer la división.");
//			}
//
//			query.fields().exclude("stateId");
//			query.fields().exclude("properties");
//			query.fields().exclude("type");
//
//			return mongoTemplate.find(query, PollingStationLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}
	
	public String getMuninipiosList(Integer stateId) throws MongoException {
		try {
			Document query = new Document(
					"stateId", stateId);
			return mongoTemplate.getCollection("Capa_Municipio")
					.find(query, List.class)
					.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getGeometryMunByName(String nameMun, Integer stateId) throws MongoException {
		try {
			BasicDBObject query = new BasicDBObject(
					"stateId", stateId).append("properties.NOMBRE", nameMun);
//			return mongoTemplate.getCollection("Capa_Municipio")
//					.findOne(query, new Document("_id", false))
//					.toString();
			
			return mongoTemplate.getCollection("Capa_Municipio")
					.find(query, List.class)
					.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	/*public String getSeccion(Integer seccion, Integer stateId) throws MongoException {
		try {
			BasicDBObject query = new BasicDBObject(
					"stateId", stateId).append("properties.SECCION", seccion);
			return mongoTemplate.getCollection("Capa_Secciones")
					.findOne(query, new BasicDBObject("_id", false))
					.toString();
		} catch (Exception e) {
			return null;
		}
	}*/
//	public SectionsLayerDocument getSeccion(Integer seccion, Integer stateId){
//		try {
//
//			Query query = new Query(where("stateId").is(stateId).and("properties.SECCION").is(seccion));
//
//			return mongoTemplate.findOne(query, SectionsLayerDocument.class);
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}
	
//	public Object getDivision(Divisions divisions,Integer numDivision, Integer stateId){
//		try {
//			switch (divisions){
//			case DISTRITOS_FEDERALES:
//				return executeCommand(
//						"Capa_DistritoFederal"
//						, new Query(where("stateId").is(stateId).and("properties.DISTRITO").is(numDivision)));
//			case DISTRITOS_LOCALES:
//				return executeCommand(
//						"Capa_DistritoLocal"
//						, new Query(where("stateId").is(stateId).and("properties.DISTRITO").is(numDivision)));
//			case MUNICIPIOS:
//				return executeCommand(
//						"Capa_Municipio"
//						, new Query(where("stateId").is(stateId).and("properties.MUNICIPIO").is(numDivision)));
//			case SECCIONES:
//				return executeCommand(
//						"Capa_Secciones"
//						, new Query(where("stateId").is(stateId).and("properties.SECCION").is(numDivision)));
//			case REGIONES:
//				return executeCommand(
//						"Capa_Regiones_Censo"
//						, new Query(where("stateId").is(stateId).and("properties.region").is(numDivision)));
//			case COORD_REG:
//				return executeCommand(
//						"Capa_Coord_Indigenas"
//						, new Query(where("stateId").is(stateId).and("properties.coord_indigena").is(numDivision)));
//			default:
//				throw new InvalidParameterException("Error al leer la división.");
//		}
//
//		} catch (Exception e) {
//
//			log.error("{}", e);
//
//			throw new MongoException("No se pudo consultar la información", e);
//		}
//	}
	
	public List<MultiPolygon> getPoligonosCapaList(String state, String capa) throws MongoException {
		try {
			
			Criteria criteria = Criteria.where("properties.codeState").is(state);
			 
	        MatchOperation filter = match(criteria);
	        ProjectionOperation project = Aggregation.project("geometry");
	        ProjectionOperation projectStage = Aggregation.project("geometry.coordinates","geometry.type");
	        
	        Aggregation ag = newAggregation(filter,project,projectStage);
	        
	        AggregationResults<MultiPolygon> results = mongoTemplate.aggregate(ag, capa, MultiPolygon.class);
	        
			return results.getMappedResults();
		} catch (Exception e) {
			return null;
		}
	}
	
	public Integer getCountFromMultiPolygon(MultiPolygon polygone, Integer layerId) throws MongoException {
//		try {
//			LayersDocument layer = getLayerByLayerId(layerId);
//			
//			String layerName = layer.getLayerName();
//			
//			BasicDBObject query = new BasicDBObject(
//					"geometry", new BasicDBObject(
//							"$geoIntersects", new BasicDBObject(
//									"$geometry", new BasicDBObject(
//											"coordinates", polygone.getCoordinates()
//											).append("type", polygone.getType()))));
//			return mongoTemplate.getCollection(layerName)
//					.find(query, new BasicDBObject("_id", false))
//					.count();
//			
//		} catch (Exception e) {
//			
//			log.error("{}", e);
//			
//			throw new MongoException("No se pudo consultar la información", e);
//		}
		return 1;
	}
	
	public Object getManchaFromMultiPolygon(MultiPolygon polygone, Integer zoomId) throws MongoException {
		try {
			LayersDocument layer = getLayerByLayerId(69);
			
			String layerName = layer.getLayerName();
			
			BasicDBObject query = null;
			
			if(zoomId > 0) {
				query = new BasicDBObject(
					"geometry", new BasicDBObject(
							"$geoIntersects", new BasicDBObject(
									"$geometry", new BasicDBObject(
											"coordinates", polygone.getCoordinates().get(0)
											).append("type", polygone.getType()))));
			}else {
				query = new BasicDBObject(
						"geometry", new BasicDBObject(
								"$geoIntersects", new BasicDBObject(
										"$geometry", new BasicDBObject(
												"coordinates", polygone.getCoordinates()
												).append("type", polygone.getType()))));
				
			}
			//System.out.println("Query: " + query);
			
//			return mongoTemplate.getCollection(layerName)
//					.find(query, new BasicDBObject("_id", false)).sort(new BasicDBObject("kilometros", -1)).limit(1);
			return 1;
			
		} catch (Exception e) {
			
			log.error("{}", e);
			
			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	
	public Object getEscuelaFromMultiPolygon(MultiPolygon polygone, Integer zoomId) throws MongoException {
//		try {
//			LayersDocument layer = getLayerByLayerId(19);
//			
//			String layerName = layer.getLayerName();
//			
//			BasicDBObject query = null;
//			
//			if(zoomId > 0) {
//				query = new BasicDBObject(
//					"geometry", new BasicDBObject(
//							"$geoIntersects", new BasicDBObject(
//									"$geometry", new BasicDBObject(
//											"coordinates", polygone.getCoordinates().get(0)
//											).append("type", polygone.getType()))));
//			}else {
//				query = new BasicDBObject(
//						"geometry", new BasicDBObject(
//								"$geoIntersects", new BasicDBObject(
//										"$geometry", new BasicDBObject(
//												"coordinates", polygone.getCoordinates()
//												).append("type", polygone.getType()))));
//				
//			}
//			//System.out.println("Query: " + query);
//			
//			return mongoTemplate.getCollection(layerName)
//					.find(query, new BasicDBObject("_id", false));
//			
//			
//		} catch (Exception e) {
//			
//			log.error("{}", e);
//			
//			throw new MongoException("No se pudo consultar la información", e);
//		}
		return null;
	}
	
	
	
	public Integer saveGeoCapa(GeoLayer geoLayer) throws MongoException {
		try {
			mongoTemplate.save(geoLayer);
			return 1;
		} catch (Exception e) {
			
			log.error("{}", e);
			
			throw new MongoException("No se pudo consultar la información", e);
		}
	}
	
	
	 public boolean exists(GeoLayer geoLayer)throws Exception{
	        try{
	            Query query = new Query();
	            query.addCriteria(Criteria.where("layerId").is(geoLayer.getLayerId()).andOperator(
	                    Criteria.where("stateId").is(geoLayer.getStateId()),
	                    Criteria.where("zoomId").is(geoLayer.getZoomId())
	            ));
	            return mongoTemplate.exists(query, GeoLayer.class);
	        }catch (Exception e){
	            log.error("exists : {}", e.toString());
	            throw e;
	        }
	 }
	 
	 public GeoLayer getGeoLayer(GeoLayer geoLayer)throws Exception{
	        try{
	            Query query = new Query();
	            query.addCriteria(Criteria.where("layerId").is(geoLayer.getLayerId()).andOperator(
	                    Criteria.where("stateId").is(geoLayer.getStateId()),
	                    Criteria.where("zoomId").is(geoLayer.getZoomId())
	            ));
	            return mongoTemplate.findOne(query, GeoLayer.class);
	        }catch (Exception e){
	            log.error("findOne : {}", e.toString());
	            throw e;
	        }
	 }
	 
	 public List<GeoLayer> getGeoLayerList(Integer stateId, Integer layerId, Integer zoomId) throws MongoException {
			try {
				
				Query query = new Query();
	            query.addCriteria(Criteria.where("layerId").is(layerId).andOperator(
	                    Criteria.where("stateId").is(stateId),
	                    Criteria.where("zoomId").is(zoomId)
	            ));
		        
	            return mongoTemplate.find(query, GeoLayer.class);
			} catch (Exception e) {
				return null;
			}
		}
	 
	 public List<GeoLayer> getGeoLayerList() throws MongoException {
			try {
				return mongoTemplate.findAll(GeoLayer.class);
			} catch (Exception e) {
				return null;
			}
	}
	 
	public HashMap<String, Object> getTotalInAisladaAndNoAilada(Integer stateId, String level) {
		HashMap<String, Object> response = new HashMap();
		Aggregation aggregation =  null;
		
		try {

			Criteria criteriaFilter = Criteria.where("stateId").is(stateId);

			if(level.indexOf("cobertura") != -1 ? true : false) {
				
				aggregation = Aggregation.newAggregation(Aggregation.match(criteriaFilter),
						Aggregation.group("stateId").sum(
								when(where("properties."+level).is("Si")).then(1).otherwise(0)
						).as("total")
						.count().as("count")
				);
				
				
			}else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteriaFilter),
					Aggregation.group("stateId")
					.sum("properties."+ level).as("total")
					.count().as("count"));
			}

			AggregationResults<Object> result_urbana = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana" ,Object.class);
			AggregationResults<Object> result_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_Aislada" ,Object.class);
			AggregationResults<Object> result_no_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_No_Aislada" ,Object.class);

			if (result_urbana.getMappedResults() == null || result_urbana.getMappedResults().isEmpty() ||
					result_aislada.getMappedResults() == null || result_aislada.getMappedResults().isEmpty() ||
							result_no_aislada.getMappedResults() == null || result_no_aislada.getMappedResults().isEmpty())
				return response;
			
			response.put("urbanas", result_urbana.getUniqueMappedResult());
			response.put("aisaladas",result_aislada.getUniqueMappedResult());
			response.put("no_aisladas", result_no_aislada.getUniqueMappedResult());
			
			return response;

		} catch (Exception e) {

			log.error("{}", e);
			return response;
		}
	}
	
	public HashMap<String, Object>  getNoBanksByState(Integer stateId, String propertie) {
		HashMap<String, Object> response = new HashMap();
		Aggregation aggregation =  null;
		Criteria query =  Criteria.where("properties."+ propertie);
		
		switch (propertie) {
		case "coberturaatt3g":
			query.is("Si");
			break;
		case "coberturaatt4g":
			query.is("Si");
			break;
		case "coberturamovi2g":
			query.is("Si");
			break;
		case "coberturamovi3g":
			query.is("Si");
			break;
		case "coberturamovi4g":
			query.is("Si");
			break;
		case "coberturatelcel2g":
			query.is("Si");
			break;
		case "coberturatelcel3gv":
			query.is("Si");
			break;
		case "coberturatelcel3g":
			query.is("Si");
			break;
		case "coberturatelcel4g":
			query.is("Si");
			break;
		case "coberturacelular":
			query.is(true);
			break;
		case "coberturabancaria":
			query.is(true);
			break;
		default:
			query.gt(0);
			break;
		}
		
		try {
			if(stateId == 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.group("stateId").sum(
								when(query).then(1).otherwise(0)
						).as("prop")
						.count().as("count"),Aggregation.sort(Sort.Direction.ASC, "_id")
						);
			}else {
				Criteria criteriaFilter = Criteria.where("stateId").is(stateId);
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteriaFilter),
						Aggregation.group("stateId").sum(
								when(query).then(1).otherwise(0)
						).as("prop")
						.count().as("count"), Aggregation.sort(Sort.Direction.ASC, "_id")
						);
			}
			
			AggregationResults<Object> result_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_Aislada" ,Object.class);
			AggregationResults<Object> result_no_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_No_Aislada" ,Object.class);
			
			if (result_aislada.getMappedResults() == null || result_aislada.getMappedResults().isEmpty() ||
					result_no_aislada.getMappedResults() == null || result_no_aislada.getMappedResults().isEmpty())
				return response;
			
			response.put("aisladas", result_aislada.getMappedResults());
			response.put("no_aisaladas",result_no_aislada.getMappedResults());
			
			return response;
			
		} catch (Exception e) {
			
			log.error("{}", e);
			return response;
		}
	}
	
	public HashMap<String, Object> getCobertura(Integer stateId) {
		HashMap<String, Object> response = new HashMap();
		Aggregation aggregation =  null;
		
		Criteria bank =  Criteria.where("properties.coberturacelular").is(false);
		Criteria phone =  Criteria.where("properties.coberturabancaria").is(true);
		Criteria c_and = new Criteria().andOperator(bank, phone);
		
		try {
			if(stateId == 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.group("stateId").sum(
								when(c_and).then(1).otherwise(0)
						).as("ban_phon")
						.count().as("count")
						);
			}else {
				//https://lishman.io/spring-data-mongotemplate-queries
				Criteria criteriaFilter = Criteria.where("stateId").is(stateId);
				
				aggregation = Aggregation.newAggregation(Aggregation.match(criteriaFilter),
						Aggregation.group("stateId").sum(
								when(c_and).then(1).otherwise(0)
						).as("ban_phon")
						.count().as("count")
				);
			}
				
			AggregationResults<Object> result_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_Aislada" ,Object.class);
			AggregationResults<Object> result_no_aislada = mongoOps.aggregate(aggregation,
					"Capa_Mancha_Urbana_No_Aislada" ,Object.class);
			
			if (result_aislada.getMappedResults() == null || result_aislada.getMappedResults().isEmpty() ||
					result_no_aislada.getMappedResults() == null || result_no_aislada.getMappedResults().isEmpty())
				return response;
			
			response.put("aisladas", result_aislada.getMappedResults());
			response.put("no_aisaladas",result_no_aislada.getMappedResults());
			
			return response;
			
		} catch (Exception e) {
			
			log.error("{}", e);
			return response;
		}
	}
	
	public HashMap<String, Object> getAllMUAMUNA() {
		HashMap<String, Object> response = new HashMap();
		Criteria bank =  Criteria.where("properties.kilometros").is("a");
		try {
			
			Aggregation aggregation_urabanas_a = Aggregation.newAggregation(
					Aggregation.group("stateId").sum("properties.kilometros").as("kmmua")
					.count().as("mua")
					);
			Aggregation aggregation_urabanas_na = Aggregation.newAggregation(
					Aggregation.group("stateId").sum("properties.kilometros").as("kmmuna")
					.count().as("muna")
					);
				
			AggregationResults<Object> result_aislada = mongoOps.aggregate(aggregation_urabanas_a,
					"Capa_Mancha_Urbana_Aislada" ,Object.class);
			AggregationResults<Object> result_no_aislada = mongoOps.aggregate(aggregation_urabanas_na,
					"Capa_Mancha_Urbana_No_Aislada" ,Object.class);
			
			if (result_aislada.getMappedResults() == null || result_aislada.getMappedResults().isEmpty() ||
					result_no_aislada.getMappedResults() == null || result_no_aislada.getMappedResults().isEmpty())
				return response;
			
			response.put("aisladas", result_aislada.getMappedResults());
			response.put("no_aisaladas",result_no_aislada.getMappedResults());
			
			return response;
			
		} catch (Exception e) {
			
			log.error("{}", e);
			return response;
		}
	}
}