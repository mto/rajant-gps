package com.mto.rajant;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import com.rajant.bcapi.protos.BCAPIProtos;
import com.rajant.bcapi.protos.GpsProtos;
import com.rajant.bcapi.protos.StateProtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GPSManager {

    private static final Logger log = LoggerFactory.getLogger(GPSManager.class);

    public void recordGPS(String host, int port, String role, String password) throws Exception {
        final Session ns = initSession(host, port, role, password);
        log.info("Session for (" + host + "," + port + " initialized");
        ns.awaitAuthentication();
        log.info("Session " + ns.getName() + " authenticated");
        log.info("Requesting GPS positions 10 times for device " + ns.getName());
        Timer t = new Timer();
        for (int i = 0; i < 10; i++) {
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    ns.askGPS();
                }
            }, i * 20000);
        }
    }

    private Session initSession(String host, int port, String role, String password) throws Exception {
        Session s = new Session(InetAddress.getByName(host), port, role, password, new Callback() {
            @Override
            public void massageAuthResponse(BCAPIProtos.BCMessage.Builder msg) {
                msg.setState(StateProtos.State.getDefaultInstance());
            }

            @Override
            public void messageReceived(Session session, BCAPIProtos.BCMessage msg) {
                if (msg.hasGps()) {
                    GPSManager.this.logGPS(session.getName(), msg.getGps());
                } else {
                    log.info("Message received from session " + session.getName() + " has no GPS data");
                }
            }
        });
        s.setName(host);

        return s;
    }

    private void logGPS(String sname, GpsProtos.GPS gps) {
        GpsProtos.GPS.GPSPositionReport pos = gps.getGpsPos();
        String latitude = pos.getGpsLat();
        String longitude = pos.getGpsLong();

        log.info("GPS position of " + sname + " are: " + latitude + " | " + longitude);
    }

    public static void main(String[] args) throws Exception {
        initLogging();

        GPSManager gpsMan = new GPSManager();
        if (args.length < 2) {
            log.error("At least the host and port of devices must be provided");
            System.exit(1);
        } else {
            String host = args[0];
            int port = Integer.parseInt(args[1]);

            Map<String, String> rpwds = new HashMap<>();
            rpwds.put("co", "breadcrumb-co");
            rpwds.put("local", "breadcrumb-local");
            rpwds.put("view", "breadcrumb-view");
            rpwds.put("admin", "breadcrumb-admin");

            String role = "co";
            String password = "breadcrumb-co";

            try {
                role = args[2];
                password = rpwds.get(role);

                password = args[3];
            } catch (Exception ex) {
            }

            log.info("Role and password are: " + role + " , " + password);
            gpsMan.recordGPS(host, port, role, password);
        }
    }

    public static void initLogging() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.setLevel(Level.DEBUG);
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ConsoleAppender ca = new ConsoleAppender();
        ca.setContext(lc);
        ca.setName("console");
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(lc);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();
        ca.setEncoder(encoder);
        ca.start();
        root.addAppender(ca);
    }
}
