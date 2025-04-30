package com.kcb.recon.tool.authentication.utils;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AppUtillities {
    public static final String ERROR = " ERROR: ";
    public static final String STACKTRACE = " STACKTRACE: ";


    public static String logPreString() {
        return " KCB RECON TOOL | " + Thread.currentThread().getStackTrace()[2].getClassName() + " | "
                + Thread.currentThread().getStackTrace()[2].getLineNumber() + " | "
                + Thread.currentThread().getStackTrace()[2].getMethodName() + "() | ";
    }

    public static String getExceptionStacktrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
}
