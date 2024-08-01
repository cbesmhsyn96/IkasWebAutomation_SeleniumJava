package utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log {

    /**
     *
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @return
     */
    private static Logger lsLog4j() {
        return LogManager.getLogger(Thread.currentThread().getName());
    }

    /**
     * Method for trace level
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param description
     */

    public void trace(String description) {
        lsLog4j().log(Level.TRACE, description);
    }

    /**
     * Method for event level
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param description
     */

    public void event(String description) {
        lsLog4j().log(Level.DEBUG, description);
    }

    /**
     *
     * Method for information level. Mostly used to print something in the console.
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param description
     */

    public void info(String description) {
        lsLog4j().log(Level.INFO, description);
    }

    /**
     *
     * Method for warning level
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param description
     */

    public void warning(String description) {
        lsLog4j().log(Level.WARN, description);
    }

    /**
     *
     * Method for error level
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param description
     */
    public void fail(String description) {
        lsLog4j().log(Level.ERROR, description);
    }

    /**
     *
     * Method for exception level
     *
     * @author Sanoj Swaminathan
     * @since 23-06-2024
     * @param e
     */

    public void exception(Exception e) {
        String eMessage = e.getMessage();
        if (eMessage != null && eMessage.contains("\n")) {
            eMessage = eMessage.substring(0, eMessage.indexOf("\n"));
        }
        lsLog4j().log(Level.FATAL, eMessage, e);
    }
}