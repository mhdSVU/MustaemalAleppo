package mohammedyouser.com.mustaemalaleppo.Data;

public class Report {
    private String userID_Reporter;
    private String userID_Reported;
    private long reportDateTime;
    private String reportDetails;

    public void setUserID_Reporter(String userID_Reporter) {
        this.userID_Reporter = userID_Reporter;
    }

    public void setUserID_Reported(String userID_Reported) {
        this.userID_Reported = userID_Reported;
    }

    public void setReportDateTime(long reportDateTime) {
        this.reportDateTime = reportDateTime;
    }

    public void setReportDetails(String reportDetails) {
        this.reportDetails = reportDetails;
    }

    public String getUserID_Reporter() {
        return userID_Reporter;
    }

    public String getUserID_Reported() {
        return userID_Reported;
    }

    public long getReportDateTime() {
        return reportDateTime;
    }

    public String getReportDetails() {
        return reportDetails;
    }

    public Report(String userID_Reporter, String userID_Reported, long reportDateTime, String reportDetails) {
        this.userID_Reporter = userID_Reporter;
        this.userID_Reported = userID_Reported;
        this.reportDateTime = reportDateTime;
        this.reportDetails = reportDetails;
    }
}
