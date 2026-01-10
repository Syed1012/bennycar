#!/bin/bash

# =============================================================================
# BennyCar - Populate Vehicle Database with Test Data
# =============================================================================

set -e  # Exit on error

BASE_URL="http://localhost:8082/api/v1"

echo "=========================================="
echo "BennyCar Vehicle Data Population Script"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to make POST request and extract ID
create_brand() {
    local name=$1
    local description=$2
    local logo_url=$3
    local country=$4
    local founded=$5

    echo -e "${BLUE}Creating brand: $name${NC}"

    response=$(curl -s -X POST "$BASE_URL/brands" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"description\": \"$description\",
            \"logoUrl\": \"$logo_url\",
            \"countryOfOrigin\": \"$country\",
            \"foundedYear\": $founded
        }")

    # Extract ID from response
    id=$(echo "$response" | grep -o '"id":"[^"]*"' | sed 's/"id":"\([^"]*\)"/\1/')
    echo -e "${GREEN}✓ Created: $name (ID: $id)${NC}"
    echo "$id"
}

create_vehicle_type() {
    local name=$1
    local description=$2
    local icon_url=$3

    echo -e "${BLUE}Creating vehicle type: $name${NC}"

    response=$(curl -s -X POST "$BASE_URL/vehicle-types" \
        -H "Content-Type: application/json" \
        -d "{
            \"name\": \"$name\",
            \"description\": \"$description\",
            \"iconUrl\": \"$icon_url\"
        }")

    id=$(echo "$response" | grep -o '"id":"[^"]*"' | sed 's/"id":"\([^"]*\)"/\1/')
    echo -e "${GREEN}✓ Created: $name (ID: $id)${NC}"
    echo "$id"
}

echo "Step 1: Creating Brands..."
echo "----------------------------"

