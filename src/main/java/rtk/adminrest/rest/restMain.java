/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rtk.adminrest.rest;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rtk.adminrest.beans.UserAttribute;
import rtk.adminrest.beans.UserEntity;
import rtk.sso.REST.apiREST;
import rtk.sso.admintest.credentialRepresentation;
import rtk.sso.admintest.federatedIdentity;
import rtk.sso.admintest.keycloakUser;

/**
 *
 * @author vasiliy.andricov
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class restMain {

    private final Logger log = Logger.getLogger(getClass().getName());
    @Context
    private Request request;
    @Context
    private UriInfo uriInfo;
    @Context
    private HttpHeaders requestHeaders;
    @Context
    private Response response;

    private static EntityManagerFactory emf;
    private EntityManager em;

    private String host;
    private String realm;

    @PreDestroy
    private void preDestroy() {
        log.debug("preDestroy");
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("postConstruct");
    }

    public restMain() {
        log.debug(String.format("restMain"));
        getEM();

    }

    private String getUserToken() {
        String res = "";
        String tempToken = requestHeaders.getHeaderString("Authorization");
        if (tempToken != null) {
            String[] arr = tempToken.split(" ");
            if (arr.length == 2) {
                String token = arr[1];
                res = token;
            } else {
                res = "Bearer error";
            }
        } else {
            res = "Authorisation error";
        }
        return res;
    }

    private EntityManager getEM() {
        if (this.emf == null) {
            this.emf = Persistence.createEntityManagerFactory("admin_rest_jpa");
        }
        this.em = this.emf.createEntityManager();
        return this.em;
    }

    private String getParam() {
        String res = null;
        try {
            File file = new File(getClass().getResource("/keycloak.json").getFile());
            res = file.getName();
        } catch (Exception ex) {
            log.log(Level.ERROR, ex);
            res = "Error => " + ex;
        }
        return res;
    }

    @Path("/test")
    @GET
    //@RolesAllowed("video-app-user")
    public String test() {
        log.info("test");
        String res = "";
        return getUserToken();
    }

    /**
     *
     * @param user
     * @return
     */
    @Path("/realms/{realm}/users")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed("video-app-user")
    public Response addUser(@PathParam("realm") String p_realm, keycloakUser user) {
        String result = null;
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "addUser"));
        log.debug("addUser url = " + uriInfo.getAbsolutePath().getPort());
        log.debug(String.format("realm = %s id = %s", realm, user));
