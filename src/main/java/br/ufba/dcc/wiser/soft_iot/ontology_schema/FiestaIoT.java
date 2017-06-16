package br.ufba.dcc.wiser.soft_iot.ontology_schema;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

public class FiestaIoT {
	
	public static String M3_LITE = "http://purl.org/iot/vocab/m3-lite#";
	public static String M3_LITE_NS = "m3-lite";
	
	
	public static String IOT_LITE = "http://purl.oclc.org/NET/UNIS/fiware/iot-lite#";
	public static String IOT_LITE_NS = "iot-lite";
	
	public static String SSN = "http://purl.oclc.org/NET/ssnx/ssn#";
	public static String SSN_NS = "ssn";
	
	public static String DUL = "http://www.loa-cnr.it/ontologies/DUL.owl#";
	public static String DUL_NS = "dul";
	
	public static String W3_GEO = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static String W3_GEO_NS = "geo";
	
	//Resources
	
	private static OntModel m_model = ModelFactory.createOntologyModel();
	
	public static final Resource classTimeInterval = m_model.createResource( DUL + "TimeInterval" );
	
	public static final Resource Coverage = m_model.createResource( IOT_LITE + "Coverage" );
	
	public static final Resource Point = m_model.createResource( W3_GEO + "Point" );
	

	public static final Resource MeasurementType = m_model.createResource( M3_LITE + "MeasurementType" );
	public static final Resource AutomaticSensing = m_model.createResource( M3_LITE + "AutomaticSensing" );
	public static final Resource Experiment = m_model.createResource( M3_LITE + "Experiment" );
	public static final Resource Invalid = m_model.createResource( M3_LITE + "Invalid" );
	public static final Resource Manual = m_model.createResource( M3_LITE + "Manual" );
	
