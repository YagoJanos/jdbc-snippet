package com.ilegra.nelioalvesjdbc.db.exception;

public class DbException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public DbException(String msg){
        super(msg);
    }

    public DbException(String msg, Throwable e){
        super(msg, e);
    }
}
