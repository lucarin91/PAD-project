package lr.core;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Created by luca on 06/04/16.
 */
public class LoggerWrapper {
    private String name;
    private Logger LOG;

    public LoggerWrapper(Class clazz, String name){
        this.name = name;
        this.LOG = Logger.getLogger(clazz);
    }

    private void log(Level level, Object msg){
        LOG.log(level, name + " - " + msg);
    }

    public void i (Object msg){
        log(Level.INFO, msg);
    }

    public void d (Object msg){
        log(Level.DEBUG, msg);
    }

    public void e (Object msg){
        log(Level.ERROR, msg);
    }
}
