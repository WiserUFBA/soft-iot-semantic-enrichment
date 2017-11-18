package br.ufba.dcc.wiser.soft_iot.semantic_enrichment;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.ufba.dcc.wiser.soft_iot.entities.Device;
import br.ufba.dcc.wiser.soft_iot.entities.Sensor;
import br.ufba.dcc.wiser.soft_iot.entities.SensorData;
import br.ufba.dcc.wiser.soft_iot.local_storage.LocalDataController;
import br.ufba.dcc.wiser.soft_iot.mapping_devices.Controller;
import br.ufba.dcc.wiser.soft_iot.ontology_schema.FiestaIoT;
import br.ufba.dcc.wiser.soft_iot.ontology_schema.SSN;

public class H2LinkedDataController {
	
	private String fusekiURI;
	private String baseURI;
	private String nameSpacePrefix;
	private HashMap<String, Date> lastDateSensor;
	private Controller fotDevices;
	private LocalDataController localDataController;
	private boolean onlyForAggregatedData;
	private boolean debugModeValue;
	

	public void init() {
		OntModel model = ModelFactory.createOntologyModel();
		model = setNSPrefixies(model);
		boolean newDevice = false;
		
		for(Device device : fotDevices.getListDevices()){
			System.out.println("======= DEVICE: " + device.getId());
			try {
				if (!deviceExistsInbase(device.getId())){
					try {
						model = enrichDevice(device, model);
						newDevice = true;
					} catch (JsonParseException e) {
						e.printStackTrace();
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (newDevice){
			printlnDebug("New semantic description of devices: ");
			if (debugModeValue)	model.write(System.out, "RDFXML");
			updateTripleStore(model, this.fusekiURI);
		}else{
			printlnDebug("There are not new devices");
		}
		refreshLastIdDataRegisted();
	}
	
	private OntModel enrichDevice(Device device, OntModel model) throws JsonParseException, JsonMappingException, IOException{
	
		Individual indDevice = model.createIndividual(this.baseURI
			+ "device_" + device.getId(), SSN.Device);
		Literal idLiteral = model.createTypedLiteral(device.getId(),XSDDatatype.XSDstring);
		indDevice.addLiteral(FiestaIoT.iotLiteId, idLiteral);
		
		if(device.getLatitude() != 0 && device.getLongitude() != 0 ){
			Individual point = model.createIndividual(this.baseURI
					+ "device_" + device.getId() + "_coverage_point", FiestaIoT.Point);
			Literal latLiteral = model.createTypedLiteral(device.getLatitude(),XSDDatatype.XSDdouble);
			point.addLiteral(FiestaIoT.geoLat, latLiteral);
			
			Literal longLiteral = model.createTypedLiteral(device.getLongitude(),XSDDatatype.XSDdouble);
			point.addLiteral(FiestaIoT.geoLong, longLiteral);
			
			Individual coverage = model.createIndividual(this.baseURI
					+ "device_" + device.getId() + "_coverage", FiestaIoT.Coverage);
			coverage.addProperty(FiestaIoT.geoLocation, point);
			indDevice.addProperty(FiestaIoT.hasCoverage, coverage);
		}
		
		
		for (Sensor sensor : device.getSensors()) {
			Resource resourceSensorType = mapSensorTypetoFiestaSensingDevice(sensor.getType());
			if(resourceSensorType != null){
				Individual indSensor = model.createIndividual(this.baseURI
						+ "device_" + device.getId() + "_sensor_" + sensor.getId(), resourceSensorType);
				indSensor.addProperty(FiestaIoT.isSubSystemOf, indDevice);
				
				Resource resourceQuantityKind = mapSensorTypeToFiestaQuantityKind(sensor.getType());
				indSensor.addProperty(FiestaIoT.hasQuantityKind, resourceQuantityKind);
				
				Resource resourceUnitType = mapSensorTypeToFiestaUnitType(sensor.getType());
				indSensor.addProperty(FiestaIoT.hasUnit, resourceUnitType);
				
				indDevice.addProperty(SSN.hasSubSystem, indSensor);
			}
		}
		return model;
	}

	private void refreshLastIdDataRegisted() {
		this.lastDateSensor = new HashMap<String, Date>();
		for(Device device : fotDevices.getListDevices()){
			for(Sensor sensor : device.getSensors()){
				Date lastDate = localDataController.getLastDateOfEnrichedSensorData(device, sensor);
				if(lastDate == null){
					lastDate = new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime();
					localDataController.createFirstLastSensorDataEnriched(device, sensor, lastDate);
				}
				this.lastDateSensor.put(device.getId() + "_" + sensor.getId(), lastDate);
			}
		}
	}

	private boolean deviceExistsInbase(String id) throws ConnectException{
		String askQuery = "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> \n" +
							"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> \n" + 
							"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
							"PREFIX iot-lite: <http://purl.oclc.org/NET/UNIS/fiware/iot-lite#> \n" +
							"ASK \n" +
							"WHERE { \n" +  
							"?device rdf:type 	ssn:Device . \n" +
							"?device iot-lite:id \"" + id +"\" \n" +	
							"}";
		System.out.println(askQuery);
		Query query = QueryFactory.create(askQuery);
		QueryExecution qExe = QueryExecutionFactory.sparqlService(this.fusekiURI, query);
		System.out.println("AQUI!");
		boolean result = qExe.execAsk();
		printlnDebug("Device of id = " + id + " exists in base = " + result);
		return result;
		
	}

	public void buildTriples() {
		System.out.println("Starting procedure to enrich sensor data...");
		System.out.println(this.fusekiURI);
		for(Device device : fotDevices.getListDevices()){
			for(Sensor sensor : device.getSensors()){
				System.out.println("sensor: " + sensor);
				Date lastId = this.lastDateSensor.get(device.getId() + "_" + sensor.getId());
				List<SensorData> sensorData;
				if(onlyForAggregatedData){
					sensorData = localDataController.getSensorDataByAggregationStatusAndDate(device, sensor,1, lastId);
				}else{
					sensorData = localDataController.getSensorDataByLastDateTime(device, sensor, lastId);
				}
				System.out.println(sensorData.size());
				if(!sensorData.isEmpty()){
					OntModel model = ModelFactory.createOntologyModel();
					model = setNSPrefixies(model);
					model = buildFiestaIoTTriples(sensorData);
					printlnDebug("\tWriting in triple store server new data...");
					updateTripleStore(model, this.fusekiURI);
					Date newLastDate = sensorData.get(sensorData.size()-1).getStartTime();
					localDataController.updateLastSensorDataEnriched(device, sensor, newLastDate);
					this.lastDateSensor.put(device.getId() + "_" + sensor.getId(), newLastDate);
				}
			}
		}
	}

	private OntModel buildFiestaIoTTriples(List<SensorData> listSensorData){
		OntModel model = ModelFactory.createOntologyModel();
		
		for (SensorData sensorData : listSensorData) {
			String sensorId = sensorData.getSensor().getId();
			String deviceId = sensorData.getDevice().getId();
			String sensorDataId = "device_" + deviceId + "_sensor_" + sensorId + sensorData.getStartTime().getTime();
			
			Individual observationValue = model.createIndividual(this.baseURI
					+ sensorDataId + "_obsValue", SSN.ObservationValue);
			if (!sensorData.getValue().isEmpty()){
				Literal valueLiteral = model.createTypedLiteral(sensorData.getValue(),XSDDatatype.XSDdouble);
				observationValue.addLiteral(FiestaIoT.hasDataValue, valueLiteral);
			}
			
			Individual sensorOutput = model.createIndividual(this.baseURI
					+ sensorDataId + "_sensorOutput", SSN.SensorOutput);
			sensorOutput.addProperty(SSN.hasValue, observationValue);
			
			Individual startTimeInterval = model.createIndividual(this.baseURI + sensorDataId +
					"_startTimeInterval", FiestaIoT.classTimeInterval);
			Individual endTimeInterval = model.createIndividual(this.baseURI + sensorDataId +
					"_endTimeInterval", FiestaIoT.classTimeInterval);
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			beginCal.setTimeInMillis(sensorData.getStartTime().getTime());
			XSDDateTime beginDateTime = new XSDDateTime(beginCal);
			endCal.setTimeInMillis(sensorData.getEndTime().getTime());
			XSDDateTime endDateTime = new XSDDateTime(endCal);
			Literal dateLiteral = model.createTypedLiteral(beginDateTime,
					XSDDatatype.XSDdateTime);
			startTimeInterval.addLiteral(FiestaIoT.hasIntervalDate, dateLiteral);
			dateLiteral = model.createTypedLiteral(endDateTime,
					XSDDatatype.XSDdateTime);
			endTimeInterval.addLiteral(FiestaIoT.hasIntervalDate, dateLiteral);
			
			Individual observation = model.createIndividual(this.baseURI + sensorDataId +"_obs", SSN.Observation);
			observation.addProperty(SSN.startTime, startTimeInterval);
			observation.addProperty(SSN.endTime, endTimeInterval);
			observation.addProperty(SSN.observationResult, sensorOutput);
			
			Resource quatityKind = mapSensorTypeToFiestaQuantityKind(sensorData.getSensor().getType());
			observation.addProperty(SSN.observedProperty, quatityKind);
			
			Resource measurementType = mapSensorTypeToFiestaMeasurementType(sensorData.getSensor().getType());
			observation.addProperty(FiestaIoT.hasMeasurementType, measurementType);
			
			Resource resSensor = model.createResource(this.baseURI + "device_" + deviceId + "_sensor_" +
					sensorId);
			resSensor.addProperty(SSN.madeObservation, observation);
			observation.addProperty(SSN.observedBy, resSensor);
		}
		
		return model;
	}

	private synchronized void updateTripleStore(Model model,
			String tripleStoreURI) {
		DatasetAccessor accessor = DatasetAccessorFactory
				.createHTTP(tripleStoreURI);
		accessor.add(model);

	}
	
	private Resource mapSensorTypetoFiestaSensingDevice(String sensorType){
		if(sensorType.contentEquals("Accelerometer")){
		    return FiestaIoT.Accelerometer;
		}else if (sensorType.contentEquals("AirPollutantSensor")){
		    return FiestaIoT.AirPollutantSensor;
		}else if (sensorType.contentEquals("AirThermometer")){
		    return FiestaIoT.AirThermometer;
		}else if (sensorType.contentEquals("AlcoholLevelSensor")){
		    return FiestaIoT.AlcoholLevelSensor;
		}else if (sensorType.contentEquals("AtmosphericPressureSensor")){
		    return FiestaIoT.AtmosphericPressureSensor;
		}else if (sensorType.contentEquals("BloodPressureSensor")){
		    return FiestaIoT.BloodPressureSensor;
		}else if (sensorType.contentEquals("BoardThermometer")){
		    return FiestaIoT.BoardThermometer;
		}else if (sensorType.contentEquals("BodyThermometer")){
		    return FiestaIoT.BodyThermometer;
		}else if (sensorType.contentEquals("CholesterolSensor")){
		    return FiestaIoT.CholesterolSensor;
		}else if (sensorType.contentEquals("CloudCoverSensor")){
		    return FiestaIoT.CloudCoverSensor;
		}else if (sensorType.contentEquals("ConductivitySensor")){
		    return FiestaIoT.ConductivitySensor;
		}else if (sensorType.contentEquals("DeltaDewPointSensor")){
		    return FiestaIoT.DeltaDewPointSensor;
		}else if (sensorType.contentEquals("DewPointSensor")){
		    return FiestaIoT.DewPointSensor;
		}else if (sensorType.contentEquals("DistanceSensor")){
		    return FiestaIoT.DistanceSensor;
		}else if (sensorType.contentEquals("ECG")){
		    return FiestaIoT.ECG;
		}else if (sensorType.contentEquals("ElectricalSensor")){
		    return FiestaIoT.ElectricalSensor;
		}else if (sensorType.contentEquals("EnergyMeter")){
		    return FiestaIoT.EnergyMeter;
		}else if (sensorType.contentEquals("FallDetector")){
		    return FiestaIoT.FallDetector;
		}else if (sensorType.contentEquals("FrequencySensor")){
		    return FiestaIoT.FrequencySensor;
		}else if (sensorType.contentEquals("FuelLevel")){
		    return FiestaIoT.FuelLevel;
		}else if (sensorType.contentEquals("GPSSensor")){
		    return FiestaIoT.GPSSensor;
		}else if (sensorType.contentEquals("GasDetector")){
		    return FiestaIoT.GasDetector;
		}else if (sensorType.contentEquals("Glucometer")){
		    return FiestaIoT.Glucometer;
		}else if (sensorType.contentEquals("GyrometerSensor")){
		    return FiestaIoT.GyrometerSensor;
		}else if (sensorType.contentEquals("GyroscopeSensor")){
		    return FiestaIoT.GyroscopeSensor;
		}else if (sensorType.contentEquals("HeartBeatSensor")){
		    return FiestaIoT.HeartBeatSensor;
		}else if (sensorType.contentEquals("HumiditySensor")){
		    return FiestaIoT.HumiditySensor;
		}else if (sensorType.contentEquals("Hydrophone")){
		    return FiestaIoT.Hydrophone;
		}else if (sensorType.contentEquals("ImageSensor")){
		    return FiestaIoT.ImageSensor;
		}else if (sensorType.contentEquals("LeafWetnessSensor")){
		    return FiestaIoT.LeafWetnessSensor;
		}else if (sensorType.contentEquals("LightSensor")){
		    return FiestaIoT.LightSensor;
		}else if (sensorType.contentEquals("Magnetometer")){
		    return FiestaIoT.Magnetometer;
		}else if (sensorType.contentEquals("OccupancyDetector")){
		    return FiestaIoT.OccupancyDetector;
		}else if (sensorType.contentEquals("Odometer")){
		    return FiestaIoT.Odometer;
		}else if (sensorType.contentEquals("PHSensor")){
		    return FiestaIoT.PHSensor;
		}else if (sensorType.contentEquals("Pedometer")){
		    return FiestaIoT.Pedometer;
		}else if (sensorType.contentEquals("PrecipitationSensor")){
		    return FiestaIoT.PrecipitationSensor;
		}else if (sensorType.contentEquals("PressureSensor")){
		    return FiestaIoT.PressureSensor;
		}else if (sensorType.contentEquals("ProximitySensor")){
		    return FiestaIoT.ProximitySensor;
		}else if (sensorType.contentEquals("PulseOxymeter")){
		    return FiestaIoT.PulseOxymeter;
		}else if (sensorType.contentEquals("RoadSurfaceThermometer")){
		    return FiestaIoT.RoadSurfaceThermometer;
		}else if (sensorType.contentEquals("SaltMeter")){
		    return FiestaIoT.SaltMeter;
		}else if (sensorType.contentEquals("Seismometer")){
		    return FiestaIoT.Seismometer;
		}else if (sensorType.contentEquals("ShakeSensor")){
		    return FiestaIoT.ShakeSensor;
		}else if (sensorType.contentEquals("SkinConductanceSensor")){
		    return FiestaIoT.SkinConductanceSensor;
		}else if (sensorType.contentEquals("SmokeDetector")){
		    return FiestaIoT.SmokeDetector;
		}else if (sensorType.contentEquals("SoilHumiditySensor")){
		    return FiestaIoT.SoilHumiditySensor;
		}else if (sensorType.contentEquals("SolarRadiationSensor")){
		    return FiestaIoT.SolarRadiationSensor;
		}else if (sensorType.contentEquals("SoundSensor")){
		    return FiestaIoT.SoundSensor;
		}else if (sensorType.contentEquals("SpeedSensor")){
		    return FiestaIoT.SpeedSensor;
		}else if (sensorType.contentEquals("SunPositionDirectionSensor")){
		    return FiestaIoT.SunPositionDirectionSensor;
		}else if (sensorType.contentEquals("SunPositionElevationSensor")){
		    return FiestaIoT.SunPositionElevationSensor;
		}else if (sensorType.contentEquals("Thermometer")){
		    return FiestaIoT.Thermometer;
		}else if (sensorType.contentEquals("ThrottleSensor")){
		    return FiestaIoT.ThrottleSensor;
		}else if (sensorType.contentEquals("TouchSensor")){
		    return FiestaIoT.TouchSensor;
		}else if (sensorType.contentEquals("UltrasonicSensor")){
		    return FiestaIoT.UltrasonicSensor;
		}else if (sensorType.contentEquals("VehicleCountSensor")){
		    return FiestaIoT.VehicleCountSensor;
		}else if (sensorType.contentEquals("VisibilitySensor")){
		    return FiestaIoT.VisibilitySensor;
		}else if (sensorType.contentEquals("VoltageSensor")){
		    return FiestaIoT.VoltageSensor;
		}else if (sensorType.contentEquals("WeightSensor")){
		    return FiestaIoT.WeightSensor;
		}else if (sensorType.contentEquals("WindChillSensor")){
		    return FiestaIoT.WindChillSensor;
		}else if (sensorType.contentEquals("WindDirectionSensor")){
		    return FiestaIoT.WindDirectionSensor;
		}else if (sensorType.contentEquals("WindSpeedSensor")){
		    return FiestaIoT.WindSpeedSensor;
		}
		return null;
		
	}
	
	private Resource mapSensorTypeToFiestaQuantityKind(String sensorType){
		if(sensorType.contentEquals("Accelerometer")){
		    return FiestaIoT.Acceleration;
		}else if (sensorType.contentEquals("AirPollutantSensor")){
		    return FiestaIoT.AirPollution;
		}else if (sensorType.contentEquals("AirThermometer")){
		    return FiestaIoT.AirTemperature;
		}else if (sensorType.contentEquals("AlcoholLevelSensor")){
		    return FiestaIoT.AlcoholLevel;
		}else if (sensorType.contentEquals("AtmosphericPressureSensor")){
		    return FiestaIoT.Pressure;
		}else if (sensorType.contentEquals("BloodPressureSensor")){
		    return FiestaIoT.BloodPressure;
		}else if (sensorType.contentEquals("BoardThermometer")){
		    return FiestaIoT.Temperature;
		}else if (sensorType.contentEquals("CholesterolSensor")){
		    return FiestaIoT.Cholesterol;
		}else if (sensorType.contentEquals("CloudCoverSensor")){
		    return FiestaIoT.CloudCover;
		}else if (sensorType.contentEquals("ConductivitySensor")){
		    return FiestaIoT.Conductivity;
		}else if (sensorType.contentEquals("DeltaDewPointSensor")){
		    return FiestaIoT.DewPoint;
		}else if (sensorType.contentEquals("DewPointSensor")){
		    return FiestaIoT.DewPoint;
		}else if (sensorType.contentEquals("DistanceSensor")){
		    return FiestaIoT.Distance;
		}else if (sensorType.contentEquals("ECG")){
		    return FiestaIoT.HeartBeat;
		}else if (sensorType.contentEquals("ElectricalSensor")){
		    return FiestaIoT.ElectricField;
		}else if (sensorType.contentEquals("EnergyMeter")){
		    return FiestaIoT.Energy;
		}else if (sensorType.contentEquals("FallDetector")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("FrequencySensor")){
		    return FiestaIoT.Frequency;
		}else if (sensorType.contentEquals("FuelLevel")){
		    return FiestaIoT.FuelConsumption;
		}else if (sensorType.contentEquals("GPSSensor")){
		    return FiestaIoT.Position;
		}else if (sensorType.contentEquals("GasDetector")){
		    return FiestaIoT.ChemicalAgentAtmosphericConcentration;
		}else if (sensorType.contentEquals("Glucometer")){
		    return FiestaIoT.BloodGlucose;
		}else if (sensorType.contentEquals("GyrometerSensor")){
		    return FiestaIoT.Angular;
		}else if (sensorType.contentEquals("GyroscopeSensor")){
		    return FiestaIoT.Direction;
		}else if (sensorType.contentEquals("HeartBeatSensor")){
		    return FiestaIoT.HeartBeat;
		}else if (sensorType.contentEquals("HumiditySensor")){
		    return FiestaIoT.Humidity;
		}else if (sensorType.contentEquals("Hydrophone")){
		    return FiestaIoT.Sound;
		}else if (sensorType.contentEquals("ImageSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("LeafWetnessSensor")){
		    return FiestaIoT.LeafWetness;
		}else if (sensorType.contentEquals("LightSensor")){
		    return FiestaIoT.LuminousIntensity;
		}else if (sensorType.contentEquals("Magnetometer")){
		    return FiestaIoT.MagneticField;
		}else if (sensorType.contentEquals("OccupancyDetector")){
		    return FiestaIoT.Presence;
		}else if (sensorType.contentEquals("Odometer")){
		    return FiestaIoT.Distance;
		}else if (sensorType.contentEquals("PHSensor")){
		    return FiestaIoT.PH;
		}else if (sensorType.contentEquals("Pedometer")){
		    return FiestaIoT.NumberStep;
		}else if (sensorType.contentEquals("PrecipitationSensor")){
		    return FiestaIoT.Precipitation;
		}else if (sensorType.contentEquals("PressureSensor")){
		    return FiestaIoT.Pressure;
		}else if (sensorType.contentEquals("ProximitySensor")){
		    return FiestaIoT.Proximity;
		}else if (sensorType.contentEquals("PulseOxymeter")){
		    return FiestaIoT.SPO2;
		}else if (sensorType.contentEquals("RoadSurfaceThermometer")){
		    return FiestaIoT.Temperature;
		}else if (sensorType.contentEquals("SaltMeter")){
		    return FiestaIoT.Salinity;
		}else if (sensorType.contentEquals("Seismometer")){
		    return FiestaIoT.Motion;
		}else if (sensorType.contentEquals("ShakeSensor")){
		    return FiestaIoT.Motion;
		}else if (sensorType.contentEquals("SkinConductanceSensor")){
		    return FiestaIoT.SkinConductance;
		}else if (sensorType.contentEquals("SmokeDetector")){
		    return FiestaIoT.ChemicalAgentAtmosphericConcentrationCO;
		}else if (sensorType.contentEquals("SoilHumiditySensor")){
		    return FiestaIoT.SoilHumidity;
		}else if (sensorType.contentEquals("SolarRadiationSensor")){
		    return FiestaIoT.SolarRadiation;
		}else if (sensorType.contentEquals("SoundSensor")){
		    return FiestaIoT.Sound;
		}else if (sensorType.contentEquals("SpeedSensor")){
		    return FiestaIoT.Speed;
		}else if (sensorType.contentEquals("SunPositionDirectionSensor")){
		    return FiestaIoT.SunPositionDirection;
		}else if (sensorType.contentEquals("SunPositionElevationSensor")){
		    return FiestaIoT.SunPositionElevation;
		}else if (sensorType.contentEquals("Thermometer")){
		    return FiestaIoT.Temperature;
		}else if (sensorType.contentEquals("ThrottleSensor")){
		    return FiestaIoT.MotionStateVehicle;
		}else if (sensorType.contentEquals("TouchSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("UltrasonicSensor")){
		    return FiestaIoT.Distance;
		}else if (sensorType.contentEquals("VehicleCountSensor")){
		    return FiestaIoT.TrafficIntensity;
		}else if (sensorType.contentEquals("VisibilitySensor")){
		    return FiestaIoT.Visibility;
		}else if (sensorType.contentEquals("VoltageSensor")){
		    return FiestaIoT.ElectricPotential;
		}else if (sensorType.contentEquals("WeightSensor")){
		    return FiestaIoT.Weight;
		}else if (sensorType.contentEquals("WindChillSensor")){
		    return FiestaIoT.WindChill;
		}else if (sensorType.contentEquals("WindDirectionSensor")){
		    return FiestaIoT.WindDirection;
		}else if (sensorType.contentEquals("WindSpeedSensor")){
		    return FiestaIoT.WindSpeed;
		}
		return null;
	}
	
	private Resource mapSensorTypeToFiestaUnitType(String sensorType){
		if(sensorType.contentEquals("Accelerometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("AirPollutantSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("AirThermometer")){
		    return FiestaIoT.DegreeCelsius;
		}else if (sensorType.contentEquals("AlcoholLevelSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("AtmosphericPressureSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("BloodPressureSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("BoardThermometer")){
		    return FiestaIoT.DegreeCelsius;
		}else if (sensorType.contentEquals("CholesterolSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("CloudCoverSensor")){
		    return FiestaIoT.Percent;
		}else if (sensorType.contentEquals("ConductivitySensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("DeltaDewPointSensor")){
		    return FiestaIoT.DegreeCelsius;
		}else if (sensorType.contentEquals("DewPointSensor")){
		    return FiestaIoT.DegreeCelsius;
		}else if (sensorType.contentEquals("DistanceSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("ECG")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("ElectricalSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("EnergyMeter")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("FallDetector")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("FrequencySensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("FuelLevel")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("GPSSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("GasDetector")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Glucometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("GyrometerSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("GyroscopeSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("HeartBeatSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("HumiditySensor")){
		    return FiestaIoT.Percent;
		}else if (sensorType.contentEquals("Hydrophone")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("ImageSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("LeafWetnessSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("LightSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Magnetometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("OccupancyDetector")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Odometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("PHSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Pedometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("PrecipitationSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("PressureSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("ProximitySensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("PulseOxymeter")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("RoadSurfaceThermometer")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SaltMeter")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Seismometer")){
		    return FiestaIoT.Motion;
		}else if (sensorType.contentEquals("ShakeSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SkinConductanceSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SmokeDetector")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SoilHumiditySensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SolarRadiationSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SoundSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SpeedSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SunPositionDirectionSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("SunPositionElevationSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("Thermometer")){
		    return FiestaIoT.DegreeCelsius;
		}else if (sensorType.contentEquals("ThrottleSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("TouchSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("UltrasonicSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("VehicleCountSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("VisibilitySensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("VoltageSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("WeightSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("WindChillSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("WindDirectionSensor")){
		    return FiestaIoT.Others;
		}else if (sensorType.contentEquals("WindSpeedSensor")){
		    return FiestaIoT.Others;
		}
		return null;
	}
	
	private Resource mapSensorTypeToFiestaMeasurementType(String sensorType){
		return FiestaIoT.AutomaticSensing;
	}
	
	private OntModel setNSPrefixies(OntModel model){
		model.setNsPrefix(SSN.NameSpace, SSN.NS);
		
		model.setNsPrefix(FiestaIoT.M3_LITE_NS, FiestaIoT.M3_LITE);
		model.setNsPrefix(FiestaIoT.IOT_LITE_NS, FiestaIoT.IOT_LITE);
		model.setNsPrefix(FiestaIoT.DUL_NS, FiestaIoT.DUL);
		model.setNsPrefix(FiestaIoT.W3_GEO_NS, FiestaIoT.W3_GEO);
		model.setNsPrefix(nameSpacePrefix, baseURI );
		return model;
	}
	
	private void printlnDebug(String str){
		if (debugModeValue)
			System.out.println(str);
	}

	public void setOnlyForAggregatedData(boolean onlyForAggregatedData) {
		this.onlyForAggregatedData = onlyForAggregatedData;
	}

	public void setFusekiURI(String fusekiURI) {
		this.fusekiURI = fusekiURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	public void setNameSpacePrefix(String nameSpacePrefix) {
		this.nameSpacePrefix = nameSpacePrefix;
	}

	public void setLastDateSensor(HashMap<String, Date> lastDateSensor) {
		this.lastDateSensor = lastDateSensor;
	}

	public void setFotDevices(Controller fotDevices) {
		this.fotDevices = fotDevices;
	}

	public void setLocalDataController(LocalDataController localDataController) {
		this.localDataController = localDataController;
	}

	public void setDebugModeValue(boolean debugModeValue) {
		this.debugModeValue = debugModeValue;
	}
}
