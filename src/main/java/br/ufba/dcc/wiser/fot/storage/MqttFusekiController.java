package br.ufba.dcc.wiser.fot.storage;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import br.ufba.dcc.wiser.fot.storage.schema.FiestaIoT;
import br.ufba.dcc.wiser.fot.storage.schema.SSN;

public class MqttFusekiController implements MqttCallback {

	public static String topic = "dev/#";

	private String brokerUrl;
	private String brokerPort;
	private String serverId;
	private String username;
	private String password;
	private String fusekiURI;
	private String baseURI;
	private MqttClient subscriber;

	public void init() {
		MqttConnectOptions connOpt = new MqttConnectOptions();

		try {
			if (!this.username.isEmpty())
				connOpt.setUserName(this.username);
			if (!this.password.isEmpty())
				connOpt.setPassword(this.password.toCharArray());
			long unixTime = System.currentTimeMillis() / 1000L;
			this.subscriber = new MqttClient("tcp://" + this.brokerUrl + ":"
					+ this.brokerPort, this.serverId + unixTime);
			this.subscriber.setCallback(this);
			this.subscriber.connect(connOpt);
			this.subscriber.subscribe(topic, 1);

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void disconnect() {
		try {
			this.subscriber.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connectionLost(Throwable arg0) {
		MqttConnectOptions connOpt = new MqttConnectOptions();
		try {
			if (!this.username.isEmpty())
				connOpt.setUserName(this.username);
			if (!this.password.isEmpty())
				connOpt.setPassword(this.password.toCharArray());
			long unixTime = System.currentTimeMillis() / 1000L;
			this.subscriber = new MqttClient(this.brokerUrl + ":"
					+ this.brokerPort, this.serverId + unixTime);
			this.subscriber.setCallback(this);
			this.subscriber.connect(connOpt);
			this.subscriber.subscribe(topic, 1);

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	public synchronized void messageArrived(String topic, final MqttMessage message)
			throws Exception {
		new Thread(new Runnable() {
			public void run() {
				String messageContent = new String(message.getPayload());
				try {
					JSONObject json = new JSONObject(messageContent);
					if ((json.get("CODE").toString().contentEquals("POST"))
							&& json.getJSONObject("BODY") != null) {

						Date date = new Date();
						Model model = buildTriples(json, date);
						model.write(System.out, "RDF/XML");
						updateTripleStore(model, getFusekiURI());
					}
				} catch (org.json.JSONException e) {
				}
			}
		}).start();
	}

	private synchronized Model buildTriples(JSONObject json, Date dateTime) {
		Model model2 = ModelFactory.createDefaultModel();
		OntModel model = ModelFactory.createOntologyModel();
		// "{\"CODE\":\"POST\",\"HEADER\":{\"NAME\":\"ufbaino01\"},\"BODY\":{\"temperatureSensor\":\"27\"}}"
		String sensorName = json.getJSONObject("BODY").keys().next().toString();
		String sensorFullName = json.getJSONObject("HEADER").getString("NAME")
				+ "_" + sensorName;

		long unixTime = System.currentTimeMillis() / 1000L;

		Individual observationValue = model.createIndividual(this.baseURI
				+ "obsValue" + unixTime, SSN.ObservationValue);

		Literal valueLiteral = model.createTypedLiteral(
				json.getJSONObject("BODY").get(sensorName).toString(),
				XSDDatatype.XSDdouble);
		observationValue.addLiteral(FiestaIoT.hasDataValue, valueLiteral);

		Individual sensorOutput = model.createIndividual(this.baseURI
				+ "sensorOutput" + unixTime, SSN.SensorOutput);
		sensorOutput.addProperty(SSN.hasValue, observationValue);

		Individual timeInterval = model.createIndividual(this.baseURI
				+ "timeInterval" + unixTime, FiestaIoT.classTimeInterval);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		String dateFormated = sdf.format(dateTime);
		Literal dateLiteral = model.createTypedLiteral(dateFormated,
				XSDDatatype.XSDdate);
		timeInterval.addLiteral(FiestaIoT.hasIntervalDate, dateLiteral);

		Individual observation = model.createIndividual(this.baseURI + "obs"
				+ unixTime, SSN.Observation);
		observation.addProperty(SSN.observationSamplingTime, timeInterval);
		observation.addProperty(SSN.observationResult, sensorOutput);

		Resource sensor = model.createResource(SSN.NS + sensorFullName);
		sensor.addProperty(SSN.madeObservation, observation);

		return model;
	}

	private synchronized void updateTripleStore(Model model, String tripleStoreURI) {
		DatasetAccessor accessor = DatasetAccessorFactory
				.createHTTP(tripleStoreURI);
		accessor.add(model);

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

	public void setFusekiURI(String fusekiURI) {
		this.fusekiURI = fusekiURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public String getFusekiURI() {
		return fusekiURI;
	}

	public String getBaseURI() {
		return baseURI;
	}

}
