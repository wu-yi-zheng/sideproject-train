package train;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrainDailyInfo {
	private String trainDate;
	private String trainNo;
	private String startStationId;
	private String endStationId;
	private String trainTypeName;
	private int direction;
	private int tripLine;
	private int wheelchairFlag;
	private int packageServiceFlag;
	private int diningFlag;
	private int bikeFlag;
	private int breastFeedingFlag;
	private int dailyFlag;
	private int serviceAddedFlag;
	private String note;
	private List<StopTimes> stopTimes;
	
	public TrainDailyInfo(JSONObject jsonObject) {
        JSONObject jsonDailyInfo = jsonObject.getJSONObject("DailyTrainInfo");
        JSONArray stopTimesArr = jsonObject.getJSONArray("StopTimes");
        List<StopTimes> stopTimesList = new ArrayList<>();

        for (int i = 1; i <= stopTimesArr.length(); i++) {
            StopTimes stoptimes = new StopTimes();
            JSONObject stopTimeDetail = stopTimesArr.getJSONObject(i - 1);
            stoptimes.setStopSequence(i);
            stoptimes.setStationId(stopTimeDetail.getString("StationID"));
            stoptimes.setArrivalTime(getSQLFormatTime(stopTimeDetail.getString("ArrivalTime")));
            stoptimes.setDepartureTime(getSQLFormatTime(stopTimeDetail.getString("DepartureTime")));
            stopTimesList.add(stoptimes);
        }
        this.trainDate = jsonObject.getString("TrainDate");
        this.trainNo = jsonDailyInfo.getString("TrainNo");
        this.startStationId = jsonDailyInfo.getString("StartingStationID");
        this.endStationId = jsonDailyInfo.getString("EndingStationID");
        this.trainTypeName = jsonDailyInfo.getJSONObject("TrainTypeName").getString("Zh_tw");
        this.direction = jsonDailyInfo.getInt("Direction");
        this.tripLine = jsonDailyInfo.getInt("TripLine");
        this.wheelchairFlag = jsonDailyInfo.getInt("WheelchairFlag");
        this.packageServiceFlag = jsonDailyInfo.getInt("PackageServiceFlag");
        this.diningFlag = jsonDailyInfo.getInt("DiningFlag");
        this.bikeFlag = jsonDailyInfo.getInt("BikeFlag");
        this.breastFeedingFlag = jsonDailyInfo.getInt("BreastFeedingFlag");
        this.dailyFlag = jsonDailyInfo.getInt("DailyFlag");
        this.serviceAddedFlag = jsonDailyInfo.getInt("ServiceAddedFlag");
        this.note = jsonDailyInfo.getJSONObject("Note").getString("Zh_tw");
        this.stopTimes = stopTimesList;
	}
	
    public java.sql.Date getSQLFormatTime(String time) {
        String stoptime = null;
        Date date = new Date();
        String day = new SimpleDateFormat("yyyy-MM-dd").format(date);
        stoptime = day + " " + time;
        Date stoptime2 = null;
        try {
            stoptime2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(stoptime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        java.sql.Date sqltime = new java.sql.Date(stoptime2.getTime());
        return sqltime;
    }

	public String getTrainDate() {
		return trainDate;
	}

	public void setTrainDate(String trainDate) {
		this.trainDate = trainDate;
	}

	public String getTrainNo() {
		return trainNo;
	}

	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

	public String getStartStationId() {
		return startStationId;
	}

	public void setStartStationId(String startStationId) {
		this.startStationId = startStationId;
	}

	public String getEndStationId() {
		return endStationId;
	}

	public void setEndStationId(String endStationId) {
		this.endStationId = endStationId;
	}

	public String getTrainTypeName() {
		return trainTypeName;
	}

	public void setTrainTypeName(String trainTypeName) {
		this.trainTypeName = trainTypeName;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getTripLine() {
		return tripLine;
	}

	public void setTripLine(int tripLine) {
		this.tripLine = tripLine;
	}

	public int getWheelchairFlag() {
		return wheelchairFlag;
	}

	public void setWheelchairFlag(int wheelchairFlag) {
		this.wheelchairFlag = wheelchairFlag;
	}

	public int getPackageServiceFlag() {
		return packageServiceFlag;
	}

	public void setPackageServiceFlag(int packageServiceFlag) {
		this.packageServiceFlag = packageServiceFlag;
	}

	public int getDiningFlag() {
		return diningFlag;
	}

	public void setDiningFlag(int diningFlag) {
		this.diningFlag = diningFlag;
	}

	public int getBikeFlag() {
		return bikeFlag;
	}

	public void setBikeFlag(int bikeFlag) {
		this.bikeFlag = bikeFlag;
	}

	public int getBreastFeedingFlag() {
		return breastFeedingFlag;
	}

	public void setBreastFeedingFlag(int breastFeedingFlag) {
		this.breastFeedingFlag = breastFeedingFlag;
	}

	public int getDailyFlag() {
		return dailyFlag;
	}

	public void setDailyFlag(int dailyFlag) {
		this.dailyFlag = dailyFlag;
	}

	public int getServiceAddedFlag() {
		return serviceAddedFlag;
	}

	public void setServiceAddedFlag(int serviceAddedFlag) {
		this.serviceAddedFlag = serviceAddedFlag;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public List<StopTimes> getStopTimes() {
		return stopTimes;
	}

	public void setStopTimes(List<StopTimes> stopTimes) {
		this.stopTimes = stopTimes;
	}
	
}
