package com.github.gtowey.rundeck.plugins.chatops;

import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.dtolabs.rundeck.plugins.step.StepPlugin;
import be.fluid_it.tools.rundeck.plugins.util.ExpandUtil;
import org.boon.HTTP;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.Signature;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.util.Base64;
import java.util.Random;
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Plugin(name = ChatopsWorkflowStepPlugin.SERVICE_PROVIDER_NAME, service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = "ChatOps RPC Plugin", description = "Performs a ChatOps RPC API call")
public class ChatopsWorkflowStepPlugin implements StepPlugin {
    public static final String SERVICE_PROVIDER_NAME = "eu.europa.ec.ercea.backoffice.tools.rundeck.plugins.rest.RestPostWorkflowStepPlugin";

    @PluginProperty(title = "ChatOps URL", description = "ChatOps RPC API Endpoint to Use", required = true)
    private String remoteURL;

    @PluginProperty(title = "ChatOps Method", description = "The method to invoke", required = true)
    private String method;

    @PluginProperty(title = "Command Arguments", description = "(syntax name1=value1 name2=value2 ...", required = false)
    private String postParameters;

    @PluginProperty(title = "User", description = "User account to use", required = true)
    private String postUser;

    @PluginProperty(title = "Room ID", description = "Slack channel to write output", required = true)
    private String roomId;

    @Override
    public void executeStep(PluginStepContext context, Map<String, Object> configuration) throws StepException {
        Map<String, Map<String, String>> dataContext = context.getExecutionContext().getDataContext();

        String user = context.getExecutionContext().getUser();
        String expandedRemoteUrl = String.format("%s/%s",
          ExpandUtil.expand(remoteURL, context.getExecutionContext()),
          method);

        /**
          Create JSON Body Object with options + parameters
        **/
        JSONObject obj = new JSONObject();
        obj.put("user", postUser );
        obj.put("method", method );
        obj.put("room_id", roomId);
        JSONObject params = new JSONObject();
        String expandedPostParameters = null;
        if (postParameters != null) {
            expandedPostParameters = ExpandUtil.expand(postParameters, context.getExecutionContext());
            for (String postNameValue: expandedPostParameters.split(" ")) {
                if (postNameValue.contains("=")) {
                    String[] nameValue =  postNameValue.split("=");
                    if (nameValue.length == 2) {
                        params.put(nameValue[0], nameValue[1]);
                    }
                }
            };
        }
        obj.put("params", params);
        String body = obj.toJSONString();

        /**
          Do some debugging output for runtime logs
        **/
        StringBuffer buffer = new StringBuffer("Posting to [");
        buffer.append(expandedRemoteUrl).append("]");
        if (postParameters != null) {
            buffer.append(" with parameters [").append(expandedPostParameters).append("]");
        }
        buffer.append(" ...");
        System.out.println(buffer);

        /**
          Add request headers, including generating ChatOps RPC signature
        **/
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization",
          String.format("Basic %s",
            Base64.getEncoder().encodeToString(
              String.format("_:%s", System.getenv("CHATOPS_AUTH_TOKEN")).getBytes()
            )
          )
        );

        String nonce = this.getNonce();
        String timestamp = this.getTimestamp();
        String signatureString = String.format("%s\n%s\n%s\n%s", expandedRemoteUrl, nonce, timestamp, body);
        headers.put("Chatops-Nonce", nonce);
        headers.put("Chatops-Timestamp", timestamp);

        try {
          headers.put("Chatops-Signature",
            String.format("Signature keyid=shell,signature=%s",
              this.sign(signatureString, this.getPrivateKey())
            )
          );
        } catch (Exception e) {
          System.out.println("Exception found generating Chatops-Signature");
          System.out.println(e);
          e.printStackTrace();
          return;
        }

        /**
          Send the request!
        **/
        String result = HTTP.postWithHeaders(expandedRemoteUrl, headers, body);

        try {
          JSONParser parser = new JSONParser();
          Object robj = parser.parse(result);
          JSONObject r = (JSONObject) robj;
          if (r.containsKey("result")) {
            System.out.println(r.get("result"));
          } else {
            System.out.println(r);
          }
        } catch (Exception e) {
          System.out.println(result);
        }

    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
      Signature privateSignature = Signature.getInstance("SHA256withRSA");
      privateSignature.initSign(privateKey);
      privateSignature.update(plainText.getBytes(StandardCharsets.UTF_8.name()));

      byte[] signature = privateSignature.sign();

      return Base64.getEncoder().encodeToString(signature);
    }

    private String getNonce() {
      byte[] b = new byte[20];
      new Random().nextBytes(b);
      return Base64.getEncoder().encodeToString(b);
    }

    private String getTimestamp() {
      TimeZone tz = TimeZone.getTimeZone("UTC");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
      df.setTimeZone(tz);
      return df.format(new Date());
    }

    public static PrivateKey getPrivateKey() throws Exception {
       // Read in the key into a String
       StringBuilder pkcs8Lines = new StringBuilder();
       BufferedReader rdr = new BufferedReader(new StringReader(System.getenv("CHATOPS_PRIVATE_KEY")));
       String line;
       while ((line = rdr.readLine()) != null) {
           pkcs8Lines.append(line);
       }

       // Remove the "BEGIN" and "END" lines, as well as any whitespace
       String pkcs8Pem = pkcs8Lines.toString();
       pkcs8Pem = pkcs8Pem.replace("-----BEGIN RSA PRIVATE KEY-----", "");
       pkcs8Pem = pkcs8Pem.replace("-----END RSA PRIVATE KEY-----", "");
       pkcs8Pem = pkcs8Pem.replaceAll("\\s+","");

       // Base64 decode the result
       byte [] pkcs8EncodedBytes = Base64.getMimeDecoder().decode(pkcs8Pem);

       // extract the private key
       PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
       KeyFactory kf = KeyFactory.getInstance("RSA");
       return kf.generatePrivate(keySpec);
   }
}
