package nearshore.infolayers.backend.documents;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Geo_capa_counter")
public class GeoLayer extends BaseDocument{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer layerId;
	private Integer zoomId;
	private List<Map> multipolygon;
	public Integer getLayerId() {
		return layerId;
	}
	public void setLayerId(Integer layerId) {
		this.layerId = layerId;
	}
	public Integer getZoomId() {
		return zoomId;
	}
	public void setZoomId(Integer zoomId) {
		this.zoomId = zoomId;
	}
	public List<Map> getMultipolygon() {
		return multipolygon;
	}
	public void setMultipolygon(List<Map> multipolygon) {
		this.multipolygon = multipolygon;
	}
	@Override
	public String toString() {
		return "GeoLayer [layerId=" + layerId + ", zoomId=" + zoomId + ", multipolygon=" + multipolygon
				+ ", getStateId()=" + getStateId() + "]";
	}
	

}
