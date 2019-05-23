package nearshore.infolayers.backend.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.stereotype.Service;

import nearshore.infolayers.backend.documents.GeoLayer;
import nearshore.infolayers.backend.documents.Geometry;
import nearshore.infolayers.backend.documents.repositories.LayersRepository;

@Service
public class InformationLayersService {

	@Autowired
	LayersRepository layersRepository;
	
	Map<String,GeoLayer> mapGeoLayers = new HashMap<>();

    public Object getStates() {
    	return layersRepository.getStates();
    }	
	
    public Object getElectoralZone() {
    	
    	return layersRepository.getElectoralZones();
    	
    }
	
    public Object getPobrezaLayer() {
    	
    	return layersRepository.getPobrezaLayer();
    	
    }
	//get all current geo layer in db
    public Map<String,GeoLayer> getGeoLayerList() {
    	
    	System.out.println("\n\n getGeoLayerList init... " + new Date() + "\n\n");
    	
    	List<GeoLayer> list = layersRepository.getGeoLayerList();

    	Map<String,GeoLayer> map = new HashMap<>();
    	
    	for(GeoLayer geo: list) {
    		map.put(geo.getLayerId()+"-"+geo.getStateId()+"-"+geo.getZoomId(), geo);
    	}
    	
    	System.out.println("\n\ngetGeoLayerList end... " +new Date() + "\n\n");
    	
    	return map;
    }

    public List<Map<String, Object>> getLayerByStateAndLayerName(String stateId, String layerId) throws Exception {
        return layersRepository.getLayerByStateIdAndLayerName(stateId, layerId);
    }
    
    public List<Map<String, Object>> getNationalLayerByName(String layerName) throws Exception {
        return layersRepository.getNationalLayerByName(layerName);
    }
    
    public List<Map<String, Object>> getLayerById(Integer layerId, Integer stateId) throws Exception {
        return layersRepository.getLayerByIdAndStateId(layerId, stateId);
    }
    
    public String getLayerById(Integer layerId, Integer stateId, Geometry currentGeometry) throws Exception {
    	return layersRepository.getLayerByIdAndStateId(layerId, stateId, currentGeometry);
    }
    
    public Object getGeoLayerList(Integer stateId, Integer layerId, Integer zoomId, ArrayList<Object> frame) {
    	
    	if(zoomId < 7) {
	    	if(mapGeoLayers==null || mapGeoLayers.isEmpty()) {
	    		mapGeoLayers = getGeoLayerList();
	    	}
	    	
	    	return mapGeoLayers.get(layerId+"-"+stateId+"-"+zoomId);
    	}else {
    	
    		Geometry geo = new Geometry();
    		geo.setCoordinates(frame);
    		geo.setType("Polygon");
    		String result = layersRepository.getLayerByIdAndStateId(layerId, stateId, geo);
    		
    		return result;
    	}
    	
    }
    
    //FUNCTION TO GET NEAR POINT UN RADIO
	public GeoResults<String> getNearLayer(Double lng,Double lat, int layerId) {
		
    	return layersRepository.getNearLayerNew(lng, lat, layerId);
    }
    
}
