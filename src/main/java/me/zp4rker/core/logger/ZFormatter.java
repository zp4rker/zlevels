package me.zp4rker.core.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ZFormatter extends SimpleFormatter {

    @Override
    public synchronized String format(LogRecord record) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return "[" + dateFormat.format(new Date(record.getMillis())) + "] [" +
                record.getLevel() + "]: " + record.getMessage();
    }
}
