package io.digital.ninjava.jdkdemo;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Slf4j
public class PatternMatching implements Demo {

    static final int WEIGHT_PERSON = 70;

    interface Vehicle {
        String make();

        String model();

        int wheels();

        int mass();

        int totalMass();
    }

    interface Motorized extends Vehicle {
        int hp();

        double crumpleZone();
    }

    interface Car extends Motorized {
        default int wheels() {
            return 4;
        }

        int passengers();

        default int totalMass() {
            return mass() + passengers() * WEIGHT_PERSON;
        }

        default int typicalSpeed() {
            return 100;
        }

    }

    interface Truck extends Motorized {
        int cargoMass();

        default int totalMass() {
            return mass() + cargoMass() + WEIGHT_PERSON;
        }

        default int typicalSpeed() {
            return 80;
        }
    }

    interface Motorcycle extends Motorized {
        default int wheels() {
            return 2;
        }

        default int totalMass() {
            return mass() + WEIGHT_PERSON;
        }

        default int typicalSpeed() {
            return 130;
        }
    }

    interface Bicycle extends Vehicle {
        default int wheels() {
            return 2;
        }

        default int totalMass() {
            return mass() + WEIGHT_PERSON;
        }

        default int typicalSpeed() {
            return 25;
        }
    }

    @ToString
    static class TrekEmonda implements Bicycle {

        @Override
        public String make() {
            return "Trek";
        }

        @Override
        public String model() {
            return "Emonda";
        }

        @Override
        public int mass() {
            return 8;
        }

        @Override
        public int typicalSpeed() {
            return 30;
        }
    }

    record GenericCar(String make, String model, int mass, int hp, int passengers) implements Car {

        @Override
        public double crumpleZone() {
            return 1.5d;
        }
    }

    record GenericTruck(String make, String model, int mass, int hp, int wheels, int cargoMass) implements Truck {
        @Override
        public double crumpleZone() {
            return 0.5d;
        }
    }

    @ToString
    static class BmwZ3 implements Car {

        @Override
        public String make() {
            return "BMW";
        }

        @Override
        public String model() {
            return "Z3";
        }

        @Override
        public int mass() {
            return 1275;
        }

        @Override
        public int hp() {
            return 150;
        }

        @Override
        public double crumpleZone() {
            return 1.8;
        }

        @Override
        public int passengers() {
            return 2;
        }
    }

    @ToString
    static class LaFerrari implements Car {

        @Override
        public String make() {
            return "Ferrari";
        }

        @Override
        public String model() {
            return "LaFerrari";
        }

        @Override
        public int mass() {
            return 1255;
        }

        @Override
        public int hp() {
            return 798;
        }

        @Override
        public double crumpleZone() {
            return 0;
        }

        @Override
        public int passengers() {
            return 2;
        }

        @Override
        public int typicalSpeed() {
            return 120;
        }
    }

    @ToString
    static class Blackbird implements Motorcycle {

        @Override
        public String make() {
            return "Honda";
        }

        @Override
        public String model() {
            return "Blackbird";
        }

        @Override
        public int mass() {
            return 255;
        }

        @Override
        public int hp() {
            return 136;
        }

        @Override
        public double crumpleZone() {
            return 0.25;
        }

        @Override
        public int typicalSpeed() {
            return 180;
        }
    }

    record GenericMotorcycle(String make, String model, int mass, int hp) implements Motorcycle {
        @Override
        public double crumpleZone() {
            return 0.2d;
        }
    }

