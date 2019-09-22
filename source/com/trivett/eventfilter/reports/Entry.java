package com.trivett.eventfilter.reports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class Entry {
    

    @SerializedName("client-address")
    @XmlElement(name = "client-address")
    private String clientAddress;
    
    @SerializedName("client-guid")
    @XmlElement(name = "client-guid")
    private String clientGuid;
    
    @SerializedName("request-time")
    @XmlElement(name = "request-time")
    private String requestTime;
    
    @SerializedName("service-guid")
    @XmlElement(name = "service-guid")
    private String serviceGuid;
    
    @SerializedName("retries-request")
    @XmlElement(name = "retries-request")
    private int retriesRequest;
    
    @SerializedName("packets-requested")
    @XmlElement(name = "packets-requested")
    private int packetsRequested;
    
    @SerializedName("packets-serviced")
    @XmlElement(name = "packets-serviced")
    private int packetsServiced;

    @SerializedName("max-hole-size")
    @XmlElement(name = "max-hole-size")
    private String maxHoleSize;

    private Date requestDate;

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String clientGuid) {
        this.clientGuid = clientGuid;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getServiceGuid() {
        return serviceGuid;
    }

    public void setServiceGuid(String serviceGuid) {
        this.serviceGuid = serviceGuid;
    }

    public int getRetriesRequest() {
        return retriesRequest;
    }

    public void setRetriesRequest(int retriesRequest) {
        this.retriesRequest = retriesRequest;
    }

    public int getPacketsRequested() {
        return packetsRequested;
    }

    public void setPacketsRequested(int packetsRequested) {
        this.packetsRequested = packetsRequested;
    }

    public int getPacketsServiced() {
        return packetsServiced;
    }

    public void setPacketsServiced(int packetsServiced) {
        this.packetsServiced = packetsServiced;
    }

    public String getMaxHoleSize() {
        return maxHoleSize;
    }

    public void setMaxHoleSize(String maxHoleSize) {
        this.maxHoleSize = maxHoleSize;
    }

    public Date getRequestDate() {
        if (requestDate == null) {
            this.parseDate();
        }
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    
    
    @Override
    public int hashCode() {
        //this turned out to be faster than the default hashcode
        return this.toString().hashCode();
    }
    
    @Override
    public String toString() {
        return "["
                + clientAddress + "|" 
                + clientGuid + "|" 
                + requestTime + "|" 
                + serviceGuid + "|" 
                + retriesRequest + "|" 
                + packetsRequested + "|" 
                + packetsServiced + "|" 
                + maxHoleSize 
                + "]"
                ;
        
    }
    
    public void parseDate() {
        try {
            long dateTimeStamp = Long.parseLong(this.requestTime);
            Calendar atlanticTime = Calendar.getInstance(TimeZone.getTimeZone("Canada/Atlantic"));
            atlanticTime.setTimeInMillis(dateTimeStamp);
            this.requestDate = atlanticTime.getTime();
            
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
            outputFormat.setTimeZone(TimeZone.getTimeZone("Canada/Atlantic"));
            requestTime = outputFormat.format(this.requestDate);
            
            return;
        } catch (NumberFormatException nfe){
            // lets assume its a text date string.
            
        }
        
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
            dateFormatter.setTimeZone(TimeZone.getTimeZone("Canada/Atlantic"));
            this.requestDate = dateFormatter.parse(this.requestTime);
            return;
        } catch (ParseException e) {
            
        }
        
        throw new RuntimeException("Unable to format date on an Entry: " + this.toString());
    }
    
    public static final Comparator<Entry> ENTRY_COMPARATOR = new Comparator<Entry>() {

        @Override
        public int compare(Entry e1, Entry e2) {

            if (e2.getRequestDate().after(e2.getRequestDate())) {
                return 1;
            } else if (e1.getRequestDate().before(e2.getRequestDate())) {
                return -1;
            } else {
                return 0;
            }
        }
    };
}