//        String host = uriInfo.getAbsolutePath().getHost();
//        String port = uriInfo.getAbsolutePath().getPort() + "";

        if (getProperties(String.format("%s.json", p_realm))) {
            Response res = getUser(realm, user.getUsername());
            //TODO: доработка
            res.getEntity();

            log.debug(String.format("addUser res => %s", res));
            if (res.getStatus() == 401) {
                // Если некоректный токен
                return Response.status(Response.Status.UNAUTHORIZED).entity("Bearer").build();
            } else {
                getEM();
                JSONParser parser = new JSONParser();
                try {
                    log.debug("res11 => " + (String) res.getEntity());
                    if (((String) res.getEntity()).contains("Realm not found")) {
                        return Response.status(Response.Status.NOT_FOUND).entity("Realm not found").build();
                    }
                    Object obj = parser.parse((String) res.getEntity());
                    JSONArray arr = (JSONArray) obj;
                    if (arr.size() == 0) {
                        // Если такого пользовтеля нет в БД добавляем
                        UserEntity tempUser = new UserEntity();
                        if (user.getAttributes().get("description") != null) {
                            tempUser.setDescription(user.getAttributes().get("description"));
                        }
                        if (user.getEmail() != null) {
                            tempUser.setEmail(user.getEmail());
                        }
                        if (user.getFirstName() != null) {
                            tempUser.setFirstName(user.getFirstName());
                        }
                        if (user.getLastName() != null) {
                            tempUser.setLastName(user.getLastName());
                        }
                        if (user.getUsername() != null) {
                            tempUser.setUsername(user.getUsername());
                        }
                        if (user.getAttributes().get("thirdName") != null) {
                            tempUser.setThirdName(user.getAttributes().get("thirdName"));
                        }
                        if (user.getAttributes().get("phone") != null) {
                            tempUser.setPhone(user.getAttributes().get("phone"));
                        }
                        if (user.getAttributes().get("hash") != null) {
                            tempUser.setHash(user.getAttributes().get("hash"));
                        }
                        if (user.getAttributes().get("hash_type") != null) {
                            tempUser.setHash_type(user.getAttributes().get("hash_type"));
                        }
                        if (user.getAttributes().get("salt") != null) {
                            tempUser.setSalt(user.getAttributes().get("salt"));
                        }
                        if (user.getAttributes().get("region") != null) {
                            try {
                                tempUser.setUser_region(Integer.parseInt(user.getAttributes().get("region")));
                            } catch (Exception ex1) {
                                log.debug(Level.ERROR, ex1);
                            }
                        }

                        tempUser.setUser_status(0);
                        tempUser.setEnabled(true);
                        tempUser.setCreate_date(new Date());
                        List<UserAttribute> userAttrList = new LinkedList();
                        HashMap<String, String> hm = user.getAttributes();

                        for (Map.Entry<String, String> entry : hm.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (key.contains("id_app_")) {
                                UserAttribute tempAttr = new UserAttribute();
                                tempAttr.setName(key);
                                tempAttr.setValue(value);
                                tempAttr.setVisible_flag(true);
                                tempAttr.setUserId(tempUser);
                                userAttrList.add(tempAttr);
                            }
                        }

                        tempUser.setUserAttributeCollection(userAttrList);

                        try {
                            em.getTransaction().begin();
                            em.merge(tempUser);
                            em.getTransaction().commit();
                            result = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                            result = e.getClass().getName();
                        }
                    } else {
                        // Если такой пользователь существует
                        result = "{\n"
                                + "    \"errorMessage\": \"User exists with same username\"\n"
                                + "}";
                        return Response.status(Response.Status.CONFLICT).entity(result).build();
                    }
                } catch (ParseException ex) {
                    log.log(Level.ERROR, ex);
                    return Response.status(Response.Status.NOT_MODIFIED).entity("Database error").build();
                }
                em.clear();
                em.close();
            }

            return Response.status(Response.Status.OK).entity(result).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }

    }

    /**
     *
     * @param userID
     * @param user
     * @return
     */
    @Path("/realms/{realm}/users/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("realm") String p_realm, @PathParam("id") String userID, keycloakUser user) {
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "updateUser"));
        log.debug("url = " + uriInfo.getAbsolutePath().getPort());
        log.debug(String.format("realm = %s id = %s", realm, userID));
//        String host = uriInfo.getAbsolutePath().getHost();
//        String port = uriInfo.getAbsolutePath().getPort() + "";
        if (getProperties(String.format("%s.json", p_realm))) {
            apiREST rest = new apiREST(getUserToken(), this.host, realm);
            int res = rest.updateUser(userID, user);
            return Response.status(res).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }
    }

    /**
     *
     * @param realm
     * @param username
     * @return
     */
    @Path("/realms/{realm}/users")
    @GET
    public Response getUser(@PathParam("realm") String p_realm, @QueryParam("username") @DefaultValue("") String username) {
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "getUser"));
        //log.debug("token => " + getUserToken());
        log.debug(String.format("url => %s", uriInfo.getAbsolutePath().getPort()));
        log.debug(String.format("realm => %s username = %s", realm, username));
        log.debug(String.format("absolutePath => %s", uriInfo.getAbsolutePath()));
        if (getProperties(String.format("%s.json", p_realm))) {
            log.debug(String.format("url => %s realm => %s", host, realm));
            apiREST rest = new apiREST(getUserToken(), this.host, realm);
            //TODO: Edit

            List params = new LinkedList();
            params.add(new BasicNameValuePair("username", username));
            JSONArray res = rest.getUsers(params);
            log.debug(String.format("getUser len = %s", res.size()));
            if ((res != null) && (res.size() > 0)) {
                if (res.get(0).toString().contains("Bearer")) {
                    log.debug("getUser res => Bearer");
                    return Response.status(Response.Status.UNAUTHORIZED).entity("Bearer").build();
                }

                if (res.get(0).toString().contains("Realm not")) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Realm not found").build();
                }
            }
            log.debug("getUser res => " + res);
            return Response.status(Response.Status.OK).entity(res.toJSONString()).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }
    }

    /**
     *
     * @param realm
     * @param userID
     * @param password
     * @return
     */
    @Path("/realms/{realm}/users/{id}/reset-password")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("realm") String p_realm, @PathParam("id") String userID, credentialRepresentation password) {
        String res = null;
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "resetPassword"));
        log.debug(String.format("url = ", uriInfo.getAbsolutePath().getPort()));
        log.debug(String.format("realm = %s id = %s", realm, userID));
