import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.rolling.RollingFileAppender
import static ch.qos.logback.classic.Level.INFO
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

def LOGS_DIR = System.getProperty("jboss.server.log.dir")
def LOG_FILE = "stamdata-batch-copy-ws.log"

appender("ROLLING", RollingFileAppender) {
    file = "${LOGS_DIR}/${LOG_FILE}"

    encoder(PatternLayoutEncoder) {
        pattern = "%d [%-4level] %msg [%thread] [%mdc]%n"
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "${LOGS_DIR}/${LOG_FILE}.%d{yyyy-MM}.zip"
    }
}

root(INFO, ['ROLLING'])