	public static final Resource Accelerometer = m_model.createResource( M3_LITE + "Accelerometer" );
	public static final Resource AirPollutantSensor = m_model.createResource( M3_LITE + "AirPollutantSensor" );
	public static final Resource AirThermometer = m_model.createResource( M3_LITE + "AirThermometer" );
	public static final Resource AlcoholLevelSensor = m_model.createResource( M3_LITE + "AlcoholLevelSensor" );
	public static final Resource AtmosphericPressureSensor = m_model.createResource( M3_LITE + "AtmosphericPressureSensor" );
	public static final Resource BloodPressureSensor = m_model.createResource( M3_LITE + "BloodPressureSensor" );
	public static final Resource BoardThermometer = m_model.createResource( M3_LITE + "BoardThermometer" );
	public static final Resource BodyThermometer = m_model.createResource( M3_LITE + "BodyThermometer" );
	public static final Resource CholesterolSensor = m_model.createResource( M3_LITE + "CholesterolSensor" );
	public static final Resource CloudCoverSensor = m_model.createResource( M3_LITE + "CloudCoverSensor" );
	public static final Resource ConductivitySensor = m_model.createResource( M3_LITE + "ConductivitySensor" );
	public static final Resource DeltaDewPointSensor = m_model.createResource( M3_LITE + "DeltaDewPointSensor" );
	public static final Resource DewPointSensor = m_model.createResource( M3_LITE + "DewPointSensor" );
	public static final Resource DistanceSensor = m_model.createResource( M3_LITE + "DistanceSensor" );
	public static final Resource ECG = m_model.createResource( M3_LITE + "ECG" );
	public static final Resource ElectricalSensor = m_model.createResource( M3_LITE + "ElectricalSensor" );
	public static final Resource EnergyMeter = m_model.createResource( M3_LITE + "EnergyMeter" );
	public static final Resource FallDetector = m_model.createResource( M3_LITE + "FallDetector" );
	public static final Resource FrequencySensor = m_model.createResource( M3_LITE + "FrequencySensor" );
	public static final Resource FuelLevel = m_model.createResource( M3_LITE + "FuelLevel" );
	public static final Resource GPSSensor = m_model.createResource( M3_LITE + "GPSSensor" );
	public static final Resource GasDetector = m_model.createResource( M3_LITE + "GasDetector" );
	public static final Resource Glucometer = m_model.createResource( M3_LITE + "Glucometer" );
	public static final Resource GyrometerSensor = m_model.createResource( M3_LITE + "GyrometerSensor" );
	public static final Resource GyroscopeSensor = m_model.createResource( M3_LITE + "GyroscopeSensor" );
	public static final Resource HeartBeatSensor = m_model.createResource( M3_LITE + "HeartBeatSensor" );
	public static final Resource HumiditySensor = m_model.createResource( M3_LITE + "HumiditySensor" );
	public static final Resource Hydrophone = m_model.createResource( M3_LITE + "Hydrophone" );
	public static final Resource ImageSensor = m_model.createResource( M3_LITE + "ImageSensor" );
	public static final Resource LeafWetnessSensor = m_model.createResource( M3_LITE + "LeafWetnessSensor" );
	public static final Resource LightSensor = m_model.createResource( M3_LITE + "LightSensor" );
	public static final Resource Magnetometer = m_model.createResource( M3_LITE + "Magnetometer" );
	public static final Resource OccupancyDetector = m_model.createResource( M3_LITE + "OccupancyDetector" );
	public static final Resource Odometer = m_model.createResource( M3_LITE + "Odometer" );
	public static final Resource PHSensor = m_model.createResource( M3_LITE + "PHSensor" );
	public static final Resource Pedometer = m_model.createResource( M3_LITE + "Pedometer" );
	public static final Resource PrecipitationSensor = m_model.createResource( M3_LITE + "PrecipitationSensor" );
	public static final Resource PressureSensor = m_model.createResource( M3_LITE + "PressureSensor" );
	public static final Resource ProximitySensor = m_model.createResource( M3_LITE + "ProximitySensor" );
	public static final Resource PulseOxymeter = m_model.createResource( M3_LITE + "PulseOxymeter" );
	public static final Resource RoadSurfaceThermometer = m_model.createResource( M3_LITE + "RoadSurfaceThermometer" );
	public static final Resource SaltMeter = m_model.createResource( M3_LITE + "SaltMeter" );
	public static final Resource Seismometer = m_model.createResource( M3_LITE + "Seismometer" );
	public static final Resource ShakeSensor = m_model.createResource( M3_LITE + "ShakeSensor" );
	public static final Resource SkinConductanceSensor = m_model.createResource( M3_LITE + "SkinConductanceSensor" );
	public static final Resource SmokeDetector = m_model.createResource( M3_LITE + "SmokeDetector" );
	public static final Resource SoilHumiditySensor = m_model.createResource( M3_LITE + "SoilHumiditySensor" );
	public static final Resource SolarRadiationSensor = m_model.createResource( M3_LITE + "SolarRadiationSensor" );
	public static final Resource SoundSensor = m_model.createResource( M3_LITE + "SoundSensor" );
	public static final Resource SpeedSensor = m_model.createResource( M3_LITE + "SpeedSensor" );
	public static final Resource SunPositionDirectionSensor = m_model.createResource( M3_LITE + "SunPositionDirectionSensor" );
	public static final Resource SunPositionElevationSensor = m_model.createResource( M3_LITE + "SunPositionElevationSensor" );
	public static final Resource Thermometer = m_model.createResource( M3_LITE + "Thermometer" );
	public static final Resource ThrottleSensor = m_model.createResource( M3_LITE + "ThrottleSensor" );
	public static final Resource TouchSensor = m_model.createResource( M3_LITE + "TouchSensor" );
	public static final Resource UltrasonicSensor = m_model.createResource( M3_LITE + "UltrasonicSensor" );
	public static final Resource VehicleCountSensor = m_model.createResource( M3_LITE + "VehicleCountSensor" );
	public static final Resource VisibilitySensor = m_model.createResource( M3_LITE + "VisibilitySensor" );
	public static final Resource VoltageSensor = m_model.createResource( M3_LITE + "VoltageSensor" );
	public static final Resource WeightSensor = m_model.createResource( M3_LITE + "WeightSensor" );
	public static final Resource WindChillSensor = m_model.createResource( M3_LITE + "WindChillSensor" );
	public static final Resource WindDirectionSensor = m_model.createResource( M3_LITE + "WindDirectionSensor" );
	public static final Resource WindSpeedSensor = m_model.createResource( M3_LITE + "WindSpeedSensor" );

