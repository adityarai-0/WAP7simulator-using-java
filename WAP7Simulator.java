/**
 * WAP-7 LOCOMOTIVE SIMULATOR
 * Enhanced Edition v2.0
 * 
 * Complete single-file implementation with no external dependencies.
 */

import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class WAP7Simulator {
    
    // Main entry point
    public static void main(String[] args) {
        WAP7Engine engine = new WAP7Engine();
        CommandProcessor processor = new CommandProcessor(engine);
        processor.start();
    }
    
    /**
     * Represents the possible states of the WAP-7 engine.
     */
    public enum EngineState {
        OFF("Off"),
        IDLE("Idle"),
        RUNNING("Running"),
        BRAKING("Braking"),
        ERROR("Error");
        
        private final String displayName;
        
        EngineState(String displayName) {
            this.displayName = displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    /**
     * Custom exception for engine-related errors.
     */
    public static class EngineException extends RuntimeException {
        public EngineException(String message) {
            super(message);
        }
    }
    
    /**
     * Central place for all simulation constants.
     */
    public static final class SimulationConstants {
        // Engine parameters
        public static final int MAX_THROTTLE = 8;
        public static final int THROTTLE_STEP_KMH = 10; // km/h per throttle level
        public static final int MAX_SPEED = 140; // km/h
        
        // Physical limitations
        public static final int EMERGENCY_BRAKE_DECELERATION = 30; // km/h per brake application
        public static final int STANDARD_BRAKE_DECELERATION = 10; // km/h per brake application
        
        // Power supply
        public static final int STANDARD_VOLTAGE = 25000; // 25kV AC
        
        private SimulationConstants() {
            // Prevent instantiation
        }
    }
    
    /**
     * Core engine simulation class for the WAP-7 locomotive.
     */
    public static class WAP7Engine {
        private static final Logger LOGGER = Logger.getLogger(WAP7Engine.class.getName());
        
        private EngineState state;
        private int speed;
        private int throttleLevel;
        private boolean pantographRaised;
        private int currentVoltage;
        private int totalDistance;
        private long startTime;
        private long runningTime;
        
        public WAP7Engine() {
            this.state = EngineState.OFF;
            this.speed = 0;
            this.throttleLevel = 0;
            this.pantographRaised = false;
            this.currentVoltage = 0;
            this.totalDistance = 0;
            this.startTime = 0;
            this.runningTime = 0;
            
            // Set up logging
            LOGGER.setLevel(Level.INFO);
        }
        
        /**
         * Raises the pantograph to connect to the overhead line.
         * @throws EngineException if the pantograph is already raised
         */
        public void raisePantograph() {
            if (!pantographRaised) {
                pantographRaised = true;
                currentVoltage = SimulationConstants.STANDARD_VOLTAGE;
                LOGGER.info("Pantograph raised, voltage: " + currentVoltage + "V");
            } else {
                throw new EngineException("Pantograph already raised");
            }
        }
        
        /**
         * Lowers the pantograph, disconnecting from power.
         * @throws EngineException if engine is not in OFF state
         */
        public void lowerPantograph() {
            if (state == EngineState.OFF) {
                pantographRaised = false;
                currentVoltage = 0;
                LOGGER.info("Pantograph lowered");
            } else {
                throw new EngineException("Cannot lower pantograph while engine is " + state);
            }
        }
        
        /**
         * Starts the engine if preconditions are met.
         * @throws EngineException if engine is already started or pantograph is not raised
         */
        public void startEngine() {
            if (state == EngineState.OFF) {
                if (!pantographRaised) {
                    throw new EngineException("Cannot start engine: pantograph not raised");
                }
                state = EngineState.IDLE;
                startTime = System.currentTimeMillis();
                LOGGER.info("Engine started");
            } else {
                throw new EngineException("Engine already started: " + state);
            }
        }
        
        /**
         * Stops the engine and resets parameters.
         */
        public void stopEngine() {
            if (state != EngineState.OFF) {
                updateRunningTime();
                state = EngineState.OFF;
                speed = 0;
                throttleLevel = 0;
                LOGGER.info("Engine stopped. Total running time: " + (runningTime / 1000) + " seconds");
            }
        }
        
        /**
         * Increases the throttle level and updates speed accordingly.
         * @throws EngineException if engine is not in appropriate state
         */
        public void increaseThrottle() {
            if (state == EngineState.IDLE || state == EngineState.RUNNING) {
                if (throttleLevel < SimulationConstants.MAX_THROTTLE) {
                    throttleLevel++;
                    updateSpeed();
                    state = EngineState.RUNNING;
                    LOGGER.info("Throttle increased to " + throttleLevel + ", speed: " + speed + "km/h");
                } else {
                    throw new EngineException("Throttle already at maximum (" + SimulationConstants.MAX_THROTTLE + ")");
                }
            } else {
                throw new EngineException("Cannot increase throttle in state: " + state);
            }
        }
        
        /**
         * Updates speed based on current throttle level, ensuring it doesn't exceed MAX_SPEED.
         */
        private void updateSpeed() {
            int targetSpeed = throttleLevel * SimulationConstants.THROTTLE_STEP_KMH;
            speed = Math.min(targetSpeed, SimulationConstants.MAX_SPEED);
        }
        
        /**
         * Applies standard brakes, reducing speed gradually.
         */
        public void applyBrakes() {
            if (speed > 0) {
                state = EngineState.BRAKING;
                speed = Math.max(0, speed - SimulationConstants.STANDARD_BRAKE_DECELERATION);
                
                // Adjust throttle level based on new speed
                throttleLevel = Math.min(throttleLevel, speed / SimulationConstants.THROTTLE_STEP_KMH);
                
                if (speed == 0) {
                    state = EngineState.IDLE;
                    throttleLevel = 0;
                }
                LOGGER.info("Brakes applied, speed: " + speed + "km/h, throttle: " + throttleLevel);
            }
        }
        
        /**
         * Performs emergency stop, bringing the engine to immediate halt.
         */
        public void emergencyStop() {
            if (speed > 0) {
                LOGGER.warning("EMERGENCY STOP INITIATED");
                speed = 0;
                throttleLevel = 0;
                state = EngineState.IDLE;
                LOGGER.info("Emergency stop completed, engine is now idle");
            }
        }
        
        /**
         * Updates the total running time of the engine.
         */
        private void updateRunningTime() {
            if (startTime > 0) {
                long currentTime = System.currentTimeMillis();
                runningTime += (currentTime - startTime);
                startTime = currentTime;
            }
        }
        
        /**
         * Simulates train movement for a given time period.
         * @param seconds Time in seconds to simulate movement
         */
        public void simulateMovement(int seconds) {
            if (state == EngineState.RUNNING || state == EngineState.BRAKING) {
                // Calculate distance traveled: speed (km/h) * time (h)
                double timeInHours = seconds / 3600.0;
                int distanceTraveled = (int) (speed * timeInHours * 1000); // Convert to meters
                totalDistance += distanceTraveled;
                LOGGER.fine("Simulated movement: " + distanceTraveled + 
                         "m in " + seconds + "s at " + speed + "km/h");
            }
        }
        
        /**
         * Gets detailed status of the engine.
         * @return String representing engine status
         */
        public String getStatus() {
            updateRunningTime();
            return String.format("State: %s | Speed: %d km/h | Throttle: %d/%d | " +
                               "Pantograph: %s | Voltage: %d V | " +
                               "Distance: %d m | Runtime: %d s",
                    state, speed, throttleLevel, SimulationConstants.MAX_THROTTLE,
                    pantographRaised ? "Up" : "Down", currentVoltage,
                    totalDistance, runningTime / 1000);
        }
        
        /**
         * Gets a compact status representation for logging.
         * @return String with essential engine metrics
         */
        public String getCompactStatus() {
            return String.format("[%s] %d km/h T:%d/%d", 
                    state, speed, throttleLevel, SimulationConstants.MAX_THROTTLE);
        }
    
        // Getters for testing and UI
        public int getSpeed() { return speed; }
        public int getThrottleLevel() { return throttleLevel; }
        public EngineState getState() { return state; }
        public boolean isPantographRaised() { return pantographRaised; }
        public int getCurrentVoltage() { return currentVoltage; }
        public int getTotalDistance() { return totalDistance; }
        public long getRunningTime() { return runningTime / 1000; } // in seconds
    }
    
    /**
     * Command processor for the CLI interface.
     */
    public static class CommandProcessor {
        private final WAP7Engine engine;
        private final Logger logger = Logger.getLogger(CommandProcessor.class.getName());
        private boolean simulationRunning = true;
    
        public CommandProcessor(WAP7Engine engine) {
            this.engine = engine;
            initializeLogger();
        }
        
        /**
         * Sets up the logger with appropriate formatting and level.
         */
        private void initializeLogger() {
            ConsoleHandler handler = new ConsoleHandler();
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setLevel(Level.INFO);
        }
    
        /**
         * Starts the command processor and enters the command loop.
         */
        public void start() {
            Scanner scanner = new Scanner(System.in);
            printWelcomeMessage();
    
            while (simulationRunning) {
                System.out.print(">> ");
                String input = scanner.nextLine().trim().toLowerCase();
                
                try {
                    processCommand(input);
                } catch (EngineException e) {
                    logger.warning("Engine error: " + e.getMessage());
                    System.out.println("⚠️  " + e.getMessage());
                } catch (Exception e) {
                    logger.severe("Unexpected error: " + e.getMessage());
                    System.out.println("❌ Unexpected error: " + e.getMessage());
                }
            }
        }
        
        /**
         * Processes a single command from user input.
         * @param input Command string from user
         */
        private void processCommand(String input) {
            String[] parts = input.split("\\s+");
            String command = parts[0];
            
            switch (command) {
                case "start":
                    engine.startEngine();
                    System.out.println("Engine started successfully.");
                    break;
                    
                case "stop":
                    engine.stopEngine();
                    System.out.println("Engine stopped.");
                    break;
                    
                case "throttle":
                    handleThrottleCommand(parts);
                    break;
                    
                case "brake":
                    engine.applyBrakes();
                    System.out.println("Brakes applied. " + engine.getCompactStatus());
                    break;
                    
                case "emergency":
                    engine.emergencyStop();
                    System.out.println("EMERGENCY STOP EXECUTED! Engine halted.");
                    break;
                    
                case "pantograph":
                    handlePantographCommand(parts);
                    break;
                    
                case "simulate":
                    handleSimulateCommand(parts);
                    break;
                    
                case "status":
                    System.out.println(engine.getStatus());
                    break;
                    
                case "help":
                    showHelp();
                    break;
                    
                case "exit":
                    System.out.println("Exiting simulation. Final status:");
                    System.out.println(engine.getStatus());
                    simulationRunning = false;
                    break;
                    
                case "verbose":
                    toggleVerboseLogging();
                    break;
                    
                default:
                    System.out.println("Invalid command. Type 'help' for available commands.");
            }
        }
        
        /**
         * Handles throttle command with optional level parameter.
         * @param parts Command parts from user input
         */
        private void handleThrottleCommand(String[] parts) {
            if (parts.length > 1) {
                try {
                    int targetLevel = Integer.parseInt(parts[1]);
                    int currentLevel = engine.getThrottleLevel();
                    
                    if (targetLevel > currentLevel) {
                        // Increase throttle to target level
                        for (int i = currentLevel; i < targetLevel; i++) {
                            engine.increaseThrottle();
                        }
                        System.out.println("Throttle set to " + targetLevel + ". " + 
                                         engine.getCompactStatus());
                    } else if (targetLevel < currentLevel) {
                        System.out.println("Use brake to reduce throttle/speed.");
                    } else {
                        System.out.println("Throttle already at " + targetLevel + ".");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid throttle level. Use a number.");
                }
            } else {
                engine.increaseThrottle();
                System.out.println("Throttle increased. " + engine.getCompactStatus());
            }
        }
        
        /**
         * Handles pantograph up/down commands.
         * @param parts Command parts from user input
         */
        private void handlePantographCommand(String[] parts) {
            if (parts.length > 1) {
                switch (parts[1]) {
                    case "up", "raise":
                        engine.raisePantograph();
                        System.out.println("Pantograph raised. Power available.");
                        break;
                    case "down", "lower":
                        engine.lowerPantograph();
                        System.out.println("Pantograph lowered. Power disconnected.");
                        break;
                    default:
                        System.out.println("Invalid pantograph command. Use 'up' or 'down'.");
                }
            } else {
                System.out.println("Pantograph is " + (engine.isPantographRaised() ? "UP" : "DOWN"));
            }
        }
        
        /**
         * Handles simulation of movement for a given time period.
         * @param parts Command parts from user input
         */
        private void handleSimulateCommand(String[] parts) {
            if (parts.length > 1) {
                try {
                    int seconds = Integer.parseInt(parts[1]);
                    if (seconds > 0) {
                        engine.simulateMovement(seconds);
                        System.out.println("Simulated " + seconds + "s of movement. " + 
                                         engine.getCompactStatus());
                    } else {
                        System.out.println("Please specify a positive time value.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid time value. Use a number in seconds.");
                }
            } else {
                System.out.println("Please specify simulation time in seconds.");
            }
        }
        
        /**
         * Toggles verbose logging mode.
         */
        private void toggleVerboseLogging() {
            Level currentLevel = logger.getLevel();
            if (currentLevel == Level.FINE) {
                logger.setLevel(Level.INFO);
                System.out.println("Verbose logging disabled.");
            } else {
                logger.setLevel(Level.FINE);
                System.out.println("Verbose logging enabled.");
            }
        }
        
        /**
         * Displays the welcome message and ASCII art.
         */
        private void printWelcomeMessage() {
            System.out.println("""
                ╔══════════════════════════════════════════════════╗
                ║                                                  ║
                ║             WAP-7 LOCOMOTIVE SIMULATOR           ║
                ║                                                  ║
                ║      _____                                       ║
                ║     |  _  \\__________________________            ║
                ║  ===|_|_|_|   |_|_] |_|_] |_|_] |_|_]|===        ║
                ║  |  |_|_|_|___________________________|  |       ║
                ║  |_______________________________________|       ║
                ║                                                  ║
                ║              Enhanced Edition v2.0               ║
                ║                                                  ║
                ╚══════════════════════════════════════════════════╝
                
                Type 'help' for available commands.
                """);
        }
    
        /**
         * Shows the help menu with available commands.
         */
        private void showHelp() {
            System.out.println("""
                ╔══════════════════════════════════════════════════╗
                ║                 AVAILABLE COMMANDS               ║
                ╠══════════════════════════════════════════════════╣
                ║ BASIC OPERATIONS:                                ║
                ║ - pantograph up   : Raise pantograph             ║
                ║ - pantograph down : Lower pantograph             ║
                ║ - start           : Start the engine             ║
                ║ - stop            : Stop the engine              ║
                ║                                                  ║
                ║ MOVEMENT CONTROLS:                               ║
                ║ - throttle        : Increase throttle by 1       ║
                ║ - throttle <n>    : Set throttle to level n      ║
                ║ - brake           : Apply brakes                 ║
                ║ - emergency       : Emergency stop               ║
                ║                                                  ║
                ║ SIMULATION:                                      ║
                ║ - simulate <n>    : Simulate n seconds of travel ║
                ║ - status          : Display engine status        ║
                ║                                                  ║
                ║ SYSTEM:                                          ║
                ║ - verbose         : Toggle verbose logging       ║
                ║ - help            : Show this help menu          ║
                ║ - exit            : Quit simulation              ║
                ╚══════════════════════════════════════════════════╝
                """);
        }
    }
}
