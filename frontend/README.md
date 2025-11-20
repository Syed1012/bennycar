# BennyCar Frontend

A React + Vite frontend application for managing car inventory.

## Features

- 📋 View all cars in a card-based grid layout
- ➕ Add new cars to the inventory
- ✏️ Edit existing car details
- 🗑️ Delete cars from inventory
- 🔍 Filter cars by brand
- ✅ Filter to show only available cars
- 📊 View statistics (total cars, available cars, etc.)

## Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- BennyCar backend running on `http://localhost:8081`

## Installation

```bash
cd frontend
npm install
```

## Running the Application

```bash
npm run dev
```

The application will start on `http://localhost:5173`

## Building for Production

```bash
npm run build
```

The production-ready files will be in the `dist` folder.

## Project Structure

```
frontend/
├── src/
│   ├── components/
│   │   ├── CarCard.jsx          # Card component for displaying car details
│   │   ├── CarCard.css          # Styles for CarCard
│   │   ├── CarForm.jsx          # Form component for adding/editing cars
│   │   └── CarForm.css          # Styles for CarForm
│   ├── services/
│   │   └── carService.js        # API service for backend communication
│   ├── App.jsx                  # Main application component
│   ├── App.css                  # Main application styles
│   ├── main.jsx                 # Application entry point
│   └── index.css                # Global styles
├── package.json
└── vite.config.js
```

## API Configuration

The frontend connects to the backend API at `http://localhost:8081/api/cars`. 

If you need to change the API URL, edit the `API_BASE_URL` constant in `src/services/carService.js`.

## Technologies Used

- **React** - UI library
- **Vite** - Build tool and dev server
- **Axios** - HTTP client for API requests
- **CSS3** - Styling

