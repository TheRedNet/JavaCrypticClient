package items;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import connection.WebSocketClient;
import information.Information;
import util.file.File;
import util.service.Portscan;
import util.service.Service;

import java.util.*;

public class Device {

    String uuid;
    WebSocketClient client = Information.webSocketClient;
    HardwareElement[] components;

    @Deprecated
    public Device(String uuid, WebSocketClient client){
        this.uuid = uuid;
        this.client = client;
    }

    public Device(String uuid){
        this.uuid = uuid;
    }

    public Map deviceInfo() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("device", "info");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", uuid);
        Map result = client.microservice("device", endpoint, data);
        //setComponents((List<Map>) result.get("hardware_type"));
        return result;
    }

    public String getUuid(){
        return uuid;
    }

    public String getName(){
        try {
            return (String) this.deviceInfo().get("name");
        } catch (UnknownMicroserviceException | InvalidServerResponseException e) {
            e.printStackTrace();
        }
        return "Error with the server";
    }

    public boolean isOnline() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("device", "ping");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", uuid);
        Map result = client.microservice("device", endpoint, data);
        return (boolean) result.get("online");
    }

    private Map changeStatus() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("device", "power");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", uuid);
        Map result = client.microservice("device", endpoint, data);
        return result;
    }

    public Map boot() throws InvalidServerResponseException, UnknownMicroserviceException {
        if(isOnline()) return null;
        return changeStatus();
    }

    public Map shutdown() throws InvalidServerResponseException, UnknownMicroserviceException {
        if(isOnline()) return changeStatus();
        return null;
    }

    public HardwareElement[] getElements() throws InvalidServerResponseException, UnknownMicroserviceException {
        if(components == null) deviceInfo();
        return components;
    }

    public Map changeName(String newName) throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("device", "change_name");
        Map<String, String> data= new HashMap<>();
        data.put("device_uuid", uuid);
        data.put("name", newName);
        return client.microservice("device", endpoint, data);
    }

    public Map getOwner() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Arrays.asList("device", "owner");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", uuid);
        return client.microservice("device", endpoint, data);
    }

    private void setComponents(List<Map> components){
        this.components = new HardwareElement[components.size()];
        for(Map component: components){
            HardwareElement element = new HardwareElement(component.get("hardware_type").toString(), component.get("hardware_element").toString());
            this.components[components.indexOf(component)] = element;
        }
    }

    public boolean hasAccess() throws UnknownMicroserviceException, InvalidServerResponseException {
        List<String> endpoint = Collections.singletonList("part_owner");
        Map<String, String> data = new HashMap<>();
        data.put("device_uuid", uuid);
        return (boolean) Information.webSocketClient.microservice("service", endpoint, data).get("ok");
    }

    public Portscan getPortscanService(){
        for(Service s: Service.getServiceList(this)){
            if(s instanceof Portscan)
                return (Portscan) s;
        }
        return null;
    }

    public File getRootDirectory() throws UnknownMicroserviceException, InvalidServerResponseException {
        return new File(null, null, true, this);
    }

    public static Device getRandomDevice(){
        List<String> endpoint = Arrays.asList("device", "spot");
        Map result;
        try {
            result = Information.webSocketClient.microservice("device", endpoint, new HashMap());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            return null;
        }
        return new Device(result.get("uuid").toString());
    }

    public static List<Device> getHackedDevices(){
        List<String> endpoint = Collections.singletonList("list_part_owner");
        Map result;
        try {
            result = Information.webSocketClient.microservice("service", endpoint, new HashMap<>());
        } catch (InvalidServerResponseException | UnknownMicroserviceException e) {
            e.printStackTrace();
            return null;
        }

        List<Device> ret = new ArrayList<>();
        for(Map m:(List<Map>)result.get("services")){
            ret.add(new Device(m.get("device").toString()));
        }

        return ret;
    }
}
