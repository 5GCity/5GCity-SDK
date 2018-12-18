package it.nextworks.sdk;

import it.nextworks.sdk.enums.ConnectionPointType;

/**
 * Created by Marco Capitani on 05/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
public class ConnectionPointTest {

	public static ConnectionPoint makeFirewallDemobject1() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth0");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
	public static ConnectionPoint makeFirewallDemobject2() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth1");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
	public static ConnectionPoint makeFirewallDemobject3() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth2");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
	public static ConnectionPoint makeFirewallDemobject4() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth3");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(22, 80, 443);
        return cp;
    }
	
	public static ConnectionPoint makeMiniwebDemobject1() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("cp-eth0");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(80, 443);
        return cp;
    }

    public static ConnectionPoint makeTestObject1() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("MGMT");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(22, 80, 443);
        return cp;
    }

    public static ConnectionPoint makeTestObject2() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("VIDEO");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(80);
        return cp;
    }

    public static ConnectionPoint makeTestObject3() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("EXT");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(443);
        return cp;
    }

    public static ConnectionPoint makeTestObject4() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("DCNET");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(8000, 22);
        return cp;
    }

    public static ConnectionPoint makeTestObjectId(Long id) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("VIDEO");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(80);
        cp.setId(id);
        return cp;
    }

    public static ConnectionPoint parametrized(
        String name,
        ConnectionPointType type,
        Long internalCpId
    ) {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName(name);
        cp.setType(type);
        cp.setInternalCpId(internalCpId);
        cp.setRequiredPort(80, 22);
        return cp;
    }
}