package br.ufba.dcc.wiser.fot.storage;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttCxfController {

	public static String topicPrefix = "dev/";

	private List<Bus> busList;
	private String brokerUrl;
	private String brokerPort;
	private String serverId;
	private String username;
	private String password;
	private int collect;
	private int publish;
	private MqttClient publisher;

	public MqttCxfController() {
	}

	public List<Bus> getBusList() {
		return busList;
	}

	public void setBusList(List<Bus> busList) {
		this.busList = busList;
	}

	public void init() {
		MqttConnectOptions connOpt = new MqttConnectOptions();
		if (!this.username.isEmpty())
			connOpt.setUserName(this.username);
		if (!this.password.isEmpty())
			connOpt.setPassword(this.password.toCharArray());
		try {
			long unixTime = System.currentTimeMillis() / 1000L;
			publisher = new MqttClient("tcp://" + this.brokerUrl + ":"
					+ this.brokerPort, this.serverId + "_pub" + unixTime);
			publisher.connect(connOpt);
			getServicesAndBuildMsgs();
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void getServicesAndBuildMsgs()
			throws InterruptedException {
		for (Bus b : busList) {
			ServerRegistry reg = b.getExtension(ServerRegistry.class);
			List<Server> servers = reg.getServers();
			
			for (Server serv : servers) {
				Thread.sleep(3000); // to keep ufbaino alive ;)
				String deviceName;
				String sensorName;
				String qname = serv.getEndpoint().getEndpointInfo().getName()
						.getLocalPart();
				if (serv.getEndpoint().containsKey("deviceName")) {
					deviceName = serv.getEndpoint().get("deviceName")
							.toString();
					if (serv.getEndpoint().containsKey("sensorName")) {
						sensorName = serv.getEndpoint().get("sensorName")
								.toString();
						publishTATUFlowRequest(deviceName, sensorName, this.collect, this.publish);
					} else {
						System.out.println("ERROR: Service " + qname
								+ " do not have 'sensorName' property.");
						System.exit(1);
					}
				} else {
					System.out.println("ERROR: Service " + qname
							+ " do not have 'deviceName' property.");
					System.exit(1);
				}
			}
		}
	}

	public void publishTATURequest(String deviceName, String sensorName) {

		String msgStr = "GET " + "INFO" + sensorName;

		MqttMessage msg = new MqttMessage();
		String topic = topicPrefix + deviceName;

		msg.setPayload(msgStr.getBytes());

		try {

			publisher.publish(topic, msg);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void publishTATUFlowRequest(String deviceName, String sensorName, int collect, int publish) {

		String msgStr = "FLOW " + "INFO " + sensorName + " {collect:" + collect + ",publish:" + publish + "}";

		MqttMessage msg = new MqttMessage();
		String topic = topicPrefix + deviceName;

		msg.setPayload(msgStr.getBytes());

		try {

			publisher.publish(topic, msg);
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		try {
			this.publisher.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public void setBrokerPort(String brokerPort) {
		this.brokerPort = brokerPort;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCollect(int collect) {
		this.collect = collect;
	}

	public void setPublish(int publish) {
		this.publish = publish;
	}
	
	

}
