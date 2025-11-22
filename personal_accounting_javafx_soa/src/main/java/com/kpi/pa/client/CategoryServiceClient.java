package com.kpi.pa.client;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CategoryServiceClient {

    private final String baseUrl;

    public CategoryServiceClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

   
    public String getCategories() throws IOException {
        URL url = new URL(baseUrl + "/api/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        InputStream is = con.getResponseCode() < 400 ? con.getInputStream() : con.getErrorStream();

        try (Scanner s = new Scanner(is, "UTF-8")) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }


    public String createCategory(String jsonBody) throws IOException {
        URL url = new URL(baseUrl + "/api/categories");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
          
            os.write(jsonBody.getBytes("utf-8")); 
            os.flush(); 
        }

        InputStream is = con.getResponseCode() < 400 ? con.getInputStream() : con.getErrorStream();

        try (Scanner s = new Scanner(is, "UTF-8")) {
            return s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
    }
}