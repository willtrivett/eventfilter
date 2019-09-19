package com.trivett.eventfilter.mapping;

import java.util.HashMap;
import java.util.Map;

public class FieldMapping {

    private static Map<String,String> fieldMap;
    // not to be initialized.
    private FieldMapping() {
        
    }
    
    public static Map<String, String> getMap() {
        if(fieldMap == null){
            
            fieldMap = new HashMap<String, String>();
            fieldMap.put("service-guid", "serviceGuid");
            fieldMap.put("packets-serviced", "packetsServiced");
            fieldMap.put("packets-requested", "packetsRequested");
            fieldMap.put("retries-request", "retriesRequest");
            fieldMap.put("request-time", "requestTime");
            fieldMap.put("client-address", "clientAddress");
            fieldMap.put("client-guid", "clientGuid");
            fieldMap.put("max-hole-size", "maxHoleSize");
        }
        
        return fieldMap;
    }
}
