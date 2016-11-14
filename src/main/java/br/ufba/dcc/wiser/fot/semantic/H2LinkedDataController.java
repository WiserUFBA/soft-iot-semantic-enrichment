package br.ufba.dcc.wiser.fot.semantic;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
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

import br.ufba.dcc.wiser.fot.semantic.schema.FiestaIoT;
import br.ufba.dcc.wiser.fot.semantic.schema.SSN;

public class H2LinkedDataController {

	private String fusekiURI;
	private String baseURI;
	private HashMap<String, Integer> lastId;
	private boolean aggregationPolices;
	private DataSource dataSource;
	private List<Bus> busList;
	private int aggregationTime;

	public void init() {

		try {
			Connection dbConnection = this.dataSource.getConnection();
			Statement stmt = dbConnection.createStatement();
			// stmt.execute("drop table semantic_collected_id_sensors");
			DatabaseMetaData dbMeta = dbConnection.getMetaData();
			System.out.println("Using datasource "
					+ dbMeta.getDatabaseProductName() + ", URL "
					+ dbMeta.getURL());
			stmt.execute("CREATE TABLE IF NOT EXISTS semantic_registered_id_sensors(sensor_name VARCHAR(255),"
					+ " device_name VARCHAR(255), last_id INT)");

			refreshLastIdDataRegisted();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// inicializar o ultimo id de cada gateway
	}

	private void writeResult(ResultSet rs, int columnCount) throws SQLException {
		for (int c = 1; c <= columnCount; c++) {
			System.out.print(rs.getString(c) + ", ");
		}
		System.out.println();
	}

	private void refreshLastIdDataRegisted() {
		Connection dbConnection;
		try {
			dbConnection = this.dataSource.getConnection();
			Statement stmt = dbConnection.createStatement();
			lastId = new HashMap<String, Integer>();
			for (Bus b : busList) {
				ServerRegistry reg = b.getExtension(ServerRegistry.class);
				List<Server> servers = reg.getServers();
				for (Server serv : servers) {
					String deviceName;
					String sensorName;
					String qname = serv.getEndpoint().getEndpointInfo()
							.getName().getLocalPart();
					if (serv.getEndpoint().containsKey("deviceName")) {
						deviceName = serv.getEndpoint().get("deviceName")
								.toString();
						if (serv.getEndpoint().containsKey("sensorName")) {
							sensorName = serv.getEndpoint().get("sensorName")
									.toString();
							ResultSet rs = stmt
									.executeQuery("SELECT last_id FROM semantic_registered_id_sensors"
											+ " WHERE device_name='"
											+ deviceName
											+ "' AND sensor_name='"
											+ sensorName + "'" + " LIMIT 1");
							if (rs.next()) {
								System.out.println("COM lastID");
								this.lastId.put(deviceName + "_" + sensorName,
										rs.getInt(1));
							} else {
								System.out.println("NO lastID");
								this.lastId.put(deviceName + "_" + sensorName,
										0);
								stmt.execute("INSERT INTO semantic_registered_id_sensors (sensor_name,device_name, last_id)"
										+ " VALUES ('" + sensorName + "','" + deviceName + "',0)");
							}
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
			dbConnection.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void disconnect() {

	}

	public void buildTriples() {
		Connection dbConnection;
		System.out.println("build triples...");
		try {
			dbConnection = this.dataSource.getConnection();
			Statement stmt = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
					   ResultSet.CONCUR_READ_ONLY);
			for (Bus b : busList) {
				ServerRegistry reg = b.getExtension(ServerRegistry.class);
				List<Server> servers = reg.getServers();
				for (Server serv : servers) {
					String deviceName;
					String sensorName;
					if (serv.getEndpoint().containsKey("deviceName")) {
						deviceName = serv.getEndpoint().get("deviceName")
								.toString();
						if (serv.getEndpoint().containsKey("sensorName")) {
							sensorName = serv.getEndpoint().get("sensorName")
									.toString();
							System.out.println("last id of "
									+ deviceName
									+ "_"
									+ sensorName
									+ ": "
									+ this.lastId.get(deviceName + "_"
											+ sensorName));
							String query = "SELECT * FROM sensors_data"
									+ " WHERE device_name='"+ deviceName + "' AND sensor_name='"
									+ sensorName + "'" + " AND id > "
									+ this.lastId.get(deviceName + "_" + sensorName)
									+ " ORDER BY time ASC";
							//System.out.println(query);
							ResultSet rs = stmt.executeQuery(query);
							
							if (rs.isBeforeFirst()){
								Model model = buildSemanticDataObj(rs, sensorName);
								updateTripleStore(model, this.fusekiURI);
								if (rs.isAfterLast() ){
									rs.previous();
									this.lastId.put(deviceName + "_" + sensorName, rs.getInt(1));
									System.out.println("-----> LAST ID: " + rs.getInt(1) );
									Statement stmtWrite = dbConnection.createStatement();
									stmtWrite.execute("UPDATE semantic_registered_id_sensors SET "
											+ "last_id=" + rs.getInt(1) + " WHERE sensor_name='" +
											sensorName + "' AND device_name='" + deviceName + "'");
								}
							}
						}
					}
				}
			}
			dbConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Model buildSemanticDataObj(ResultSet rs, String sensorType) throws SQLException {
		List<SemanticData> listData = new ArrayList<SemanticData>();
		if (this.aggregationPolices) {
			System.out.println("building triples with aggregation polices...");
			if(sensorType.contentEquals("temperatureSensor")){
				listData = buildMeanByMinuteInt(rs);
			}else if (sensorType.contentEquals("currentSensor01")){
				listData = buildSumByMinuteFloat(rs);
			}
		} else {
			System.out.println("building triples without aggregation polices...");
			listData = buildUnit(rs);
		}
		Model model = buildFiestaIoTTriples(listData);
		model.write(System.out, "RDFXML");
		
		return model;
	}
	
	private Model buildFiestaIoTTriples(List<SemanticData> listData){
		OntModel model = ModelFactory.createOntologyModel();
		
		for (SemanticData data : listData) {
			String sensorName = data.getSensorName();
			String sensorFullName = data.getDeviceName() + "_" + sensorName;
			long beginTimestamp = data.getBeginDate().getTime();
			long endTimestamp = data.getEndDate().getTime();
			
			long unixTime = System.currentTimeMillis() / 1000L;
			Individual observationValue = model.createIndividual(this.baseURI
					+ "obsValue_" + beginTimestamp + endTimestamp, SSN.ObservationValue);
			
			Literal valueLiteral = model.createTypedLiteral(data.getCollectedValue(),XSDDatatype.XSDdouble);
			observationValue.addLiteral(FiestaIoT.hasDataValue, valueLiteral);
			
			Individual sensorOutput = model.createIndividual(this.baseURI
					+ "sensorOutput_" + beginTimestamp + endTimestamp, SSN.SensorOutput);
			sensorOutput.addProperty(SSN.hasValue, observationValue);
			
			Individual startTimeInterval = model.createIndividual(this.baseURI
					+ "startTimeInterval" + beginTimestamp + endTimestamp, FiestaIoT.classTimeInterval);
			Individual endTimeInterval = model.createIndividual(this.baseURI
					+ "endTimeInterval" + beginTimestamp + endTimestamp, FiestaIoT.classTimeInterval);
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			beginCal.setTimeInMillis(data.getBeginDate().getTime());
			XSDDateTime beginDateTime = new XSDDateTime(beginCal);
			endCal.setTimeInMillis(data.getEndDate().getTime());
			XSDDateTime endDateTime = new XSDDateTime(endCal);
			
			//SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			//String dateFormated = sdf.format(data.getBeginDate());
			Literal dateLiteral = model.createTypedLiteral(beginDateTime,
					XSDDatatype.XSDdateTime);
			startTimeInterval.addLiteral(FiestaIoT.hasIntervalDate, dateLiteral);
			
			dateLiteral = model.createTypedLiteral(endDateTime,
					XSDDatatype.XSDdateTime);
			endTimeInterval.addLiteral(FiestaIoT.hasIntervalDate, dateLiteral);
			
			Individual observation = model.createIndividual(this.baseURI + "obs_" + beginTimestamp + endTimestamp, SSN.Observation);
			observation.addProperty(SSN.startTime, startTimeInterval);
			observation.addProperty(SSN.endTime, endTimeInterval);
			observation.addProperty(SSN.observationResult, sensorOutput);

			Resource sensor = model.createResource(SSN.NS + sensorFullName);
			sensor.addProperty(SSN.madeObservation, observation);
		}
		
		return model;
	}
	
	private List<SemanticData> buildMeanByMinuteInt(ResultSet rs) throws SQLException{
		List<SemanticData> listData = new ArrayList<SemanticData>();
		while (rs.next()) {
			Integer means = Integer.valueOf(rs.getString(4));
			Timestamp beginDate = rs.getTimestamp(5);
			Timestamp endDate = rs.getTimestamp(5);
			int count=1;
			if (rs.isLast()){
				SemanticData dt = new SemanticData(rs.getString(3), rs.getString(2), means.toString(), beginDate, endDate);
				listData.add(dt);
			}
			while (rs.next()) {
				if((endDate.getTime() - beginDate.getTime()) > this.aggregationTime){
					means /= count;
					SemanticData dt = new SemanticData(rs.getString(3), rs.getString(2), means.toString(), beginDate, endDate);
					listData.add(dt);
					rs.previous();
					break;
				}else{
					endDate = rs.getTimestamp(5);
					means += Integer.valueOf(rs.getString(4)); 
					count++;
				}
			}
		}
		return listData;
	}
	
	private List<SemanticData> buildSumByMinuteFloat(ResultSet rs) throws SQLException{
		List<SemanticData> listData = new ArrayList<SemanticData>();
		while (rs.next()) {
			Float sum = Float.valueOf(rs.getString(4));
			Timestamp beginDate = rs.getTimestamp(5);
			Timestamp endDate = rs.getTimestamp(5);
			
			if (rs.isLast()){
				SemanticData dt = new SemanticData(rs.getString(3), rs.getString(2), sum.toString(), beginDate, endDate);
				listData.add(dt);
			}
			while (rs.next()) {
				if((endDate.getTime() - beginDate.getTime()) > this.aggregationTime){
					SemanticData dt = new SemanticData(rs.getString(3), rs.getString(2), sum.toString(), beginDate, endDate);
					listData.add(dt);
					rs.previous();
					break;
				}else{
					endDate = rs.getTimestamp(5);
					sum += Float.valueOf(rs.getString(4)); 
				}
			}
		}
		return listData;
	}
	
	private List<SemanticData> buildUnit(ResultSet rs) throws SQLException{
		List<SemanticData> listData = new ArrayList<SemanticData>();
		while (rs.next()) {
			Timestamp beginDate = rs.getTimestamp(5);
			Timestamp endDate = rs.getTimestamp(5);
			SemanticData dt = new SemanticData(rs.getString(3), rs.getString(2), rs.getString(4), beginDate, endDate);
			listData.add(dt);
			
		}
		System.out.println(listData.size());
		return listData;
	}
	
/*
	public synchronized void messageArrived(String topic,
			final MqttMessage message) throws Exception {
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
*/
/*
	private synchronized Model buildTriples(JSONObject json, Date dateTime) {
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
*/
	private synchronized void updateTripleStore(Model model,
			String tripleStoreURI) {
		DatasetAccessor accessor = DatasetAccessorFactory
				.createHTTP(tripleStoreURI);
		accessor.add(model);

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

	public void setaggregationPolices(boolean aggregationPolices) {
		
		this.aggregationPolices = aggregationPolices;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setBusList(List<Bus> busList) {
		this.busList = busList;
	}

	public void setAggregationTime(int aggregationTime) {
		this.aggregationTime = aggregationTime;
	}

	
	
}
