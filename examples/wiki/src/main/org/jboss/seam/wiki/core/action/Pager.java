package org.jboss.seam.wiki.core.action;

public class Pager {

    private Long numOfRecords = 0l;
    private Integer page = 0;
    private Long pageSize = 10l;

    public Pager() {}

    public Pager(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getNumOfRecords() {
        return numOfRecords;
    }

    public void setNumOfRecords(Long numOfRecords) {
        this.numOfRecords = numOfRecords;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page != null) this.page = page;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public int getNextPage() {
        return page + 1;
    }

    public int getPreviousPage() {
        return page - 1;
    }

    public int getFirstPage() {
        return 0;
    }

    public long getFirstRecord() {
        return page * pageSize + 1;
    }

    public long getLastRecord() {
        return (page * pageSize + pageSize) > numOfRecords
                ? numOfRecords
                : page * pageSize + pageSize;
    }

    public long getNextRecord() {
        return page * pageSize;
    }

    public long getLastPage() {
        long lastPage = (numOfRecords / pageSize);
        if (numOfRecords % pageSize == 0) lastPage--;
        return lastPage;
    }

    public boolean isNextPageAvailable() {
        return numOfRecords > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return numOfRecords != null && page > 0;
    }

}
