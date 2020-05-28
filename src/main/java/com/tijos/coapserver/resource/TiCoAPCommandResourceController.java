package com.tijos.coapserver.resource;

import com.tijos.coapserver.service.DeviceCommandCacheService;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the command resource "cmd" , only accept GET request from device
 */
public class TiCoAPCommandResourceController extends CoapResource{

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(TiCoAPCommandResourceController.class.getName());

    public TiCoAPCommandResourceController(){
        // set resource identifier
        super("command");

        // set display name
        getAttributes().setTitle("device-cmd Resource");

    }

    @Override
    public void handleGET(CoapExchange exchange) {

        LOGGER.log(Level.INFO, "handleGET");
        OptionSet options =  exchange.getRequestOptions();

        String uriQuery = options.getUriQueryString();

       LOGGER.log(Level.INFO, uriQuery);
       String [] params = uriQuery.split("=");

       String deviceId = params[1];
        LOGGER.log(Level.INFO, "deviceId " + deviceId);

        try {
            //Fetch a cached command
            String command = DeviceCommandCacheService.getInstance().pollDeviceCommand(deviceId);

            LOGGER.log(Level.INFO, "command" + command);

            Response response = new Response(CoAP.ResponseCode.CONTENT);
            response.setPayload(command);

            exchange.respond(response);
        }
        catch (Exception ex) {
            exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
            LOGGER.log(Level.SEVERE, ex.toString());

        }
    }

}

