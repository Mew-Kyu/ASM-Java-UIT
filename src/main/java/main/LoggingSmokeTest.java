package main;

/** Simple smoke test to verify Log4j2 is detected via JUL bridge. */
public class LoggingSmokeTest {
    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
        try { java.util.logging.LogManager.getLogManager().reset(); } catch (Exception ignored) {}
    }
    public static void main(String[] args) {
        var logger = java.util.logging.Logger.getLogger("smoke");
        logger.info("Smoke info log");
        logger.warning("Smoke warning log");
        System.out.println("Done");
    }
}