BMW_ID=$(create_brand "BMW" \
    "Bayerische Motoren Werke AG - Premium German automobile manufacturer known for performance and luxury" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/BMW.svg/200px-BMW.svg.png" \
    "Germany" 1916)

MERCEDES_ID=$(create_brand "Mercedes-Benz" \
    "Daimler AG brand - German luxury automotive manufacturer producing premium vehicles" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Mercedes-Logo.svg/200px-Mercedes-Logo.svg.png" \
    "Germany" 1926)

AUDI_ID=$(create_brand "Audi" \
    "Audi AG - German luxury automobile manufacturer, part of Volkswagen Group" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Audi_logo.svg/200px-Audi_logo.svg.png" \
    "Germany" 1909)

PORSCHE_ID=$(create_brand "Porsche" \
    "Dr. Ing. h.c. F. Porsche AG - German sports car manufacturer known for high performance" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Porsche_logo.svg/200px-Porsche_logo.svg.png" \
    "Germany" 1931)

TESLA_ID=$(create_brand "Tesla" \
    "Tesla, Inc. - American electric vehicle and clean energy company" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Tesla_Motors.svg/200px-Tesla_Motors.svg.png" \
    "United States" 2003)

LAMBORGHINI_ID=$(create_brand "Lamborghini" \
    "Automobili Lamborghini S.p.A. - Italian luxury sports car manufacturer" \
    "https://upload.wikimedia.org/wikipedia/en/thumb/d/df/Lamborghini_Logo.svg/200px-Lamborghini_Logo.svg.png" \
    "Italy" 1963)

FERRARI_ID=$(create_brand "Ferrari" \
    "Ferrari S.p.A. - Italian luxury sports car manufacturer, racing icon" \
    "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Ferrari_logo.svg/200px-Ferrari_logo.svg.png" \
    "Italy" 1947)

echo ""
echo "Step 2: Creating Vehicle Types..."
echo "-----------------------------------"

SEDAN_ID=$(create_vehicle_type "Sedan" \
    "Four-door passenger car with separate trunk, ideal for families and business" \
    "https://cdn-icons-png.flaticon.com/512/3097/3097153.png")

SUV_ID=$(create_vehicle_type "SUV" \
    "Sport Utility Vehicle - Spacious vehicle combining off-road capability with passenger comfort" \
    "https://cdn-icons-png.flaticon.com/512/3448/3448339.png")

COUPE_ID=$(create_vehicle_type "Coupe" \
    "Two-door sports car with sleek design and performance focus" \
    "https://cdn-icons-png.flaticon.com/512/2991/2991695.png")

CONVERTIBLE_ID=$(create_vehicle_type "Convertible" \
    "Retractable roof vehicle combining open-air driving with luxury" \
    "https://cdn-icons-png.flaticon.com/512/3774/3774278.png")

SPORTSCAR_ID=$(create_vehicle_type "Sports Car" \
    "High-performance vehicle designed for speed, agility, and driver engagement" \
    "https://cdn-icons-png.flaticon.com/512/3523/3523063.png")

ELECTRIC_ID=$(create_vehicle_type "Electric" \
    "Battery-powered vehicle with zero emissions and advanced technology" \
    "https://cdn-icons-png.flaticon.com/512/2913/2913133.png")

echo ""
echo "=========================================="
echo "Brand and Vehicle Type IDs:"
echo "=========================================="
echo "BMW_ID=$BMW_ID"
echo "MERCEDES_ID=$MERCEDES_ID"
echo "AUDI_ID=$AUDI_ID"
echo "PORSCHE_ID=$PORSCHE_ID"
echo "TESLA_ID=$TESLA_ID"
echo "LAMBORGHINI_ID=$LAMBORGHINI_ID"
echo "FERRARI_ID=$FERRARI_ID"
echo ""
echo "SEDAN_ID=$SEDAN_ID"
echo "SUV_ID=$SUV_ID"
echo "COUPE_ID=$COUPE_ID"
echo "CONVERTIBLE_ID=$CONVERTIBLE_ID"
echo "SPORTSCAR_ID=$SPORTSCAR_ID"
echo "ELECTRIC_ID=$ELECTRIC_ID"
echo "=========================================="
echo ""

# Export for use in vehicle creation
export BMW_ID MERCEDES_ID AUDI_ID PORSCHE_ID TESLA_ID LAMBORGHINI_ID FERRARI_ID
export SEDAN_ID SUV_ID COUPE_ID CONVERTIBLE_ID SPORTSCAR_ID ELECTRIC_ID

echo "Step 3: Creating Vehicles..."
echo "----------------------------"
echo ""

# Create vehicles function
create_vehicle() {
    local brand_id=$1
    local type_id=$2
    local model=$3
    local year=$4
    local description=$5
    local price=$6
    local engine=$7
    local transmission=$8
    local fuel=$9
    local hp=${10}
    local seats=${11}
    local cargo=${12}
    local efficiency=${13}
    local main_image=${14}
    local stock=${15}

    echo -e "${BLUE}Creating vehicle: $model${NC}"

    curl -s -X POST "$BASE_URL/vehicles" \
        -H "Content-Type: application/json" \
        -d "{
            \"brandId\": \"$brand_id\",
            \"vehicleTypeId\": \"$type_id\",
            \"model\": \"$model\",
            \"modelYear\": $year,
            \"description\": \"$description\",
            \"basePrice\": $price,
            \"engine\": \"$engine\",
            \"transmission\": \"$transmission\",
            \"fuelType\": \"$fuel\",
            \"horsepower\": \"$hp\",
            \"seatingCapacity\": $seats,
            \"cargoCapacityLiters\": $cargo,
            \"fuelEfficiency\": \"$efficiency\",
            \"mainImageUrl\": \"$main_image\",
            \"stockQuantity\": $stock
        }" > /dev/null

    echo -e "${GREEN}✓ Created: $model${NC}"
}

# BMW Vehicles
create_vehicle "$BMW_ID" "$SEDAN_ID" "3 Series 330i" 2024 \
    "The BMW 3 Series is a compact executive sedan that perfectly balances sportiness and luxury." \
    45000.00 "2.0L 4-cylinder Turbo" "8-speed Automatic" "Petrol" "255 hp" 5 480 "6.5 L/100km" \
    "https://images.unsplash.com/photo-1555215695-3004980ad54e?w=1200&h=800&fit=crop" 8

create_vehicle "$BMW_ID" "$SUV_ID" "X5 xDrive40i" 2024 \
    "The BMW X5 is a luxury midsize SUV that combines versatility with performance." \
    68000.00 "3.0L 6-cylinder Turbo" "8-speed Automatic" "Petrol" "335 hp" 7 650 "9.2 L/100km" \
    "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=1200&h=800&fit=crop" 5

create_vehicle "$BMW_ID" "$COUPE_ID" "M4 Competition" 2024 \
    "The BMW M4 Competition is a high-performance coupe that embodies BMW M's racing heritage." \
    85000.00 "3.0L 6-cylinder Twin-Turbo" "8-speed M Steptronic" "Petrol" "503 hp" 4 440 "10.5 L/100km" \
    "https://images.unsplash.com/photo-1617814076367-b759c7d7e738?w=1200&h=800&fit=crop" 3

# Mercedes-Benz Vehicles
create_vehicle "$MERCEDES_ID" "$SEDAN_ID" "C 300" 2024 \
    "The Mercedes-Benz C-Class defines modern luxury with its elegant design." \
    48000.00 "2.0L 4-cylinder Turbo" "9-speed Automatic" "Petrol" "255 hp" 5 455 "6.8 L/100km" \
    "https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?w=1200&h=800&fit=crop" 10

create_vehicle "$MERCEDES_ID" "$SUV_ID" "GLE 450 4MATIC" 2024 \
    "The Mercedes-Benz GLE combines SUV versatility with Mercedes luxury." \
    72000.00 "3.0L 6-cylinder Turbo + EQ Boost" "9-speed Automatic" "Hybrid" "362 hp" 7 630 "8.5 L/100km" \
    "https://images.unsplash.com/photo-1606016159991-4855c8c8e4fa?w=1200&h=800&fit=crop" 6

create_vehicle "$MERCEDES_ID" "$SPORTSCAR_ID" "AMG GT 63 S" 2024 \
    "The Mercedes-AMG GT is a handcrafted performance masterpiece." \
    165000.00 "4.0L V8 Biturbo" "9-speed AMG Speedshift" "Petrol" "630 hp" 4 395 "12.8 L/100km" \
    "https://images.unsplash.com/photo-1553440569-bcc63803a83d?w=1200&h=800&fit=crop" 2

# Audi Vehicles
create_vehicle "$AUDI_ID" "$SEDAN_ID" "A4 45 TFSI" 2024 \
    "The Audi A4 combines progressive design with advanced technology." \
    46000.00 "2.0L 4-cylinder TFSI" "7-speed S tronic" "Petrol" "261 hp" 5 460 "6.3 L/100km" \
    "https://images.unsplash.com/photo-1610768764270-790fbec18178?w=1200&h=800&fit=crop" 12

create_vehicle "$AUDI_ID" "$SUV_ID" "Q7 55 TFSI" 2024 \
    "The Audi Q7 is a full-size luxury SUV offering three rows of seating." \
    70000.00 "3.0L V6 TFSI" "8-speed Tiptronic" "Petrol" "335 hp" 7 770 "9.0 L/100km" \
    "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=1200&h=800&fit=crop" 7

create_vehicle "$AUDI_ID" "$SPORTSCAR_ID" "R8 V10 Performance" 2024 \
    "The Audi R8 is a naturally aspirated supercar with racing DNA." \
    208000.00 "5.2L V10 FSI" "7-speed S tronic" "Petrol" "612 hp" 2 112 "13.4 L/100km" \
    "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?w=1200&h=800&fit=crop" 1

# Porsche Vehicles
create_vehicle "$PORSCHE_ID" "$SPORTSCAR_ID" "911 Carrera S" 2024 \
    "The legendary Porsche 911 needs no introduction." \
    132000.00 "3.0L 6-cylinder Twin-Turbo" "8-speed PDK" "Petrol" "443 hp" 4 132 "9.0 L/100km" \
    "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=1200&h=800&fit=crop" 4

create_vehicle "$PORSCHE_ID" "$ELECTRIC_ID" "Taycan Turbo S" 2024 \
    "The Porsche Taycan Turbo S is an all-electric sports sedan." \
    185000.00 "Dual Electric Motors" "2-speed Automatic" "Electric" "761 hp" 4 366 "26.4 kWh/100km" \
    "https://images.unsplash.com/photo-1614200187524-dc4b892acf16?w=1200&h=800&fit=crop" 3

create_vehicle "$PORSCHE_ID" "$SUV_ID" "Cayenne Turbo" 2024 \
    "The Porsche Cayenne Turbo combines SUV practicality with sports car performance." \
    142000.00 "4.0L V8 Twin-Turbo" "8-speed Tiptronic S" "Petrol" "541 hp" 5 770 "11.3 L/100km" \
    "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=1200&h=800&fit=crop" 4

# Tesla Vehicles
create_vehicle "$TESLA_ID" "$ELECTRIC_ID" "Model S Plaid" 2024 \
    "The Tesla Model S Plaid is the ultimate electric performance sedan." \
    135000.00 "Tri-Motor Electric" "Single-speed Automatic" "Electric" "1020 hp" 5 793 "18.9 kWh/100km" \
    "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=1200&h=800&fit=crop" 5

create_vehicle "$TESLA_ID" "$SUV_ID" "Model X Plaid" 2024 \
    "The Tesla Model X Plaid is an all-electric SUV with falcon-wing doors." \
    125000.00 "Tri-Motor Electric" "Single-speed Automatic" "Electric" "1020 hp" 7 2577 "21.3 kWh/100km" \
    "https://images.unsplash.com/photo-1617788138017-80ad40651399?w=1200&h=800&fit=crop" 6

create_vehicle "$TESLA_ID" "$ELECTRIC_ID" "Model 3 Performance" 2024 \
    "The Tesla Model 3 Performance delivers thrilling acceleration." \
    58000.00 "Dual Motor Electric" "Single-speed Automatic" "Electric" "450 hp" 5 561 "14.9 kWh/100km" \
    "https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=1200&h=800&fit=crop" 15

# Lamborghini Vehicles
create_vehicle "$LAMBORGHINI_ID" "$SUV_ID" "Urus Performante" 2024 \
    "The Lamborghini Urus Performante is the world's first super SUV." \
    245000.00 "4.0L V8 Twin-Turbo" "8-speed Automatic" "Petrol" "666 hp" 5 616 "12.5 L/100km" \
    "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?w=1200&h=800&fit=crop" 2

create_vehicle "$LAMBORGHINI_ID" "$SPORTSCAR_ID" "Aventador SVJ" 2024 \
    "The Lamborghini Aventador SVJ is a naturally aspirated V12 masterpiece." \
    517770.00 "6.5L V12 Naturally Aspirated" "7-speed ISR" "Petrol" "770 hp" 2 110 "17.2 L/100km" \
    "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=1200&h=800&fit=crop" 1

create_vehicle "$LAMBORGHINI_ID" "$SPORTSCAR_ID" "Huracán EVO" 2024 \
    "The Lamborghini Huracán EVO is powered by a naturally aspirated V10 engine." \
    287000.00 "5.2L V10 Naturally Aspirated" "7-speed Dual-Clutch" "Petrol" "631 hp" 2 100 "13.7 L/100km" \
    "https://images.unsplash.com/photo-1525609004556-c46c7d6cf023?w=1200&h=800&fit=crop" 2

# Ferrari Vehicles
create_vehicle "$FERRARI_ID" "$SPORTSCAR_ID" "F8 Tributo" 2024 \
    "The Ferrari F8 Tributo is a mid-rear-engined sports car." \
    295000.00 "3.9L V8 Twin-Turbo" "7-speed Dual-Clutch" "Petrol" "710 hp" 2 200 "11.6 L/100km" \
    "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=1200&h=800&fit=crop" 2

create_vehicle "$FERRARI_ID" "$SPORTSCAR_ID" "SF90 Stradale" 2024 \
    "The Ferrari SF90 Stradale is Ferrari's first plug-in hybrid supercar." \
    430000.00 "4.0L V8 Twin-Turbo + 3 Electric Motors" "8-speed Dual-Clutch" "Plug-in Hybrid" "1000 hp" 2 74 "7.9 L/100km + 25km Electric" \
    "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=1200&h=800&fit=crop" 1

create_vehicle "$FERRARI_ID" "$COUPE_ID" "Roma" 2024 \
    "The Ferrari Roma is a grand touring coupe that combines timeless Italian elegance." \
    226000.00 "3.9L V8 Twin-Turbo" "8-speed Dual-Clutch" "Petrol" "612 hp" 4 272 "11.2 L/100km" \
    "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=1200&h=800&fit=crop" 3

echo ""
echo "=========================================="
echo "✓ Database Population Complete!"
echo "=========================================="
echo "Created:"
echo "  - 7 Brands"
echo "  - 6 Vehicle Types"
echo "  - 21 Vehicles"
echo "=========================================="
echo ""
echo "You can now browse vehicles at: http://localhost:3000/vehicles"
echo ""

