package util.interpreter.classes;

import Exceptions.InvalidServerResponseException;
import Exceptions.UnknownMicroserviceException;
import Exceptions.interpreterExceptions.WrongServiceException;
import util.interpreter.annotations.UsableClass;
import util.interpreter.annotations.UsableConstructor;
import util.interpreter.annotations.UsableMethod;
import util.items.Device;
import util.service.SSH;
import util.service.Service;

@UsableClass(name = "SSH")
public class SSHClass extends SSH {

    @UsableConstructor
    public SSHClass(String serviceUuid, Device device) throws WrongServiceException {
        super(serviceUuid, device);

        if (!this.getInfo(false).get("name").toString().equalsIgnoreCase("ssh")) {
            throw new WrongServiceException("Not a SSH Service");
        }
    }

    public SSHClass(Service service) {
        super(service);
    }

    // Service Commands

    @Override
    @UsableMethod(name = "getPort")
    public int getRunningPort() {
        return super.getRunningPort();
    }

    @Override
    @UsableMethod(name = "getUuid")
    public String getUuid() {
        return super.getUuid();
    }

    @Override
    @UsableMethod(name = "getDevice")
    public Device getDevice() {
        return super.getDevice();
    }

    @Override
    @UsableMethod(name = "delete")
    public void delete() throws InvalidServerResponseException, UnknownMicroserviceException {
        super.delete();
    }
}