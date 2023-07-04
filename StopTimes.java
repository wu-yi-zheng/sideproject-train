package train;

import java.sql.Date;

public class StopTimes {
	private int stopSequence;
	private String stationId;
	private Date arrivalTime;
	private Date departureTime;
	private int trainDailyId;
	
    public int getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public int getTrainDailyId() {
        return trainDailyId;
    }

    public void setTrainDailyId(int trainDailyId) {
        this.trainDailyId = trainDailyId;
    }
}
