package it.nextworks.sdk;

import it.nextworks.sdk.enums.ConnectionPointType;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ConnectionPointTest {

    public static ConnectionPoint makeTestObject1(SdkFunction parent) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("MGMT");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setSdkFunction(parent);
        cp.setPort(22, 80, 443);
        return cp;
    }

    public static ConnectionPoint makeTestObject2(SdkFunction parent) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("VIDEO");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setSdkFunction(parent);
        cp.setPort(80);
        return cp;
    }

    public static ConnectionPoint makeTestObject3(SdkFunction parent) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("EXT");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setSdkFunction(parent);
        cp.setPort(443);
        return cp;
    }

    public static ConnectionPoint makeTestObject4(SdkFunction parent) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("DCNET");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setSdkFunction(parent);
        cp.setPort(8000, 22);
        return cp;
    }
}