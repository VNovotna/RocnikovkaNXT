package rocnikovka;

import java.io.IOException;
import lejos.nxt.Button;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.mapping.NXTNavigationModel;
import lejos.robotics.mapping.NavEventListener;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move.MoveType;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.util.PilotProps;

public class Moving implements NavEventListener {

    public static final float MAX_DISTANCE = 50f;
    public static final int DETECTOR_DELAY = 1000;
    private NXTNavigationModel model;

    public static void main(String[] args) throws Exception {
        (new Moving()).run();
    }

    public void run() throws Exception {
        model = new NXTNavigationModel();
        model.addListener(this);
        model.setDebug(true);
        model.setSendMoveStart(true);
        model.setSendMoveStop(true);
        model.setAutoSendPose(true);
        lol();
        Button.waitForAnyPress();
        model.shutDown();
    }

    @Override
    public void whenConnected() {
        PilotProps pp = new PilotProps();
        try {
            pp.loadPersistentValues();
        } catch (IOException ioe) {
            System.exit(1);
        }
        float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "56"));
        float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "160"));
        RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
        RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
        boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE, "false"));
        final DifferentialPilot diffPilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
        final Navigator navigator = new Navigator(diffPilot);
        UltrasonicSensor sonicSensor = new UltrasonicSensor(SensorPort.S3);
        RangeFeatureDetector detector = new RangeFeatureDetector(sonicSensor, MAX_DISTANCE, DETECTOR_DELAY);

        // Adding the navigator, adds the pilot and pose provider as well
        model.addNavigator(navigator);
        
        System.out.println(model.hasMap());
        // Add the feature detector
        // Give it a pose provider, so that it records the pose when a feature was detected
        model.addFeatureDetector(detector);
        detector.enableDetection(true);
        detector.setPoseProvider(navigator.getPoseProvider());
        navigator.getPoseProvider().getPose();
        // emergency stop when obstacle is near, pokud se netočí
        detector.addListener(new FeatureListener() {
            @Override
            public void featureDetected(Feature feature, FeatureDetector detector) {
                if (feature.getRangeReading().getRange() <= 20) {      //Nemusí přece hned zastavovat 
                    if (diffPilot.isMoving() && diffPilot.getMovement().getMoveType() != MoveType.ROTATE) {
                        diffPilot.stop();
                        if (navigator.isMoving()) {
                            System.out.println("EMERGENCY STOP");
                            navigator.clearPath();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void eventReceived(NavEvent navEvent) {
        System.out.println("NAV EVENT");
        if (navEvent == NavEvent.WAYPOINT_REACHED) {
            System.out.println("WAYPOINT_REACHED");
        } else if (navEvent == NavEvent.CALCULATE_PATH) {
            System.out.println("CALCULATE_PATH");
        } else if (navEvent == NavEvent.FOLLOW_PATH) {
            System.out.println("FOLLOW_PATH");
        }
    }

    public void lol() {
        System.out.println("Waiting BT connection");
    }
}