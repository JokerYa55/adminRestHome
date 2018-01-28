/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.sso.REST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rtk.httpUtil.utlhttp;
import rtk.sso.admintest.federatedIdentity;
import rtk.sso.admintest.keycloakUser;

/**
 *
 * @author vasil
 */
public class apiREST {

    private static final Logger log = Logger.getLogger(apiREST.class.getName());
    private String token = null;
    private String username = null;
    private String password = null;
    private String host = null;
    private String realm = null;
    private String client_id = null;
    private String client_secret = null;
    private String token_json;

    public apiREST(String username, String password, String client_id, String client_secret, String host, String realm) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.realm = realm;
        this.client_id = client_id;
        this.client_secret = client_secret;
        Init();
    }

    public apiREST(String token, String host, String realm) {
        this.token = token;
        this.host = host;
        this.realm = realm;
    }

    public void Init() {
        try {
            JSONObject objToken = genToken();
            this.token_json = objToken.toJSONString();
            this.token = (String) objToken.get("access_token");
        } catch (Exception e) {
        }
    }

    private JSONObject genToken() {
        JSONObject res = null;
        log.info("getToken()");
        try {
            utlhttp httpUtil = new utlhttp();
            String url;
            if (!host.contains("http")) {
                url = "http://" + host + "/realms/" + this.realm + "/protocol/openid-connect/token";
            } else {
                url = host + "/realms/" + this.realm + "/protocol/openid-connect/token";
            }
            log.info(String.format("url = %s", url));
            List nameValuePairs = new ArrayList(1);
            nameValuePairs.add(new BasicNameValuePair("client_id", this.client_id)); //you can as many name value pair as you want in the list.
            nameValuePairs.add(new BasicNameValuePair("username", this.username));
            nameValuePairs.add(new BasicNameValuePair("password", this.password));
            nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
            nameValuePairs.add(new BasicNameValuePair("client_secret", this.client_secret));
            JSONObject accessJson = httpUtil.doPost(url, nameValuePairs, null);            
            res = accessJson;
                    //
            log.log(Level.DEBUG, String.format("access_token = {0}", res));
        } catch (Exception e) {
            log.log(Level.ERROR, e);
        }
        return res;
    }

    public String addUser(Object user) {
        String res = null;
        try {
            utlhttp httpUtil = new utlhttp();
            // Отправляем другой запрос
            String url;
            //= "http://" + host + "/auth/admin/realms/" + this.realm + "/users";
            if (!host.contains("http")) {
                url = "http://" + host + "/admin/realms/" + this.realm + "/users";
            } else {
                url = host + "/admin/realms/" + this.realm + "/users";
            }

            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Content-Type", "application/json");
            mapHeader.put("Authorization", "Bearer " + this.token);
            JSONObject res1 = httpUtil.doPost(url, user, mapHeader);

            //log.info("res1 => " + res1);
            //log.log(Level.INFO, String.format("res1 = {0}", res1.toJSONString()));
            List<NameValuePair> params = new LinkedList<>();
            params.add(new BasicNameValuePair("search", ((keycloakUser) user).getUsername()));
            JSONArray userJSON = getUsers(params);
            JSONObject userDB = null;
            if (userJSON.size() == 1) {
                log.info(String.format("userDB = > ", userDB.toJSONString()));

                userDB = (JSONObject) userJSON.get(0);
                res = "1";
            } else {
                res = res1.toString();
            }

            // Change user password
            /*log.log(Level.INFO, String.format("userID = {0}", userDB.get("id")));
            changeUserPassword((String) userDB.get("id"), ((keycloakUser) user).getAttributes().get("password"));
            log.log(Level.INFO, String.format("userJSON = {0}", userJSON.toJSONString()));
            res = (String) res1.get("error");*/
        } catch (Exception e) {
            log.log(Level.ERROR, e);
        }
        return res;
    }

    public int changeUserPassword(String userID, String password) {
        try {
            log.info(String.format("changeUserPassword => {0}", password));
            String url;
            //= "http://" + host + "/auth/admin/realms/" + this.realm + "/users/" + userID + "/reset-password";

            if (!host.contains("http")) {
                url = "http://" + host + "/admin/realms/" + this.realm + "/users/" + userID + "/reset-password";
            } else {
                url = host + "/admin/realms/" + this.realm + "/users/" + userID + "/reset-password";;
            }

            utlhttp httpUtil = new utlhttp();

            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Content-Type", "application/json");
            mapHeader.put("charset", "utf-8");
            mapHeader.put("Authorization", "Bearer " + this.token);
            mapHeader.put("Accept-Encoding", "gzip,deflate,sdch");

            Map<String, String> param = new HashMap<>();
            param.put("type", "password");
            param.put("temporary", "false");
            param.put("value", password);
            int res = httpUtil.doPut(url, param, mapHeader);
            log.info(String.format("res = > %s", res));
            return res;

        } catch (Exception e) {
            log.log(Level.ERROR, e);
        }
        return 0;
    }

    /**
     *
     * @param params
     * @return
     */
    public JSONArray getUsers(List params) {
        log.info("getUsers");
        JSONArray res = null;
        try {

            utlhttp httpUtil = new utlhttp();
            // /admin/realms/{realm}/users
            String url;
            //= "http://" + host + "/auth/admin/realms/" + this.realm + "/users?";

            if (!host.contains("http")) {
                url = "http://" + host + "/admin/realms/" + this.realm + "/users?";
            } else {
                url = host + "/admin/realms/" + this.realm + "/users?";
            }

            if (params != null) {
                for (Object item : params) {
                    url = url + item.toString() + "&";
                }
            }
            log.info("url = " + url);
            Map<String, String> mapHeader = new HashMap<>();
            mapHeader.put("Content-Type", "application/json");
            mapHeader.put("charset", "utf-8");
            mapHeader.put("Authorization", "Bearer " + this.token);
            String arrStr = httpUtil.doGet(url, params, mapHeader);
            JSONParser parser = new JSONParser();
            try {
                Object obj = parser.parse(arrStr);
                res = (JSONArray) obj;
            } catch (Exception ex1) {
                res = new JSONArray();
                res.add(arrStr);
            }

        } catch (Exception e) {
            log.log(Level.ERROR, e);
        }
        //System.out.println("getUsers res= " + res.toJSONString());
        return res;
    }

    public int updateUser(String userID, Object user) {
        log.info("updateUser");
        String res = null;
        String url;
        //= "http://" + host + "/auth/admin/realms/" + this.realm + "/users/" + userID;

        if (!host.contains("http")) {
            url = "http://" + host + "/admin/realms/" + this.realm + "/users/" + userID;
        } else {
            url = host + "/admin/realms/" + this.realm + "/users/" + userID;
        }

        utlhttp httpUtil = new utlhttp();

        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("Content-Type", "application/json");
        mapHeader.put("charset", "utf-8");
        mapHeader.put("Authorization", "Bearer " + this.token);
        mapHeader.put("Accept-Encoding", "gzip,deflate,sdch");
        Integer res1 = httpUtil.doPut(url, user, mapHeader);
        return res1;
    }

    /**
     *
     * @param userID
     * @param param
     * @return
     */
    public String federatedIdentity(String userID, Object param) {
        log.info("federatedIdentity");
        String res = null;

        String url;
        //= "http://" + this.host + "/auth/admin/realms/" + this.realm + "/users/" + userID + "/federated-identity/" + ((federatedIdentity) param).getIdentityProvider();

        if (!host.contains("http")) {
            url = "http://" + host + "/admin/realms/" + this.realm + "/users/" + userID + "/federated-identity/" + ((federatedIdentity) param).getIdentityProvider();
        } else {
            url = host + "/admin/realms/" + this.realm + "/users/" + userID + "/federated-identity/" + ((federatedIdentity) param).getIdentityProvider();
        }

        log.info(String.format("url = %s", url));
        utlhttp httpUtil = new utlhttp();

        Map<String, String> mapHeader = new HashMap<>();
        mapHeader.put("Content-Type", "application/json");
        mapHeader.put("charset", "utf-8");
        mapHeader.put("Authorization", "Bearer " + this.token);
        mapHeader.put("Accept-Encoding", "gzip,deflate,sdch");

        JSONObject resArr = httpUtil.doPost(url, param, mapHeader);
        if (resArr.get("error") != null) {
            if (((String) resArr.get("error")).contains("Bearer")) {
                res = "Bearer";
            } else {
                res = (String) resArr.get("error");
            }
        } else if (((String) resArr.get("errorMessage")) != null) {
            res = (String) resArr.get("errorMessage");
        } else {
            res = resArr.toJSONString();
        }
        log.info(String.format("res = %s", res));
        return res;
    }

    public String getToken() {
        return token;
    }

    public String getToken_json() {
        return token_json;
    }

}