//        String host = uriInfo.getAbsolutePath().getHost();
//        String port = uriInfo.getAbsolutePath().getPort() + "";
        if (getProperties(String.format("%s.json", p_realm))) {
            apiREST rest = new apiREST(getUserToken(), this.host, realm);
            int res1 = rest.changeUserPassword(userID, password.getValue());
            return Response.status(res1).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }
    }

    /**
     *
     * @param realm
     * @param userID
     * @param provider
     * @param ident
     * @return
     */
    @Path("/realms/{realm}/users/{id}/federated-identity/{provider}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response federatedIdentity(@PathParam("realm") String p_realm, @PathParam("id") String userID, @PathParam("provider") String provider, federatedIdentity ident) {
        String res = null;
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "federatedIdentity"));
        log.debug(String.format("realm = %s id=$s provider = %s ident = %s", realm, userID, provider, ident));
        if (getProperties(String.format("%s.json", p_realm))) {
            apiREST rest = new apiREST(getUserToken(), this.host, this.realm);
            res = rest.federatedIdentity(userID, ident);
            log.debug(String.format("res => %s", res));
            if ((res != null) && (res.contains("Bearer"))) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(res).build();
            } else {
                return Response.status(Response.Status.OK).entity(res).build();
            }
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }
    }

    @Path("/realms/{realm}/protocol/openid-connect/token")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response getToken(@PathParam("realm") String p_realm,
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("client_id") String client_id,
            @FormParam("grant_type") String grant_type,
            @FormParam("client_secret") String client_secret) {
        String res = null;
        log.debug(String.format("\n\n********************* %s  %s *********************", new Date(), "federatedIdentity"));
        
        
        if (getProperties(String.format("%s.json", p_realm))) {
            System.out.println(String.format("p_realm = %s realm = %s client_d = %s", p_realm, this.realm, client_id));
            // проверяем realm на соответствие 
            if (this.realm.equals(p_realm)) {
                apiREST rest = new apiREST(username, password, client_id, client_secret, this.host, this.realm);
                res = rest.getToken_json();
                return Response.status(Response.Status.OK).entity(res).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Realm not found").build();
            }
        } else {
            return Response.status(Response.Status.CONFLICT).entity("Property not fount").build();
        }

    }

    private boolean getProperties(String filename) {
        boolean res = false;
        try {
            System.out.println("getProperties()");
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            try (InputStream input = classLoader.getResourceAsStream(filename)) {
                String json = IOUtils.toString(input, "UTF-8");

                JSONParser parser = new JSONParser();
                try {
                    Object obj = parser.parse(json);
                    JSONObject jsonObj = (JSONObject) obj;
                    this.host = (String) jsonObj.get("auth-server-url");
                    this.realm = (String) jsonObj.get("realm");
                    System.out.println("host = " + this.host);
                    System.out.println("realm = " + this.realm);
                    res = true;
                } catch (ParseException ex) {
                    log.log(Level.ERROR, ex);
                    res = false;
                }
            } catch (Exception ex2) {
                log.log(Level.ERROR, ex2);
                res = false;
            }
        } catch (Exception ex1) {
            log.log(Level.ERROR, ex1);
            res = false;
        }
        System.out.println("res => " + res);
        return res;
    }
}