	public static final Resource Acceleration = m_model.createResource( M3_LITE + "Acceleration" );
	public static final Resource AccelerationInstantaneous = m_model.createResource( M3_LITE + "AccelerationInstantaneous" );
	public static final Resource AirPollution = m_model.createResource( M3_LITE + "AirPollution" );
	public static final Resource AirQuality = m_model.createResource( M3_LITE + "AirQuality" );
	public static final Resource AirTemperature = m_model.createResource( M3_LITE + "AirTemperature" );
	public static final Resource AlcoholLevel = m_model.createResource( M3_LITE + "AlcoholLevel" );
	public static final Resource Angular = m_model.createResource( M3_LITE + "Angular" );
	public static final Resource BatteryLevel = m_model.createResource( M3_LITE + "BatteryLevel" );
	public static final Resource BloodGlucose = m_model.createResource( M3_LITE + "BloodGlucose" );
	public static final Resource BloodPressure = m_model.createResource( M3_LITE + "BloodPressure" );
	public static final Resource CO2 = m_model.createResource( M3_LITE + "CO2" );
	public static final Resource Calcium = m_model.createResource( M3_LITE + "Calcium" );
	public static final Resource Capacitance = m_model.createResource( M3_LITE + "Capacitance" );
	public static final Resource ChemicalAgentAtmosphericConcentration = m_model.createResource( M3_LITE + "ChemicalAgentAtmosphericConcentration" );
	public static final Resource ChemicalAgentAtmosphericConcentrationAirParticles = m_model.createResource( M3_LITE + "ChemicalAgentAtmosphericConcentrationAirParticles" );
	public static final Resource ChemicalAgentAtmosphericConcentrationCO = m_model.createResource( M3_LITE + "ChemicalAgentAtmosphericConcentrationCO" );
	public static final Resource ChemicalAgentAtmosphericConcentrationNO2 = m_model.createResource( M3_LITE + "ChemicalAgentAtmosphericConcentrationNO2" );
	public static final Resource Cholesterol = m_model.createResource( M3_LITE + "Cholesterol" );
	public static final Resource CloudCover = m_model.createResource( M3_LITE + "CloudCover" );
	public static final Resource Conductivity = m_model.createResource( M3_LITE + "Conductivity" );
	public static final Resource DewPoint = m_model.createResource( M3_LITE + "DewPoint" );
	public static final Resource DewPointTemperature = m_model.createResource( M3_LITE + "DewPointTemperature" );
	public static final Resource Direction = m_model.createResource( M3_LITE + "Direction" );
	public static final Resource DirectionAzimuth = m_model.createResource( M3_LITE + "DirectionAzimuth" );
	public static final Resource DirectionHeading = m_model.createResource( M3_LITE + "DirectionHeading" );
	public static final Resource Distance = m_model.createResource( M3_LITE + "Distance" );
	public static final Resource ElectricCharge = m_model.createResource( M3_LITE + "ElectricCharge" );
	public static final Resource ElectricField = m_model.createResource( M3_LITE + "ElectricField" );
	public static final Resource ElectricField1800MHz = m_model.createResource( M3_LITE + "ElectricField1800MHz" );
	public static final Resource ElectricField2100MHz = m_model.createResource( M3_LITE + "ElectricField2100MHz" );
	public static final Resource ElectricField2400MHz = m_model.createResource( M3_LITE + "ElectricField2400MHz" );
	public static final Resource ElectricField900Mhz = m_model.createResource( M3_LITE + "ElectricField900Mhz" );
	public static final Resource ElectricPotential = m_model.createResource( M3_LITE + "ElectricPotential" );
	public static final Resource ElectricalResistance = m_model.createResource( M3_LITE + "ElectricalResistance" );
	public static final Resource Energy = m_model.createResource( M3_LITE + "Energy" );
	public static final Resource FillLevel = m_model.createResource( M3_LITE + "FillLevel" );
	public static final Resource FillLevelGasTank = m_model.createResource( M3_LITE + "FillLevelGasTank" );
	public static final Resource FillLevelWasteContainer = m_model.createResource( M3_LITE + "FillLevelWasteContainer" );
	public static final Resource Food = m_model.createResource( M3_LITE + "Food" );
	public static final Resource FoodTemperature = m_model.createResource( M3_LITE + "FoodTemperature" );
	public static final Resource Frequency = m_model.createResource( M3_LITE + "Frequency" );
	public static final Resource FuelConsumption = m_model.createResource( M3_LITE + "FuelConsumption" );
	public static final Resource FuelConsumptionInstantaneous = m_model.createResource( M3_LITE + "FuelConsumptionInstantaneous" );
	public static final Resource FuelConsumptionTotal = m_model.createResource( M3_LITE + "FuelConsumptionTotal" );
	public static final Resource HeartBeat = m_model.createResource( M3_LITE + "HeartBeat" );
	public static final Resource Humidity = m_model.createResource( M3_LITE + "Humidity" );
	public static final Resource Illuminance = m_model.createResource( M3_LITE + "Illuminance" );
	public static final Resource LeafWetness = m_model.createResource( M3_LITE + "LeafWetness" );
	public static final Resource Location = m_model.createResource( M3_LITE + "Location" );
	public static final Resource LuminousFlux = m_model.createResource( M3_LITE + "LuminousFlux" );
	public static final Resource LuminousIntensity = m_model.createResource( M3_LITE + "LuminousIntensity" );
	public static final Resource MagneticField = m_model.createResource( M3_LITE + "MagneticField" );
	public static final Resource MagneticFluxDensity = m_model.createResource( M3_LITE + "MagneticFluxDensity" );
	public static final Resource Mass = m_model.createResource( M3_LITE + "Mass" );
	public static final Resource Mileage = m_model.createResource( M3_LITE + "Mileage" );
	public static final Resource Motion = m_model.createResource( M3_LITE + "Motion" );
	public static final Resource MotionState = m_model.createResource( M3_LITE + "MotionState" );
	public static final Resource MotionStateVehicle = m_model.createResource( M3_LITE + "MotionStateVehicle" );
	public static final Resource NumberStep = m_model.createResource( M3_LITE + "NumberStep" );
	public static final Resource Others = m_model.createResource( M3_LITE + "Others" );
	public static final Resource PH = m_model.createResource( M3_LITE + "PH" );
	public static final Resource Position = m_model.createResource( M3_LITE + "Position" );
	public static final Resource PositionAltitude = m_model.createResource( M3_LITE + "PositionAltitude" );
	public static final Resource PositionLatitude = m_model.createResource( M3_LITE + "PositionLatitude" );
	public static final Resource PositionLongitude = m_model.createResource( M3_LITE + "PositionLongitude" );
	public static final Resource Potassium = m_model.createResource( M3_LITE + "Potassium" );
	public static final Resource Power = m_model.createResource( M3_LITE + "Power" );
	public static final Resource Precipitation = m_model.createResource( M3_LITE + "Precipitation" );
	public static final Resource Presence = m_model.createResource( M3_LITE + "Presence" );
	public static final Resource PresenceState = m_model.createResource( M3_LITE + "PresenceState" );
	public static final Resource PresenceStateDriverCard = m_model.createResource( M3_LITE + "PresenceStateDriverCard" );
	public static final Resource PresenceStateEmergencyVehicle = m_model.createResource( M3_LITE + "PresenceStateEmergencyVehicle" );
	public static final Resource PresenceStateParking = m_model.createResource( M3_LITE + "PresenceStateParking" );
	public static final Resource PresenceStatePeople = m_model.createResource( M3_LITE + "PresenceStatePeople" );
	public static final Resource Pressure = m_model.createResource( M3_LITE + "Pressure" );
	public static final Resource Proximity = m_model.createResource( M3_LITE + "Proximity" );
	public static final Resource RFIDQuantityKind = m_model.createResource( M3_LITE + "RFIDQuantityKind" );
	public static final Resource Rainfall = m_model.createResource( M3_LITE + "Rainfall" );
	public static final Resource RecognizedActivity = m_model.createResource( M3_LITE + "RecognizedActivity" );
	public static final Resource RelativeHumidity = m_model.createResource( M3_LITE + "RelativeHumidity" );
	public static final Resource RoadOccupancy = m_model.createResource( M3_LITE + "RoadOccupancy" );
	public static final Resource RotationalSpeed = m_model.createResource( M3_LITE + "RotationalSpeed" );
	public static final Resource RotationalSpeedEngine = m_model.createResource( M3_LITE + "RotationalSpeedEngine" );
	public static final Resource SPO2 = m_model.createResource( M3_LITE + "SPO2" );
	public static final Resource Salinity = m_model.createResource( M3_LITE + "Salinity" );
	public static final Resource SkinConductance = m_model.createResource( M3_LITE + "SkinConductance" );
	public static final Resource Sodium = m_model.createResource( M3_LITE + "Sodium" );
	public static final Resource SoilHumidity = m_model.createResource( M3_LITE + "SoilHumidity" );
	public static final Resource SoilMoistureTension = m_model.createResource( M3_LITE + "SoilMoistureTension" );
	public static final Resource SoilTemperature = m_model.createResource( M3_LITE + "SoilTemperature" );
	public static final Resource SolarRadiation = m_model.createResource( M3_LITE + "SolarRadiation" );
	public static final Resource Sound = m_model.createResource( M3_LITE + "Sound" );
	public static final Resource SoundPressureLevel = m_model.createResource( M3_LITE + "SoundPressureLevel" );
	public static final Resource SoundPressureLevelAmbient = m_model.createResource( M3_LITE + "SoundPressureLevelAmbient" );
	public static final Resource Speed = m_model.createResource( M3_LITE + "Speed" );
	public static final Resource SpeedAverage = m_model.createResource( M3_LITE + "SpeedAverage" );
	public static final Resource SpeedInstantaneous = m_model.createResource( M3_LITE + "SpeedInstantaneous" );
	public static final Resource SpeedMedian = m_model.createResource( M3_LITE + "SpeedMedian" );
	public static final Resource SunPositionDirection = m_model.createResource( M3_LITE + "SunPositionDirection" );
	public static final Resource SunPositionElevation = m_model.createResource( M3_LITE + "SunPositionElevation" );
	public static final Resource Temperature = m_model.createResource( M3_LITE + "Temperature" );
	public static final Resource TemperatureEngine = m_model.createResource( M3_LITE + "TemperatureEngine" );
	public static final Resource TemperatureWasteContainer = m_model.createResource( M3_LITE + "TemperatureWasteContainer" );
	public static final Resource TimeRelatedState = m_model.createResource( M3_LITE + "TimeRelatedState" );
	public static final Resource TimeRelatedStateDriver = m_model.createResource( M3_LITE + "TimeRelatedStateDriver" );
	public static final Resource Timestamp = m_model.createResource( M3_LITE + "Timestamp" );
	public static final Resource TrafficCongestion = m_model.createResource( M3_LITE + "TrafficCongestion" );
	public static final Resource TrafficIntensity = m_model.createResource( M3_LITE + "TrafficIntensity" );
	public static final Resource VehicleOverspeedState = m_model.createResource( M3_LITE + "VehicleOverspeedState" );
	public static final Resource Visibility = m_model.createResource( M3_LITE + "Visibility" );
	public static final Resource WaterLevel = m_model.createResource( M3_LITE + "WaterLevel" );
	public static final Resource WaterTemperature = m_model.createResource( M3_LITE + "WaterTemperature" );
	public static final Resource Weight = m_model.createResource( M3_LITE + "Weight" );
	public static final Resource WindChill = m_model.createResource( M3_LITE + "WindChill" );
	public static final Resource WindDirection = m_model.createResource( M3_LITE + "WindDirection" );
	public static final Resource WindSpeed = m_model.createResource( M3_LITE + "WindSpeed" );
	public static final Resource WorkingState = m_model.createResource( M3_LITE + "WorkingState" );
	public static final Resource WorkingStateDriver = m_model.createResource( M3_LITE + "WorkingStateDriver" );

