package nearshore.infolayers.backend.documents;

import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BaseDocumentNear {
	 @Transient
	    private static final long serialVersionUID = 2598936215255416339L;

	    @JsonIgnore
	    @Id
	    private ObjectId id;

	    private Integer stateId;

	    private String type;

	    private Map<String, Object> properties;
	    
	    @Field("geometry")
	    private Object geometryData;
	    
	    public ObjectId getId() {
	        return id;
	    }

	    public void setId(ObjectId id) {
	        this.id = id;
	    }

	    public Integer getStateId() {
	        return stateId;
	    }

	    public void setStateId(Integer stateId) {
	        this.stateId = stateId;
	    }

	    public String getType() {
	        return type;
	    }

	    public void setType(String type) {
	        this.type = type;
	    }

	    public Map<String, Object> getProperties() {
	        return properties;
	    }

	    public void setProperties(Map<String, Object> properties) {
	        this.properties = properties;
	    }

		public Object getGeometryData() {
			return geometryData;
		}

		public void setGeometryData(Object geometryData) {
			this.geometryData = geometryData;
		}
}
