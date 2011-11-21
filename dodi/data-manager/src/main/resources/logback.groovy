import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import static ch.qos.logback.classic.Level.INFO
import ch.qos.logback.core.ConsoleAppender

def LOGS_DIR = System.getProperty("jboss.server.log.dir")
def LOG_FILE = "stamdata-data-manager.log"

def appenders = ["ROLLING"]
def outputPattern = "%d [%-4level] %msg [thread=%thread] [%mdc]%n"

if (LOGS_DIR == null)
{
    // If we are not running in production (JBoss) we'll
    // output to the console as well.
    //
    appenders << "CONSOLE"

    appender("CONSOLE", ConsoleAppender) {

        encoder(PatternLayoutEncoder) {
            pattern = outputPattern
        }
    }

    // Also output the log to the target directory.
    //
    LOGS_DIR = "target/logs"
}

appender("ROLLING", RollingFileAppender) {
    file = "${LOGS_DIR}/${LOG_FILE}"

    encoder(PatternLayoutEncoder) {
        pattern = outputPattern
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "${LOGS_DIR}/${LOG_FILE}.%d{yyyy-MM}.zip"
    }
}

root(INFO, appenders)
