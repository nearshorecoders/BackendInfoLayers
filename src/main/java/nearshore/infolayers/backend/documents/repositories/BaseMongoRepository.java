package nearshore.infolayers.backend.documents.repositories;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.mongodb.MongoException;

@Repository
public class BaseMongoRepository<T> {

    public static final Logger log = LoggerFactory.getLogger(BaseMongoRepository.class);

    @Autowired
    protected MongoOperations mongoOps;

    @Autowired
    protected MongoTemplate mongoTemplate;

    public T save(T t) throws MongoException {

        try {

            mongoOps.save(t);

        } catch (Exception e) {

            log.error("{}", e);

            throw new MongoException("Error al guardar la bitacora", e);

        }

        return t;
    }

    public T insert(T t) throws MongoException {

        try {

            mongoOps.insert(t);

        } catch (Exception e) {

            log.error("{}", e);

            throw new MongoException("Error al insertar la bitacora", e);

        }

        return t;
    }

    List<Map<String, Object>> getByParameterAndValue(String collection, String key, String parameter){
        try {

            return executeCommand(collection, new Query(where(parameter).is(key)));

        } catch (Exception e) {

            log.error("{}", e);

            throw new MongoException("No se pudo consultar la información", e);
        }
    }

    List<Map<String, Object>> getByParameterAndValue(String collection, Integer key, String parameter){
        try {

            return executeCommand(collection, new Query(where(parameter).is(key)));

        } catch (Exception e) {

            log.error("{}", e);

            throw new MongoException("No se pudo consultar la información", e);
        }
    }

    List<Map<String, Object>> getCollectionInElementID(String collection, Integer key, String parameter){
        try {

            return executeCommand(collection, new Query(where(parameter).in(key)));

        } catch (Exception e) {

            log.error("{}", e);

            throw new MongoException("No se pudo consultar la información", e);
        }
    }

    List<Map<String, Object>> executeCommand(String collection, Query query){
        List<Map<String, Object>> result = new ArrayList<>();

        mongoOps.executeQuery(query, collection, new DocumentCallbackHandler() {
			@Override
			public void processDocument(Document document) throws MongoException, DataAccessException {
                result.add(document);
			}
        });

        return result;
    }
}
