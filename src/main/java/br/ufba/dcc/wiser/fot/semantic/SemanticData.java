package br.ufba.dcc.wiser.fot.semantic;

import java.sql.Timestamp;
import java.util.Date;

public class SemanticData {
	
	private String deviceName;
	private String sensorName;
	private String collectedValue;
	private Timestamp beginDate;
	private Timestamp endDate;
	
	public SemanticData(String deviceName, String sensorName,
			String collectedValue, Timestamp beginDate, Timestamp endDate) {
		super();
		this.deviceName = deviceName;
		this.sensorName = sensorName;
		this.collectedValue = collectedValue;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getCollectedValue() {
		return collectedValue;
	}

	public void setCollectedValue(String collectedValue) {
		this.collectedValue = collectedValue;
	}

	public Timestamp getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Timestamp beginDate) {
		this.beginDate = beginDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}
	
	
	
	
	

}
