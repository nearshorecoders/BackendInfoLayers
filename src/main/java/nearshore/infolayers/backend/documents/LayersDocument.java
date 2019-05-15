package nearshore.infolayers.backend.documents;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Document(collection = "Capas")
public class LayersDocument implements Serializable {

	@Transient
	private static final long serialVersionUID = -3300943254032276638L;

	private Integer layerId;

	@JsonIgnore
	private String layerName;

	private String layerAlias;

	@JsonIgnore
	private ArrayList<Integer> stateId;

	private Integer type;

	public Integer getLayerId() {
		return layerId;
	}

	public void setLayerId(Integer layerId) {
		this.layerId = layerId;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getLayerAlias() {
		return layerAlias;
	}

	public void setLayerAlias(String layerAlias) {
		this.layerAlias = layerAlias;
	}

	public ArrayList<Integer> getStateId() {
		return stateId;
	}

	public void setStateId(ArrayList<Integer> stateId) {
		this.stateId = stateId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "LayersDocument [layerId=" + layerId + ", layerName=" + layerName + ", layerAlias=" + layerAlias
				+ ", stateId=" + stateId + ", type=" + type + "]";
	}
}
