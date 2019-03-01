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


    public static ConnectionPoint makeNS1FirewallObject1() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth0");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
    public static ConnectionPoint makeNS1FirewallObject2() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth1");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
    public static ConnectionPoint makeNS1FirewallObject3() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("eth2");
        cp.setType(ConnectionPointType.EXTERNAL);
        return cp;
    }
    public static ConnectionPoint makeNS1FirewallObject4() {
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

    public static ConnectionPoint makeNS1vPlateObject1() {
        ConnectionPoint cp = new ConnectionPoint();
        cp.setName("cp-eth0");
        cp.setType(ConnectionPointType.EXTERNAL);
        cp.setRequiredPort(80, 443);
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