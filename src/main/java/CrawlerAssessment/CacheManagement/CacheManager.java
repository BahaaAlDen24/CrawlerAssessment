package CrawlerAssessment.CacheManagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;



/**
 * This class is a simple cache manager for our crawler
 * It contains a Map<String,Object> to store objects in the memory with a specific key
 *
 * @author  Bahaa aldeen Hussein
 * @version 1.0
 * @since   2020-11-20
 */
@Service
public class CacheManager {

    private static Logger LOGGER = LoggerFactory.getLogger(CacheManager.class);

    public static Map< String, Object > cache = new HashMap<String,Object>(); // GLOBAL VARIABLE

    public static Map < String, Object > getCache() {
        return cache;
    }

    // Put data in global cache variable
    public boolean putInCache(String key ,Object object){
        try {
            LOGGER.debug("Store : " + key + " in cache");
            cache.put(key,object);
            return true ;
        }catch (Exception e){
            LOGGER.error("Error : " + e.getMessage());
            return false ;
        }
    }

    // Delete data in global cache variable
    public boolean deleteFromCache(String key){
        try {
            LOGGER.debug("Remove : " + key + " From cache");
            cache.remove(key);
            return  true ;
        }catch (Exception e){
            LOGGER.error("Error : " + e.getMessage());
            return false ;
        }
    }

    // Get data from global cache variable
    public Object getFromCache(String key){
        try {
            LOGGER.debug("Get : " + key + " From cache");
            return cache.get(key);
        }catch (Exception e){
            LOGGER.error("Error : " + e.getMessage());
            return null ;
        }
    }
}
