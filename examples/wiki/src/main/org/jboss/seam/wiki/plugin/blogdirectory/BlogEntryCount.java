package org.jboss.seam.wiki.plugin.blogdirectory;

public class BlogEntryCount {

    Long numOfEntries;
    Integer year;
    Integer month;
    Integer day;

    public BlogEntryCount() {}

    public BlogEntryCount(Long numOfEntries, Integer year, Integer month, Integer day) {
        this.numOfEntries = numOfEntries;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public Long getNumOfEntries() {
        return numOfEntries;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDay() {
        return day;
    }

    public String getAsString() {
        return BlogDirectory.dateAsString(year, month, day);
    }

    public String toString() {
        return "NumOfEntries: " + getNumOfEntries() + " Year: " + getYear() + " Month: " + getMonth() + " Day: " + getDay();
    }

}
