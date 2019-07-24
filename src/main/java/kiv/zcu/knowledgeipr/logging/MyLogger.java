package kiv.zcu.knowledgeipr.logging;

import kiv.zcu.knowledgeipr.app.AppServletContextListener;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.*;

/**
 * Provides logging to the console and file
 */
public class MyLogger {
    static private Date dat = new Date();

    //static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;
    static private MessageFormat formatter;

    //private final static String format = "{0,date} {0,time}";
    private static Object[] args = new Object[1];

    private static String lineSeparator = "\n";

    public static void setup(String fileSuffix) {
        formatterTxt = new SimpleFormatter() {
            private static final String format = "{0,date} {0,time}";

            @Override
            public synchronized String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();

                // Minimize memory allocations here.
                dat.setTime(record.getMillis());
                args[0] = dat;

                // Date and time
                StringBuffer text = new StringBuffer();
                if (formatter == null) {
                    formatter = new MessageFormat(format);
                }
                formatter.format(args, text, null);
                sb.append(text);
                sb.append(" ");


                // Class name
                if (record.getSourceClassName() != null) {
                    sb.append(record.getSourceClassName());
                } else {
                    sb.append(record.getLoggerName());
                }

                // Method name
                if (record.getSourceMethodName() != null) {
                    sb.append(" ");
                    sb.append(record.getSourceMethodName());
                }
                sb.append(" - "); // lineSeparator

                String message = formatMessage(record);

                // Level
                sb.append(record.getLevel().getLocalizedName());
                sb.append(": ");

                // Indent - the more serious, the more indented.
                //sb.append( String.format("% ""s") );
                int iOffset = (1000 - record.getLevel().intValue()) / 100;
                for (int i = 0; i < iOffset; i++) {
                    sb.append(" ");
                }

                sb.append(message);
                sb.append(lineSeparator);
                if (record.getThrown() != null) {
                    try {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        record.getThrown().printStackTrace(pw);
                        pw.close();
                        sb.append(sw.toString());
                    } catch (Exception ex) {
                    }
                }
                return sb.toString();
            }
        };

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.ALL);

        //setupConsoleHandler(logger);
        setupFileHandler(logger, fileSuffix);
    }

    private static void setupConsoleHandler(Logger logger) {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(formatterTxt);
        logger.addHandler(consoleHandler);
    }

    private static void setupFileHandler(Logger logger, String fileSuffix) {
        try {
            Properties properties = AppServletContextListener.getProperties();
            String basePath = properties.getProperty("logs");

            long timeStamp = System.currentTimeMillis();

            String fullPath = basePath + "log-" + timeStamp + "-" + fileSuffix + ".log";
            new File(fullPath).getParentFile().mkdirs();
            FileHandler fhandler = new FileHandler(
                    basePath + "log-" + timeStamp + "-" + fileSuffix + ".log");

            fhandler.setFormatter(formatterTxt);
            logger.addHandler(fhandler);

        } catch (IOException | SecurityException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
