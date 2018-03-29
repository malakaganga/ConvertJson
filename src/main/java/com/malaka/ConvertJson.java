package com.malaka;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Class mediator for JSON transformation.
 *
 * @author malaka
 */
public class ConvertJson extends AbstractMediator {

    /**
     * Holds the name.
     */
    private String nameParam;

    /**
     * Mediate overridden method to set the token property.
     */

    public boolean mediate(MessageContext context) {
        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JsonUtil.writeAsJson(context.getEnvelope().getBody().getFirstElement(), outputStream);
            String payload = outputStream.toString();
            JSONObject jsonBody = new JSONObject(payload);

// Adding the name:nameParam.
            Integer x = (Integer) jsonBody.get("name");
            nameParam = String.valueOf(x);
            jsonBody.put("name", nameParam);

            String transformedJson = jsonBody.toString();

            SOAPEnvelope soapEnvelope = new SOAP11Factory().getDefaultEnvelope();
            try {
                context.setEnvelope(soapEnvelope);
                JsonUtil.newJsonPayload(((Axis2MessageContext) context).getAxis2MessageContext(), transformedJson,
                        true, true);
                context.setProperty("JSON_OBJECT", jsonBody);
            } catch (AxisFault axisFault) {
                throw new RuntimeException(axisFault);
            }
// Setting the new json payload.

        } catch (Exception e) {
            System.err.println("Errorrred: " + e);
            return false;
        }

        return true;
    }
}