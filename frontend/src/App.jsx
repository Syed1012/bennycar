import React, { useState, useEffect } from 'react';
import CarCard from './components/CarCard';
import CarForm from './components/CarForm';
import carService from './services/carService';
import './App.css';

function App() {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [editingCar, setEditingCar] = useState(null);
  const [filterBrand, setFilterBrand] = useState('');
  const [showAvailableOnly, setShowAvailableOnly] = useState(false);

  useEffect(() => {
    loadCars();
  }, []);

  const loadCars = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await carService.getAllCars();
      setCars(data);
    } catch (err) {
      setError('Failed to load cars. Please make sure the backend is running.');
      console.error('Error loading cars:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCar = () => {
    setEditingCar(null);
    setShowForm(true);
  };

  const handleEditCar = (car) => {
    setEditingCar(car);
    setShowForm(true);
  };

  const handleDeleteCar = async (id) => {
    if (window.confirm('Are you sure you want to delete this car?')) {
      try {
        await carService.deleteCar(id);
        await loadCars();
      } catch (err) {
        alert('Failed to delete car');
        console.error('Error deleting car:', err);
      }
    }
  };

  const handleFormSubmit = async (carData) => {
    try {
      if (editingCar) {
        await carService.updateCar(editingCar.id, carData);
      } else {
        await carService.createCar(carData);
      }
      setShowForm(false);
      setEditingCar(null);
      await loadCars();
    } catch (err) {
      alert('Failed to save car: ' + (err.response?.data?.message || err.message));
      console.error('Error saving car:', err);
    }
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setEditingCar(null);
  };

  const getFilteredCars = () => {
    let filtered = cars;

    if (filterBrand) {
      filtered = filtered.filter(car =>
        car.brand.toLowerCase().includes(filterBrand.toLowerCase())
      );
    }

    if (showAvailableOnly) {
      filtered = filtered.filter(car => car.isAvailable);
    }

    return filtered;
  };

  const uniqueBrands = [...new Set(cars.map(car => car.brand))].sort();
  const filteredCars = getFilteredCars();

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <h1>🚗 BennyCar</h1>
          <p>Car Inventory Management System</p>
        </div>
      </header>

      <main className="app-main">
        <div className="toolbar">
          <div className="toolbar-left">
            <button className="btn btn-primary" onClick={handleAddCar}>
              + Add New Car
            </button>
            <button className="btn btn-secondary" onClick={loadCars}>
              🔄 Refresh
            </button>
          </div>

          <div className="toolbar-right">
            <div className="filter-group">
              <label>
                <input
                  type="checkbox"
                  checked={showAvailableOnly}
                  onChange={(e) => setShowAvailableOnly(e.target.checked)}
                />
                <span>Available Only</span>
              </label>
            </div>

            <select
              className="filter-select"
              value={filterBrand}
              onChange={(e) => setFilterBrand(e.target.value)}
            >
              <option value="">All Brands</option>
              {uniqueBrands.map(brand => (
                <option key={brand} value={brand}>{brand}</option>
              ))}
            </select>
          </div>
        </div>

        <div className="stats-bar">
          <div className="stat-item">
            <span className="stat-label">Total Cars:</span>
            <span className="stat-value">{cars.length}</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">Showing:</span>
            <span className="stat-value">{filteredCars.length}</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">Available:</span>
            <span className="stat-value">{cars.filter(c => c.isAvailable).length}</span>
          </div>
        </div>

        {loading && (
          <div className="loading">
            <div className="spinner"></div>
            <p>Loading cars...</p>
          </div>
        )}

        {error && (
          <div className="error">
            <p>{error}</p>
            <button className="btn btn-secondary" onClick={loadCars}>
              Try Again
            </button>
          </div>
        )}

        {!loading && !error && filteredCars.length === 0 && (
          <div className="empty-state">
            <p>No cars found. {cars.length > 0 ? 'Try adjusting your filters.' : 'Add your first car to get started!'}</p>
          </div>
        )}

        {!loading && !error && filteredCars.length > 0 && (
          <div className="car-grid">
            {filteredCars.map(car => (
              <CarCard
                key={car.id}
                car={car}
                onEdit={handleEditCar}
                onDelete={handleDeleteCar}
              />
            ))}
          </div>
        )}
      </main>

      {showForm && (
        <CarForm
          car={editingCar}
          onSubmit={handleFormSubmit}
          onCancel={handleFormCancel}
        />
      )}
    </div>
  );
}

export default App;

