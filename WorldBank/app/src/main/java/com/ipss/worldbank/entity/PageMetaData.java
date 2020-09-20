package com.ipss.worldbank.entity;

public class PageMetaData {
    private int page;
    private int pages;
    private int per_page;
    private int total;

    public PageMetaData() {
    }

    public PageMetaData(int page, Integer pages, int per_page, int total) {
        this.page = page;
        this.pages = pages;
        this.per_page = per_page;
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