	public static final Resource Altitude = m_model.createResource( M3_LITE + "Altitude" );
	public static final Resource Ampere = m_model.createResource( M3_LITE + "Ampere" );
	public static final Resource BeatPerMinute = m_model.createResource( M3_LITE + "BeatPerMinute" );
	public static final Resource Candela = m_model.createResource( M3_LITE + "Candela" );
	public static final Resource Coulomb = m_model.createResource( M3_LITE + "Coulomb" );
	public static final Resource Day = m_model.createResource( M3_LITE + "Day" );
	public static final Resource Decibel = m_model.createResource( M3_LITE + "Decibel" );
	public static final Resource Decibel_A = m_model.createResource( M3_LITE + "Decibel_A" );
	public static final Resource Degree = m_model.createResource( M3_LITE + "Degree" );
	public static final Resource DegreeCelsius = m_model.createResource( M3_LITE + "DegreeCelsius" );
	public static final Resource DegreeFahrenheit = m_model.createResource( M3_LITE + "DegreeFahrenheit" );
	public static final Resource Dimensionless = m_model.createResource( M3_LITE + "Dimensionless" );
	public static final Resource EAQI = m_model.createResource( M3_LITE + "EAQI" );
	public static final Resource Farad = m_model.createResource( M3_LITE + "Farad" );
	public static final Resource Gauss = m_model.createResource( M3_LITE + "Gauss" );
	public static final Resource Gram = m_model.createResource( M3_LITE + "Gram" );
	public static final Resource GramPerCubicMetre = m_model.createResource( M3_LITE + "GramPerCubicMetre" );
	public static final Resource GramPerLiter = m_model.createResource( M3_LITE + "GramPerLiter" );
	public static final Resource Hertz = m_model.createResource( M3_LITE + "Hertz" );
	public static final Resource Hour = m_model.createResource( M3_LITE + "Hour" );
	public static final Resource Inch = m_model.createResource( M3_LITE + "Inch" );
	public static final Resource Index = m_model.createResource( M3_LITE + "Index" );
	public static final Resource Kelvin = m_model.createResource( M3_LITE + "Kelvin" );
	public static final Resource Kilo = m_model.createResource( M3_LITE + "Kilo" );
	public static final Resource KiloWattHour = m_model.createResource( M3_LITE + "KiloWattHour" );
	public static final Resource Kilogram = m_model.createResource( M3_LITE + "Kilogram" );
	public static final Resource Latitude = m_model.createResource( M3_LITE + "Latitude" );
	public static final Resource Liter = m_model.createResource( M3_LITE + "Liter" );
	public static final Resource LitrePer100Kilometres = m_model.createResource( M3_LITE + "LitrePer100Kilometres" );
	public static final Resource Longitude = m_model.createResource( M3_LITE + "Longitude" );
	public static final Resource Lumen = m_model.createResource( M3_LITE + "Lumen" );
	public static final Resource Lux = m_model.createResource( M3_LITE + "Lux" );
	public static final Resource Meter = m_model.createResource( M3_LITE + "Meter" );
	public static final Resource MeterPerSecond = m_model.createResource( M3_LITE + "MeterPerSecond" );
	public static final Resource MeterPerSecondSquare = m_model.createResource( M3_LITE + "MeterPerSecondSquare" );
	public static final Resource Miles = m_model.createResource( M3_LITE + "Miles" );
	public static final Resource Milliampere = m_model.createResource( M3_LITE + "Milliampere" );
	public static final Resource Millibar = m_model.createResource( M3_LITE + "Millibar	" );
	public static final Resource MilligramPerSquareMetre = m_model.createResource( M3_LITE + "MilligramPerSquareMetre" );
	public static final Resource Millilitre = m_model.createResource( M3_LITE + "Millilitre" );
	public static final Resource MillimeterPerHour = m_model.createResource( M3_LITE + "MillimeterPerHour" );
	public static final Resource Millimetre = m_model.createResource( M3_LITE + "Millimetre" );
	public static final Resource Millivolt = m_model.createResource( M3_LITE + "Millivolt" );
	public static final Resource MillivoltPerMeter = m_model.createResource( M3_LITE + "MillivoltPerMeter" );
	public static final Resource Milliwatt = m_model.createResource( M3_LITE + "Milliwatt" );
	public static final Resource Minute_angle = m_model.createResource( M3_LITE + "Minute_angle" );
	public static final Resource Minute_time = m_model.createResource( M3_LITE + "Minute_time" );
	public static final Resource MmHg = m_model.createResource( M3_LITE + "MmHg" );
	public static final Resource MmolPerLiter = m_model.createResource( M3_LITE + "MmolPerLiter" );
	public static final Resource Ohm = m_model.createResource( M3_LITE + "Ohm" );
	public static final Resource Okta = m_model.createResource( M3_LITE + "Okta" );
	public static final Resource PPM = m_model.createResource( M3_LITE + "PPM" );
	public static final Resource Pascal = m_model.createResource( M3_LITE + "Pascal" );
	public static final Resource Percent = m_model.createResource( M3_LITE + "Percent" );
	public static final Resource Pound = m_model.createResource( M3_LITE + "Pound" );
	public static final Resource Radian = m_model.createResource( M3_LITE + "Radian" );
	public static final Resource RadianPerSecond = m_model.createResource( M3_LITE + "RadianPerSecond" );
	public static final Resource RevolutionsPerMinute = m_model.createResource( M3_LITE + "RevolutionsPerMinute" );
	public static final Resource Scale = m_model.createResource( M3_LITE + "Scale" );
	public static final Resource Second_angle = m_model.createResource( M3_LITE + "Second_angle" );
	public static final Resource Second_time = m_model.createResource( M3_LITE + "Second_time" );
	public static final Resource Tesla = m_model.createResource( M3_LITE + "Tesla" );
	public static final Resource Time = m_model.createResource( M3_LITE + "Time" );
	public static final Resource Tonne = m_model.createResource( M3_LITE + "Tonne" );
	public static final Resource Var = m_model.createResource( M3_LITE + "Var" );
	public static final Resource VehiclesPerMinute = m_model.createResource( M3_LITE + "VehiclesPerMinute" );
	public static final Resource Volt = m_model.createResource( M3_LITE + "Volt" );
	public static final Resource Volt_ampere_reactive = m_model.createResource( M3_LITE + "Volt-ampere_reactive" );
	public static final Resource Watt = m_model.createResource( M3_LITE + "Watt" );
	public static final Resource WattPerMeterSquare = m_model.createResource( M3_LITE + "WattPerMeterSquare" );
	public static final Resource WattPerSquareMeter = m_model.createResource( M3_LITE + "WattPerSquareMeter" );
	public static final Resource Wout = m_model.createResource( M3_LITE + "Wout" );
	public static final Resource Year = m_model.createResource( M3_LITE + "Year" );	
	
	
	
	
	
