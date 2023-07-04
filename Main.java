package train;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {
		Connection conn = getConn();
		if (conn != null) {
			try {
				Date date = new Date();
				date.toInstant();
				Calendar calendar = Calendar.getInstance();
				String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
				JSONArray jsonArray = getData(dateStr);
				if (jsonArray != null) {
					insertData(jsonArray, conn);
				}
				for (int i = 0; i < 3; i++) {
					calendar.add(Calendar.DATE, 1);
					date = calendar.getTime();
					dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
					jsonArray = getData(dateStr);
					if (jsonArray != null) {
						insertData(jsonArray, conn);
					}
				}
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("資料庫連接失敗");
		}
	}

	public static Connection getConn() {
		Connection conn = null;
		String jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=train;trustServerCertificate=true";
		String user = "watcher";
		String password = "P@ssw0rd";
		try {
			Class.forName(jdbcDriver);
			conn = DriverManager.getConnection(dbURL, user, password);
		} catch (Exception e) {
			e.printStackTrace();
			conn = null;
		}
		return conn;
	}

	public static JSONArray getData(String dateStr) {
		String trainDataURL = "https://ptx.transportdata.tw/MOTC/v2/Rail/TRA/DailyTimetable/TrainDate/" + dateStr
				+ "?$format=json";
		JSONArray jsonArray = null;
		try {
			URL url = new URL(trainDataURL);
			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
					+ "AppleWebkit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
			httpConn.setDoInput(true);
			httpConn.setRequestMethod("GET");
			httpConn.setUseCaches(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestProperty("Content-Type", "application/xml; charset=UTF-8");
			httpConn.connect();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String lines;
			StringBuffer stringBuffer = new StringBuffer("");
			while ((lines = bufferedReader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				stringBuffer.append(lines);
			}
			System.out.println("查詢" + dateStr + "資料成功");
			jsonArray = new JSONArray(stringBuffer.toString());
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public static void insertData(JSONArray jsonArray, Connection conn) {
		PreparedStatement pstmt = null;
		try {
			String insertTrainDailyInfoSQL = "INSERT INTO trainDailyInfo"
					+ "(trainDate, trainNo, startStationId, endStationId, trainTypeName, direction, tripLine, "
					+ "wheelchairFlag, packageServiceFlag, diningFlag, bikeFlag, breastFeedingFlag, dailyFlag,"
					+ "serviceAddedFlag, note) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			String getTrainDailyInfoIdSQL = "SELECT traindailyId FROM traindailyinfo WHERE trainDate =? AND trainNo = ?";

			String insertStoptimesSQL = "INSERT INTO stopTimes"
					+ "(stopSequence, stationId, arrivalTime, departureTime, trainDailyId) VALUES(?,?,?,?,?)";
			String dateStr = "";
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				TrainDailyInfo trainDailyInfo = new TrainDailyInfo(jsonObject);
				dateStr = trainDailyInfo.getTrainDate();
				pstmt = conn.prepareStatement(getTrainDailyInfoIdSQL);
				pstmt.setString(1, trainDailyInfo.getTrainDate());
				pstmt.setString(2, trainDailyInfo.getTrainNo());

				ResultSet resultSet = pstmt.executeQuery();

				if (!resultSet.next()) {
					pstmt = conn.prepareStatement(insertTrainDailyInfoSQL);
					pstmt.setString(1, trainDailyInfo.getTrainDate());
					pstmt.setString(2, trainDailyInfo.getTrainNo());
					pstmt.setString(3, trainDailyInfo.getStartStationId());
					pstmt.setString(4, trainDailyInfo.getEndStationId());
					pstmt.setString(5, trainDailyInfo.getTrainTypeName());
					pstmt.setInt(6, trainDailyInfo.getDirection());
					pstmt.setInt(7, trainDailyInfo.getTripLine());
					pstmt.setInt(8, trainDailyInfo.getWheelchairFlag());
					pstmt.setInt(9, trainDailyInfo.getPackageServiceFlag());
					pstmt.setInt(10, trainDailyInfo.getDiningFlag());
					pstmt.setInt(11, trainDailyInfo.getBikeFlag());
					pstmt.setInt(12, trainDailyInfo.getBreastFeedingFlag());
					pstmt.setInt(13, trainDailyInfo.getDailyFlag());
					pstmt.setInt(14, trainDailyInfo.getServiceAddedFlag());
					pstmt.setString(15, trainDailyInfo.getNote());
					pstmt.executeUpdate();

					pstmt = conn.prepareStatement(getTrainDailyInfoIdSQL);
					pstmt.setString(1, trainDailyInfo.getTrainDate());
					pstmt.setString(2, trainDailyInfo.getTrainNo());
					ResultSet resultSet2 = pstmt.executeQuery();
					resultSet2.next();
					int trainDailyId = resultSet2.getInt("trainDailyId");

					pstmt = conn.prepareStatement(insertStoptimesSQL);
					List<StopTimes> stopTimes = trainDailyInfo.getStopTimes();
					for (int j = 0; j < stopTimes.size(); j++) {
						StopTimes stopTime = stopTimes.get(j);
						pstmt.setInt(1, stopTime.getStopSequence());
						pstmt.setString(2, stopTime.getStationId());
						pstmt.setTimestamp(3, new Timestamp(stopTime.getArrivalTime().getTime()));
						pstmt.setTimestamp(4, new Timestamp(stopTime.getDepartureTime().getTime()));
						pstmt.setInt(5, trainDailyId);
						pstmt.executeUpdate();
					}
				}
			}
			if (pstmt.getUpdateCount() == -1) {
				System.out.println("已有當日資料");
			} else {
				System.out.println(dateStr + "資料新增完成");
			}
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
