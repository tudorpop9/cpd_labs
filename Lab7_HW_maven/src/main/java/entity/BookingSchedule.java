package entity;

public class BookingSchedule {
    private String month;
    private String year;
    private long userId;

    public BookingSchedule(String month, String year, long userId) {
        this.month = month;
        this.year = year;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        BookingSchedule bs = (BookingSchedule) obj;
        return bs.getMonth().equalsIgnoreCase(this.month) &&
                bs.getYear().equalsIgnoreCase(this.year) &&
                super.equals(obj);
    }

    @Override
    public String toString() {
        return "BookingSchedule{" +
                "month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", userId=" + userId +
                '}';
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
