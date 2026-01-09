package com.ithouse.core.message.interfaces;

public interface EnablePagination {
    void setPageNumber(int page);
    void setPageSize(int size);
    int getPageNumber();
    int getPageSize();

}