	//ObjectProperties
	
	public static final ObjectProperty geoLocation = m_model.createObjectProperty(W3_GEO + "location");
	
	public static final ObjectProperty hasCoverage = m_model.createObjectProperty(IOT_LITE + "hasCoverage");
	public static final ObjectProperty isSubSystemOf = m_model.createObjectProperty(IOT_LITE + "isSubSystemOf");
	public static final ObjectProperty hasQuantityKind = m_model.createObjectProperty(IOT_LITE + "hasQuantityKind");
	public static final ObjectProperty hasUnit = m_model.createObjectProperty(IOT_LITE + "hasUnit");
	
	public static final ObjectProperty hasMeasurementType = m_model.createObjectProperty(M3_LITE + "hasMeasurementType");
	
	
	
	
	//DataTypeProperties	
	
	public static final DatatypeProperty geoLat = m_model.createDatatypeProperty(W3_GEO + "lat");
	
	public static final DatatypeProperty geoLong = m_model.createDatatypeProperty(W3_GEO + "long");
	
	public static final DatatypeProperty hasDataValue = m_model.createDatatypeProperty(DUL + "hasDataValue");
	
	public static final DatatypeProperty hasIntervalDate = m_model.createDatatypeProperty(DUL + "hasIntervalDate");
	
	public static final DatatypeProperty iotLiteId = m_model.createDatatypeProperty(IOT_LITE + "id");

}
