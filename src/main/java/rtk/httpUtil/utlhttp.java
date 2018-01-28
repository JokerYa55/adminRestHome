/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.httpUtil;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
//import org.jboss.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author vasil
 */
public class utlhttp {

    private static final Logger log = Logger.getLogger(utlhttp.class.getName());
    private final int timeout = 20;
    RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();

    /**
     *
     * @param url
     * @param params
     * @param headerList
     * @return
     */
    public JSONObject doPost(String url, Object params, Map<String, String> headerList) {
        log.info(String.format("doPost => url =%s param = %s", url, params.toString()));
        JSONObject res = new JSONObject();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            Gson gson = new Gson();

            HttpPost post = new HttpPost(url);
            StringEntity postingString = new StringEntity(gson.toJson(params), "application/json", "UTF-8");
            post.setEntity(postingString);

            if (headerList != null) {
                headerList.entrySet().stream().forEach((t) -> {
                    Header header = new BasicHeader(t.getKey(), t.getValue());
                    post.setHeader(header);
                });
            }
            HttpResponse response = httpClient.execute(post);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuilder json = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                json.append(line);
            }

            log.info(String.format("json = %s", json.toString()));
            if ((json != null) && (json.toString().contains("Bearer"))) {
                JSONObject obj = new JSONObject();
                obj.put("error", json + "");
                res = obj;
            } else {
                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(json.toString());
                    JSONObject jsonObj = (JSONObject) obj;
                    res = jsonObj;
                } catch (Exception ex2) {
                    log.log(Level.ERROR, ex2);
                    JSONObject obj = new JSONObject();
                    obj.put("error", json + "");
                    res = obj;
                }
            }
        } catch (Exception e) {
            log.log(Level.ERROR, e);
        }
        log.info(String.format("res => ", res.toJSONString()));
        return res;
    }

    /**
     *
     * @param url
     * @param params
     * @param headerList
     * @return
     */
    public JSONObject doPost(String url, List params, Map<String, String> headerList) {
        log.info(String.format("doPost_1 => %s", params.toString()));
        JSONObject res = new JSONObject();
        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            HttpPost post = new HttpPost(url);
            if (headerList != null) {
                headerList.entrySet().stream().forEach((t) -> {
                    Header header = new BasicHeader(t.getKey(), t.getValue());
                    post.setHeader(header);
                });
            }

            if (params != null) {
                //System.out.println((new UrlEncodedFormEntity(params)).toString());
                post.setEntity(new UrlEncodedFormEntity(params));
            }

            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuilder json = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                //System.out.println(line);
                json.append(line);
            }

            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json.toString());
            JSONObject jsonObj = (JSONObject) obj;
            res = jsonObj;

        } catch (IOException | IllegalStateException | ParseException e) {
            log.log(Level.ERROR, e);
        }
        return res;
    }

    /**
     *
     * @param url
     * @param headerList
     * @return
     * @throws ParseException
     */
    public String doGet(String url, List params, Map<String, String> headerList) throws ParseException, UnsupportedEncodingException {
        log.info("doGet");
        String res = "";

        try (CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build()) {
            if (params != null) {
                StringBuilder pStr = new StringBuilder();
                for (Object param : params) {
                    pStr.append(param.toString());
                }
                if (pStr.toString().length() > 0) {
                    url = url + "?" + pStr.toString();
                }
            }

            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response;

            if (headerList != null) {
                headerList.entrySet().stream().forEach((t) -> {
                    Header header = new BasicHeader(t.getKey(), t.getValue());
                    request.setHeader(header);
                });
            }

            try {
                response = client.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String line = "";
                StringBuilder json = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    json.append(line);
                }

                res = json.toString();
                /*org.json.simple.parser.JSONParser parser = new JSONParser();
            Object obj = parser.parse(json.toString());
            try {
                JSONObject jsonObj = (JSONObject) obj;
                res = jsonObj;
            } catch (Exception e) {
                JSONArray jsonArr = (JSONArray) obj;
                System.out.println("arr = " + jsonArr.toJSONString() );
            }
            System.out.println("res = " + res.toJSONString());*/
            } catch (Exception ex) {
                log.log(Level.ERROR, ex);
                res = ex.getMessage();
            }
        } catch (Exception ex1) {
            log.log(Level.ERROR, ex1);
            res = ex1.getMessage();
        }
        log.info(String.format("res = %s", res));
        return res;
    }

    /**
     *
     * @param url
     * @param params
     * @param headerList
     * @return
     */
    public int doPut(/*String data,*/String url, Object params, Map<String, String> headerList) {
        log.info(String.format("doPut => %s param = %s", url, params));
        int responseCode = -1;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();) {
            HttpPut request = new HttpPut(url);

            // Set PARAMS
            if (params != null) {
                Gson gson = new Gson();
                StringEntity paramStr = new StringEntity(gson.toJson(params), "UTF-8");
                request.setEntity(paramStr);
            }

            // Set HEADERS
            if (headerList != null) {
                headerList.entrySet().stream().forEach((t) -> {
                    Header header = new BasicHeader(t.getKey(), t.getValue());
                    request.setHeader(header);
                });
            }

            HttpResponse response = httpClient.execute(request);
            responseCode = response.getStatusLine().getStatusCode();
            log.info(String.format("responseCode = %s", responseCode));
            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 204) {
                if (responseCode != 204) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent()), "UTF-8"));
                    System.out.println("br = " + br);
                    String output;
                    // System.out.println("Output from Server ...." + response.getStatusLine().getStatusCode() + "\n");
                    while ((output = br.readLine()) != null) {
                        log.info(output);
                    }
                    log.info(String.format("output = %s", output));
                }
            } else {
                log.info(response.getStatusLine().getStatusCode());
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

        } catch (Exception ex) {
            log.log(Level.ERROR, ex);
            System.out.println("url:" + url);
            System.out.println("data:" + params);
        }

        return responseCode;

    }
}
