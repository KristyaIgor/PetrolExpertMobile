package com.petrolexpert.mobile.Utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    public static URL generateURL_Ping (String ip, String port){
        Uri builtUri;
        builtUri = Uri.parse("http://" + ip + ":" + port + "/peservice/json/Ping")
                .buildUpon()
                .build();
        URL url =null;
        try {
            url= new URL (builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }
    public static URL generateURL_GetCardInfo (String barcodes){
        Uri builtUri;
        builtUri = Uri.parse("http://178.168.80.129:1909/pec/json/GetCardInfoByBarcode?StationID=14&Barcode="  + barcodes )
                .buildUpon()
                .build();
        URL url =null;
        try {
            url= new URL (builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }
    public static URL generateURL_GetAssortiment (String ip, String port,String DeviceID,String CardID){
        Uri builtUri;
        builtUri = Uri.parse("http://" + ip + ":" + port + "/peservice/json/GetAssortment?deviceId="+DeviceID+ "&CardID=" + CardID)
                .buildUpon()
                .build();
        URL url =null;
        try {
            url= new URL (builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;

    }
    public static URL generateURL_RegDev (String ip,String port,String dev_id,String name,String latitude,String longitude){
        Uri reg_dev;
        reg_dev = Uri.parse("http://" + ip + ":" + port + "/peservice/json/RegisterDevice?deviceId="+ dev_id+"&Name="+ name + "&Latitude=" + latitude +"&Longitude=" + longitude )
                .buildUpon()
                .build();
        URL reg_dev_url =null;
        try {
            reg_dev_url= new URL (reg_dev.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return reg_dev_url;

    }
    public static URL generateURL_PrintX (String ip,String port,String dev_id){
        Uri reg_dev;
        reg_dev = Uri.parse("http://" + ip + ":" + port + "/peservice/json/PrintXReport?deviceId="+ dev_id)
                .buildUpon()
                .build();
        URL reg_dev_url =null;
        try {
            reg_dev_url= new URL (reg_dev.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return reg_dev_url;

    }
    public static URL generateURL_CreateBill(String ip,String port,String dev_id,String card_id,String price_lineUid,String price,String count,String latitude,String longitude){
        Uri reg_dev;
        reg_dev = Uri.parse("http://" + ip + ":" + port + "/peservice/json/CreateBill?deviceId="+dev_id+"&CardID="
                +card_id+"&PriceLine="+price_lineUid+"&Price="+ price+ "&Count="+count+"&Latitude="+latitude+"&Longitude=" +longitude)
                .buildUpon()
                .build();
        URL reg_dev_url =null;
        try {
            reg_dev_url= new URL (reg_dev.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return reg_dev_url;

    }

    public static String getResponse_from_Ping(URL url) throws IOException {
        String resp = "false";

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(2000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponse_from_GetCardInfo(URL url) throws IOException {
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponse_from_GetAssortiment(URL url) throws IOException {
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(6000);

        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponse_from_RegDev(URL url) throws IOException{
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponse_from_PrintX(URL url) throws IOException{
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
    public static String getResponse_from_CreateBill(URL url) throws IOException{
        String resp = "false";
        HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return resp;
            }

        } finally{
            urlConnection.disconnect();
        }
    }
}
