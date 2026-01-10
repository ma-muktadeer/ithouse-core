package com.ithouse.core.message.interfaces;

public interface EnablePagination {
    void setPageNumber(Integer page);
    void setPageSize(Integer size);
    Integer getPageNumber();
    Integer getPageSize();

}
