package com.tijos.coapserver.service;

import com.tijos.coapserver.entity.TiDeviceCommandCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Device command cache, there is a queue for each device
 */
@Component
public class DeviceCommandCacheService {

    private  static Logger LOGGER = Logger.getLogger(DeviceCommandCacheService.class.getName());

    private HashMap<String, TiDeviceCommandCache> deviceCommandCacheMap = new HashMap<>();

    private static DeviceCommandCacheService instance;
    public static DeviceCommandCacheService getInstance() {
        if(instance == null) {
            instance = new DeviceCommandCacheService();
        }
        return instance;
    }

    /**
     * Add a new device command
     * @param deviceId  device path
     * @param devCommand command in json
     */
    public void addDeviceCommand(String deviceId, String devCommand) {

        LOGGER.info("addDeviceCommand " +  deviceId + " " + devCommand);

        TiDeviceCommandCache commandCache = deviceCommandCacheMap.get(deviceId);
        if(commandCache == null) {
            commandCache = new TiDeviceCommandCache();
            deviceCommandCacheMap.put(deviceId, commandCache);
        }

        commandCache.addCommand(devCommand);
    }

    /**
     * poll a command from the queue
     * @param deviceId device path
     * @return command in json
     */
    public String pollDeviceCommand(String deviceId) {

        LOGGER.info("pollDeviceCommand " +  deviceId);

        TiDeviceCommandCache commandCache = deviceCommandCacheMap.get(deviceId);
        if (commandCache == null) {
            return "";
        }

        String cmdString = commandCache.popCommand();
        if (commandCache.isEmpty()) {
            deviceCommandCacheMap.remove(deviceId);
        }

        return cmdString;
    }

    /**
     * get the oldest command from the queue
     */
    public  String getDeviceCommand(String deviceId) {
        LOGGER.info("getDeviceCommand " +  deviceId);

        TiDeviceCommandCache commandCache = deviceCommandCacheMap.get(deviceId);
        if (commandCache == null) {
            return "";
        }

        return  commandCache.getCommand();
    }

    /**
     * Get cached command number of the device
     * @param deviceId device path
     * @return number of command
     */
    public int getDeviceCommandNum(String deviceId) {
        LOGGER.info("getDeviceCommandNum " + deviceId);

        TiDeviceCommandCache commandCache = deviceCommandCacheMap.get(deviceId);
        if (commandCache == null) {
            return 0;
        }

        return commandCache.getCommandNumber();
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void clearExpiredCommandTask() {
        LOGGER.info("clearExpiredCommandTask ");

        try {
            for (TiDeviceCommandCache devCache : deviceCommandCacheMap.values()) {
                devCache.clearExpiredCommand();
            }
        }
        catch (Exception ex){
            LOGGER.log(Level.SEVERE, ex.toString());
        }
    }
}
