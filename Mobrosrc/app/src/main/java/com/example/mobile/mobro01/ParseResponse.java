package com.example.mobile.mobro01;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.InputStream;

public class ParseResponse {

    public static String convertStreamToString(java.io.InputStream is){
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next():"";
    }

    public static String parse_response(HttpResponse response){
        String result = "";
        HttpEntity entity = response.getEntity();
        try {
            if (entity != null) {
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
            }
        }
        catch (Exception exec) {

        }
        // Headers
        org.apache.http.Header[] headers = response.getAllHeaders();
        return result;
    }
}
