package de.bennycar.vehicle.config;

import de.bennycar.vehicle.domain.Brand;
import de.bennycar.vehicle.domain.Vehicle;
import de.bennycar.vehicle.domain.VehicleType;
import de.bennycar.vehicle.repository.BrandRepository;
import de.bennycar.vehicle.repository.VehicleRepository;
import de.bennycar.vehicle.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data seeder for development environment.
 * Seeds the database with real car data including brands, types, and vehicles.
 */
@Slf4j
@Component
@Profile("dev")
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final BrandRepository brandRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (brandRepository.count() > 0) {
            log.info("Database already seeded. Skipping data seeding.");
            return;
        }

        log.info("Starting data seeding...");

        // Seed Vehicle Types
        List<VehicleType> vehicleTypes = seedVehicleTypes();
        log.info("Seeded {} vehicle types", vehicleTypes.size());

        // Seed Brands
        List<Brand> brands = seedBrands();
        log.info("Seeded {} brands", brands.size());

        // Seed Vehicles
        seedVehicles(brands, vehicleTypes);
        log.info("Data seeding completed successfully!");
    }

    private List<VehicleType> seedVehicleTypes() {
        List<VehicleType> types = Arrays.asList(
                VehicleType.builder()
                        .name("Sedan")
                        .description("Four-door passenger car with a separate trunk")
                        .build(),
                VehicleType.builder()
                        .name("SUV")
                        .description("Sport Utility Vehicle with higher ground clearance")
                        .build(),
                VehicleType.builder()
                        .name("Coupe")
                        .description("Two-door car with a fixed roof")
                        .build(),
                VehicleType.builder()
                        .name("Convertible")
                        .description("Car with a retractable or removable roof")
                        .build(),
                VehicleType.builder()
                        .name("Hatchback")
                        .description("Car with a rear door that opens upward")
                        .build(),
                VehicleType.builder()
                        .name("Truck")
                        .description("Large vehicle designed for transporting cargo")
                        .build(),
                VehicleType.builder()
                        .name("Electric")
                        .description("Electric vehicle powered by battery")
                        .build()
        );

        return vehicleTypeRepository.saveAll(types);
    }

    private List<Brand> seedBrands() {
        List<Brand> brands = Arrays.asList(
                Brand.builder()
                        .name("Tesla")
                        .description("American electric vehicle and clean energy company")
                        .countryOfOrigin("United States")
                        .foundedYear(2003)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Tesla_Motors.svg/200px-Tesla_Motors.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("BMW")
                        .description("German luxury automobile manufacturer")
                        .countryOfOrigin("Germany")
                        .foundedYear(1916)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/BMW.svg/200px-BMW.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Mercedes-Benz")
                        .description("German luxury automotive brand")
                        .countryOfOrigin("Germany")
                        .foundedYear(1926)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Mercedes-Logo.svg/200px-Mercedes-Logo.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Audi")
                        .description("German luxury automobile manufacturer")
                        .countryOfOrigin("Germany")
                        .foundedYear(1909)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Audi-Logo_2016.svg/200px-Audi-Logo_2016.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Porsche")
                        .description("German luxury sports car manufacturer")
                        .countryOfOrigin("Germany")
                        .foundedYear(1931)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Porsche_logo.svg/200px-Porsche_logo.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Toyota")
                        .description("Japanese automotive manufacturer")
                        .countryOfOrigin("Japan")
                        .foundedYear(1937)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Toyota_logo.svg/200px-Toyota_logo.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Ford")
                        .description("American automobile manufacturer")
                        .countryOfOrigin("United States")
                        .foundedYear(1903)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3e/Ford_logo_flat.svg/200px-Ford_logo_flat.svg.png")
                        .active(true)
                        .build(),
                Brand.builder()
                        .name("Lamborghini")
                        .description("Italian luxury sports car manufacturer")
                        .countryOfOrigin("Italy")
                        .foundedYear(1963)
                        .logoUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Lamborghini_logo.svg/200px-Lamborghini_logo.svg.png")
                        .active(true)
                        .build()
        );

        return brandRepository.saveAll(brands);
    }

    private void seedVehicles(List<Brand> brands, List<VehicleType> types) {
        Brand tesla = brands.stream().filter(b -> b.getName().equals("Tesla")).findFirst().orElse(null);
        Brand bmw = brands.stream().filter(b -> b.getName().equals("BMW")).findFirst().orElse(null);
        Brand mercedes = brands.stream().filter(b -> b.getName().equals("Mercedes-Benz")).findFirst().orElse(null);
        Brand audi = brands.stream().filter(b -> b.getName().equals("Audi")).findFirst().orElse(null);
        Brand porsche = brands.stream().filter(b -> b.getName().equals("Porsche")).findFirst().orElse(null);
        Brand toyota = brands.stream().filter(b -> b.getName().equals("Toyota")).findFirst().orElse(null);
        Brand ford = brands.stream().filter(b -> b.getName().equals("Ford")).findFirst().orElse(null);
        Brand lamborghini = brands.stream().filter(b -> b.getName().equals("Lamborghini")).findFirst().orElse(null);

        VehicleType sedan = types.stream().filter(t -> t.getName().equals("Sedan")).findFirst().orElse(null);
        VehicleType suv = types.stream().filter(t -> t.getName().equals("SUV")).findFirst().orElse(null);
        VehicleType coupe = types.stream().filter(t -> t.getName().equals("Coupe")).findFirst().orElse(null);
        VehicleType electric = types.stream().filter(t -> t.getName().equals("Electric")).findFirst().orElse(null);
        VehicleType truck = types.stream().filter(t -> t.getName().equals("Truck")).findFirst().orElse(null);

        List<Vehicle> vehicles = Arrays.asList(
                // Tesla Vehicles
                createVehicle(tesla, electric, "Model S", 2024,
                        "Luxury electric sedan with cutting-edge technology and exceptional performance",
                        new BigDecimal("79990"),
                        "Dual Motor", "Single-Speed", "Electric", "670 HP",
                        5, 709, "120 MPGe",
                        "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=800&h=600&fit=crop",
                                "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=800&h=600&fit=crop"),
                        "AVAILABLE", 15),

                createVehicle(tesla, electric, "Model 3", 2024,
                        "Compact electric sedan with impressive range and performance",
                        new BigDecimal("38990"),
                        "Single Motor", "Single-Speed", "Electric", "283 HP",
                        5, 425, "132 MPGe",
                        "https://images.unsplash.com/photo-1617531653332-bd46c24f2068?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1617531653332-bd46c24f2068?w=800&h=600&fit=crop"),
                        "AVAILABLE", 25),

                createVehicle(tesla, suv, "Model X", 2024,
                        "Luxury electric SUV with falcon-wing doors and advanced autopilot",
                        new BigDecimal("79990"),
                        "Dual Motor", "Single-Speed", "Electric", "670 HP",
                        7, 88, "102 MPGe",
                        "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1617788138017-80ad40651399?w=800&h=600&fit=crop"),
                        "AVAILABLE", 10),

                createVehicle(tesla, suv, "Model Y", 2024,
                        "Compact electric SUV perfect for families",
                        new BigDecimal("47740"),
                        "Dual Motor", "Single-Speed", "Electric", "384 HP",
                        7, 76, "129 MPGe",
                        "https://images.unsplash.com/photo-1617531653332-bd46c24f2068?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 20),

                // BMW Vehicles
                createVehicle(bmw, sedan, "3 Series", 2024,
                        "Luxury compact sedan with sporty performance and elegant design",
                        new BigDecimal("43945"),
                        "2.0L Turbo I4", "8-Speed Automatic", "Gasoline", "255 HP",
                        5, 480, "26 MPG",
                        "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop"),
                        "AVAILABLE", 18),

                createVehicle(bmw, suv, "X5", 2024,
                        "Premium midsize SUV with powerful engine and luxurious interior",
                        new BigDecimal("61900"),
                        "3.0L Turbo I6", "8-Speed Automatic", "Gasoline", "335 HP",
                        7, 72, "23 MPG",
                        "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=800&h=600&fit=crop"),
                        "AVAILABLE", 12),

                createVehicle(bmw, coupe, "M4", 2024,
                        "High-performance sports coupe with track-ready capabilities",
                        new BigDecimal("74900"),
                        "3.0L Twin-Turbo I6", "8-Speed Automatic", "Gasoline", "473 HP",
                        4, 13, "20 MPG",
                        "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 8),

                // Mercedes-Benz Vehicles
                createVehicle(mercedes, sedan, "E-Class", 2024,
                        "Luxury midsize sedan with advanced technology and comfort",
                        new BigDecimal("55850"),
                        "2.0L Turbo I4", "9-Speed Automatic", "Gasoline", "255 HP",
                        5, 540, "25 MPG",
                        "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&h=600&fit=crop"),
                        "AVAILABLE", 14),

                createVehicle(mercedes, suv, "GLE", 2024,
                        "Luxury midsize SUV with spacious interior and advanced features",
                        new BigDecimal("57950"),
                        "2.0L Turbo I4", "9-Speed Automatic", "Gasoline", "255 HP",
                        7, 33, "22 MPG",
                        "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 16),

                createVehicle(mercedes, coupe, "AMG GT", 2024,
                        "High-performance sports car with racing heritage",
                        new BigDecimal("134900"),
                        "4.0L Twin-Turbo V8", "7-Speed Dual-Clutch", "Gasoline", "523 HP",
                        2, 10, "18 MPG",
                        "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 5),

                // Audi Vehicles
                createVehicle(audi, sedan, "A4", 2024,
                        "Premium compact sedan with quattro all-wheel drive",
                        new BigDecimal("39900"),
                        "2.0L Turbo I4", "7-Speed Dual-Clutch", "Gasoline", "261 HP",
                        5, 13, "27 MPG",
                        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop"),
                        "AVAILABLE", 22),

                createVehicle(audi, suv, "Q7", 2024,
                        "Luxury three-row SUV with advanced technology",
                        new BigDecimal("57995"),
                        "3.0L Turbo V6", "8-Speed Automatic", "Gasoline", "335 HP",
                        7, 14, "20 MPG",
                        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 11),

                createVehicle(audi, coupe, "R8", 2024,
                        "Supercar with V10 engine and quattro all-wheel drive",
                        new BigDecimal("169900"),
                        "5.2L V10", "7-Speed Dual-Clutch", "Gasoline", "562 HP",
                        2, 8, "15 MPG",
                        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 3),

                // Porsche Vehicles
                createVehicle(porsche, coupe, "911", 2024,
                        "Iconic sports car with legendary performance",
                        new BigDecimal("107550"),
                        "3.0L Twin-Turbo Flat-6", "8-Speed Dual-Clutch", "Gasoline", "379 HP",
                        4, 4, "21 MPG",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop"),
                        "AVAILABLE", 7),

                createVehicle(porsche, suv, "Cayenne", 2024,
                        "Luxury SUV with sports car DNA",
                        new BigDecimal("72900"),
                        "3.0L Turbo V6", "8-Speed Automatic", "Gasoline", "335 HP",
                        5, 27, "20 MPG",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 9),

                createVehicle(porsche, electric, "Taycan", 2024,
                        "All-electric sports sedan with Porsche performance",
                        new BigDecimal("86900"),
                        "Dual Permanent Magnet", "2-Speed", "Electric", "402 HP",
                        4, 84, "79 MPGe",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 6),

                // Toyota Vehicles
                createVehicle(toyota, sedan, "Camry", 2024,
                        "Reliable midsize sedan with excellent fuel economy",
                        new BigDecimal("26520"),
                        "2.5L I4", "8-Speed Automatic", "Gasoline", "203 HP",
                        5, 15, "32 MPG",
                        "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800&h=600&fit=crop"),
                        "AVAILABLE", 30),

                createVehicle(toyota, suv, "RAV4", 2024,
                        "Compact SUV with hybrid option and great reliability",
                        new BigDecimal("28010"),
                        "2.5L I4 Hybrid", "CVT", "Hybrid", "219 HP",
                        5, 37, "40 MPG",
                        "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 28),

                createVehicle(toyota, truck, "Tundra", 2024,
                        "Full-size pickup truck with powerful V6 engine",
                        new BigDecimal("36965"),
                        "3.5L Twin-Turbo V6", "10-Speed Automatic", "Gasoline", "389 HP",
                        5, 22, "20 MPG",
                        "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 15),

                // Ford Vehicles
                createVehicle(ford, truck, "F-150", 2024,
                        "America's best-selling truck with powerful performance",
                        new BigDecimal("33490"),
                        "3.5L EcoBoost V6", "10-Speed Automatic", "Gasoline", "400 HP",
                        5, 52, "20 MPG",
                        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop"),
                        "AVAILABLE", 25),

                createVehicle(ford, suv, "Explorer", 2024,
                        "Three-row SUV with advanced technology and safety features",
                        new BigDecimal("37325"),
                        "2.3L EcoBoost I4", "10-Speed Automatic", "Gasoline", "300 HP",
                        7, 18, "23 MPG",
                        "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 20),

                createVehicle(ford, coupe, "Mustang", 2024,
                        "Iconic American muscle car with powerful V8 engine",
                        new BigDecimal("30290"),
                        "5.0L V8", "6-Speed Manual", "Gasoline", "450 HP",
                        4, 13, "16 MPG",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 12),

                // Lamborghini Vehicles
                createVehicle(lamborghini, coupe, "Huracán", 2024,
                        "Exotic supercar with V10 engine and stunning design",
                        new BigDecimal("208571"),
                        "5.2L V10", "7-Speed Dual-Clutch", "Gasoline", "631 HP",
                        2, 3, "13 MPG",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of("https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop"),
                        "AVAILABLE", 2),

                createVehicle(lamborghini, suv, "Urus", 2024,
                        "Luxury super SUV with supercar performance",
                        new BigDecimal("218009"),
                        "4.0L Twin-Turbo V8", "8-Speed Automatic", "Gasoline", "641 HP",
                        5, 21, "14 MPG",
                        "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&h=600&fit=crop",
                        Set.of(),
                        "AVAILABLE", 4)
        );

        vehicleRepository.saveAll(vehicles);
        log.info("Seeded {} vehicles", vehicles.size());
    }

    private Vehicle createVehicle(Brand brand, VehicleType type, String model, Integer year,
                                  String description, BigDecimal price, String engine, String transmission,
                                  String fuelType, String horsepower, Integer seats, Integer cargo,
                                  String efficiency, String mainImage, Set<String> additionalImages,
                                  String status, Integer stock) {
        return Vehicle.builder()
                .brand(brand)
                .vehicleType(type)
                .model(model)
                .modelYear(year)
                .description(description)
                .basePrice(price)
                .engine(engine)
                .transmission(transmission)
                .fuelType(fuelType)
                .horsepower(horsepower)
                .seatingCapacity(seats)
                .cargoCapacityLiters(cargo)
                .fuelEfficiency(efficiency)
                .mainImageUrl(mainImage)
                .additionalImages(additionalImages != null ? additionalImages : new HashSet<>())
                .status(status)
                .stockQuantity(stock)
                .build();
    }
}