    private static Vehicle parseVehicle(Stack<String> stack) {
        String type = stack.pop();
        log.info("Trying to create a {}", type);
        return switch (type) {
            case "car", "auto" -> {
                var make = stack.pop();
                var model = stack.pop();
                var mass = Integer.parseInt(stack.pop());
                var hp = Integer.parseInt(stack.pop());
                var passengers = Integer.parseInt(stack.pop());
                yield new GenericCar(make, model, mass, hp, passengers);
            }
            case "z3", "bmw-z3" -> new BmwZ3();
            case "ferrari", "laFerrari" -> new LaFerrari();
            case "truck", "vrachtwagen" -> {
                var make = stack.pop();
                var model = stack.pop();
                var mass = Integer.parseInt(stack.pop());
                var hp = Integer.parseInt(stack.pop());
                var wheels = Integer.parseInt(stack.pop());
                var cargoMass = Integer.parseInt(stack.pop());
                yield new GenericTruck(make, model, mass, hp, wheels, cargoMass);
            }
            case "motor", "motorcycle" -> {
                var make = stack.pop();
                var model = stack.pop();
                var mass = Integer.parseInt(stack.pop());
                var hp = Integer.parseInt(stack.pop());
                yield new GenericMotorcycle(make, model, mass, hp);
            }
            case "blackbird", "honda-blackbird" -> new Blackbird();
            case "racefiets", "roadbike", "trek", "emonda" -> new TrekEmonda();

            default -> throw new IllegalArgumentException("Unknown type '" + type + "'");
        };
    }

    private static MovingVehicle parseMovingVehicle(Stack<String> stack) {
        var vehicle = parseVehicle(stack);
        var speed = Integer.parseInt(stack.pop());

        if (vehicle instanceof GenericCar c && speed > (1.5 * c.typicalSpeed())) {
            log.warn("{} is pretty fast for a car, even if it is a {} {}", speed, c.make(), c.model());
        }
        if (vehicle instanceof LaFerrari f && speed < 0.8 * f.typicalSpeed()) {
            log.warn("Be realistic; Ferrari drivers don't drive below {} km/h", f.typicalSpeed());
        }
        return new MovingVehicle(vehicle, speed);
    }

    record MovingVehicle(Vehicle vehicle, int speed) {

        double speedMS() {
            return speed * 1000 / 3600d;
        }

        double kineticEnergy() {
            var massKg = vehicle.totalMass();
            var speedMS = speedMS();
            return 0.5 * massKg * speedMS * speedMS;
        }

        double stoppingTime() {
            double stopDistance;
            if (vehicle instanceof Motorized motorized) {
                stopDistance = motorized.crumpleZone();
            } else {
                stopDistance = 0.5d;
            }
            // my physics suck. Let's just assume the time it would normally take to travel this distance
            return stopDistance / speedMS();
        }

    }

    @Override
    public void go(String... args) {
        List<MovingVehicle> vehicles = createVehicles(args);

        log.info("Found the following moving vehicles: {}", StringUtils.join(vehicles));
        if (vehicles.size() >= 2) {
            var a = RandomUtils.nextInt(0, vehicles.size());
            int b;
            do {
                b = RandomUtils.nextInt(0, vehicles.size());
            } while (b == a);

            crash(vehicles.get(a), vehicles.get(b));
        }
    }

    private static double impactForce(double kineticEnergy, double distance) {
        return kineticEnergy / distance;
    }

    private void crash(MovingVehicle north, MovingVehicle south) {
        log.info("Uh oh. A {} is traveling north-bound with {} km/h, and is not paying attention.",
                north.vehicle() instanceof  Motorized m ? m.make() + " " + m.model() : "bike",
                north.speed());

        log.info("At the same time, a {} is traveling south-bound with {} km/h on the same road",
                south.vehicle() instanceof  Motorized m ? m.make() + " " + m.model() : "bike",
                south.speed());

        var keNorth = north.kineticEnergy();
        var keSouth = south.kineticEnergy();
        var totalEnergy = keNorth + keSouth;

        log.info("A{} crash follows. Total impact is {} J",
                totalEnergy > 7_000_000 ? " humongous" :
                        totalEnergy > 1_000_000 ? "n enormous" :
                                " ",
                totalEnergy);

    }


    private List<MovingVehicle> createVehicles(String[] args) {
        var asList = new ArrayList<>(Arrays.asList(args));
        Collections.reverse(asList);
        var asStack = new Stack<String>();
        asList.forEach(asStack::push);

        List<MovingVehicle> movingVehicles = new ArrayList<>();
        while (!asStack.isEmpty()) {
            movingVehicles.add(parseMovingVehicle(asStack));
        }
        return movingVehicles;
    }

}
