# WAP-7 Locomotive Simulator

## Overview

The WAP-7 Locomotive Simulator is a Java-based command-line application that simulates the operation of an Indian Railways WAP-7 electric locomotive. This simulator offers a realistic experience of controlling a high-power AC electric locomotive, including pantograph operation, engine control, throttle management, and braking systems.

![WAP-7 Locomotive](https://upload.wikimedia.org/wikipedia/commons/thumb/9/97/30347_WAP-7_at_Sahibabad.jpg/320px-30347_WAP-7_at_Sahibabad.jpg)

## Features

- **Realistic Engine Simulation**
  - Proper startup/shutdown sequence
  - Multi-level throttle control
  - Progressive braking system
  - Emergency stop functionality
  - Pantograph control

- **Simulation Physics**
  - Speed calculations based on throttle position
  - Distance tracking
  - Runtime monitoring
  - Power system simulation (25kV AC)

- **User Interface**
  - Interactive command-line interface
  - Visual ASCII art display
  - Comprehensive help system
  - Status monitoring

## Installation

1. Ensure you have Java Development Kit (JDK) 17 or higher installed
2. Download the `WAP7Simulator.java` file
3. Compile the code:
   ```
   javac WAP7Simulator.java
   ```
4. Run the simulator:
   ```
   java WAP7Simulator
   ```

## Usage Guide

### Basic Operation Sequence

1. **Raise the pantograph** to connect to the overhead electrical line:
   ```
   >> pantograph up
   ```

2. **Start the engine**:
   ```
   >> start
   ```

3. **Increase throttle** to accelerate:
   ```
   >> throttle
   ```
   or set to a specific level (1-8):
   ```
   >> throttle 5
   ```

4. **Apply brakes** to slow down:
   ```
   >> brake
   ```

5. **Check status** at any time:
   ```
   >> status
   ```

6. **Stop the engine** when finished:
   ```
   >> stop
   ```

7. **Lower the pantograph**:
   ```
   >> pantograph down
   ```

### Advanced Commands

- **Emergency Stop** for immediate halt:
  ```
  >> emergency
  ```

- **Simulate Movement** for a specific duration (in seconds):
  ```
  >> simulate 120
  ```

- **Toggle Verbose Logging**:
  ```
  >> verbose
  ```

- **Exit** the simulator:
  ```
  >> exit
  ```

## Technical Details

### Engine Specifications

- **Maximum Speed**: 140 km/h
- **Throttle Levels**: 8 positions
- **Acceleration**: 10 km/h per throttle position
- **Power Supply**: 25kV AC
- **States**: OFF, IDLE, RUNNING, BRAKING, ERROR

### System Architecture

The simulator is built using object-oriented design principles with the following components:

- **WAP7Engine**: Core simulation logic
- **CommandProcessor**: User interface and command handling
- **EngineState**: State management enumeration
- **SimulationConstants**: Configuration parameters
- **EngineException**: Custom exception handling

## Example Session

```
>> pantograph up
Pantograph raised. Power available.

>> start
Engine started successfully.

>> throttle 3
Throttle set to 3. [RUNNING] 30 km/h T:3/8

>> simulate 60
Simulated 60s of movement. [RUNNING] 30 km/h T:3/8

>> status
State: Running | Speed: 30 km/h | Throttle: 3/8 | Pantograph: Up | Voltage: 25000 V | Distance: 500 m | Runtime: 78 s

>> brake
Brakes applied. [BRAKING] 20 km/h T:2/8

>> emergency
EMERGENCY STOP EXECUTED! Engine halted.

>> stop
Engine stopped.

>> exit
Exiting simulation. Final status:
State: Off | Speed: 0 km/h | Throttle: 0/8 | Pantograph: Up | Voltage: 25000 V | Distance: 500 m | Runtime: 85 s
```

## Future Enhancements

- Graphical user interface with JavaFX
- Track and signaling system
- Multiple train handling
- Terrain and weather effects
- Dynamic load calculations
- Multiplayer support for crew operations

## License

This project is released under the MIT License. See the LICENSE file for details.

---

Developed as an educational project for locomotive simulation and Java programming practice.
