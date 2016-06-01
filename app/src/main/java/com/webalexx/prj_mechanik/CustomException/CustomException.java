package com.webalexx.prj_mechanik.CustomException;

import android.util.Log;

/**
 * Created by web-w on 19.05.2016.
 */
public class CustomException extends Exception{
    /**
     * Instantiates a new Custom exception.
     *
     * @param e the e
     */
    public CustomException(Throwable e) {
        super(e.getMessage().toString());
        PrintLog(e);
    }

    /**
     * Instantiates a new Custom exception.
     *
     * @param mark the mark
     * @param e    the e
     */
    public CustomException(String mark, Throwable e) {
        super(e.getMessage().toString());
        //initCause(e);
        PrintLog(mark, e);
    }

    /**
     * throws an exception on the level above current (where it happens)
     *
     * @param e    the e
     */
    private void ExceptionThrowUd(Throwable e){

    }

    /**
     * Print log.
     *
     * @param mark the mark
     * @param e    the e
     */
    public static void  PrintLog(String mark, Throwable e) {
        Log.d(mark, e.getMessage());
    }

    /**
     * Print log.
     *
     * @param e the e
     */
    public static void  PrintLog(Throwable e) {
        Log.d("Custom Exception ->", e.getMessage());
    }

    /**
     * Print log.
     *
     * @param mark the mark
     * @param e    the e
     */
    public static void  PrintLog(String mark, String e) {
        Log.d(mark, e);
    }

}
