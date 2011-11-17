import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import static ch.qos.logback.classic.Level.INFO

def LOGS_DIR = System.getProperty("jboss.server.log.dir")
def LOG_FILE = "stamdata-data-manager.log"

appender("ROLLING", RollingFileAppender) {
    file = "${LOGS_DIR}/${LOG_FILE}"

    encoder(PatternLayoutEncoder) {
        pattern = "%d [%-4level] %msg [thread=%thread] [%mdc]%n"
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "${LOGS_DIR}/${LOG_FILE}.%d{yyyy-MM}.zip"
    }
}

root(INFO, ['ROLLING'])
