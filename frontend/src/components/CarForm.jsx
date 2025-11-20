import React, { useState, useEffect } from 'react';
import './CarForm.css';

const CarForm = ({ car, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    vin: '',
    brand: '',
    model: '',
    year: new Date().getFullYear(),
    color: '',
    transmission: 'AUTOMATIC',
    fuelType: 'GASOLINE',
    bodyType: 'SEDAN',
    mileage: 0,
    price: 0,
    description: '',
    condition: 'NEW',
    location: '',
    isAvailable: true
  });

  useEffect(() => {
    if (car) {
      setFormData(car);
    }
  }, [car]);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Convert string numbers to actual numbers
    const submitData = {
      ...formData,
      year: parseInt(formData.year),
      mileage: parseInt(formData.mileage),
      price: parseFloat(formData.price)
    };
    onSubmit(submitData);
  };

  return (
    <div className="car-form-overlay">
      <div className="car-form-container">
        <div className="car-form-header">
          <h2>{car ? 'Edit Car' : 'Add New Car'}</h2>
          <button className="close-btn" onClick={onCancel}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="car-form">
          <div className="form-row">
            <div className="form-group">
              <label>Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                placeholder="e.g., BMW 3 Series 320i"
              />
            </div>

            <div className="form-group">
              <label>VIN *</label>
              <input
                type="text"
                name="vin"
                value={formData.vin}
                onChange={handleChange}
                required
                maxLength="17"
                placeholder="17-character VIN"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Brand *</label>
              <input
                type="text"
                name="brand"
                value={formData.brand}
                onChange={handleChange}
                required
                placeholder="e.g., BMW"
              />
            </div>

            <div className="form-group">
              <label>Model *</label>
              <input
                type="text"
                name="model"
                value={formData.model}
                onChange={handleChange}
                required
                placeholder="e.g., 3 Series"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Year *</label>
              <input
                type="number"
                name="year"
                value={formData.year}
                onChange={handleChange}
                required
                min="1900"
                max={new Date().getFullYear() + 1}
              />
            </div>

            <div className="form-group">
              <label>Color</label>
              <input
                type="text"
                name="color"
                value={formData.color}
                onChange={handleChange}
                placeholder="e.g., Alpine White"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Transmission</label>
              <select
                name="transmission"
                value={formData.transmission}
                onChange={handleChange}
              >
                <option value="MANUAL">Manual</option>
                <option value="AUTOMATIC">Automatic</option>
                <option value="SEMI_AUTOMATIC">Semi-Automatic</option>
                <option value="CVT">CVT</option>
                <option value="DUAL_CLUTCH">Dual Clutch</option>
              </select>
            </div>

            <div className="form-group">
              <label>Fuel Type</label>
              <select
                name="fuelType"
                value={formData.fuelType}
                onChange={handleChange}
              >
                <option value="GASOLINE">Gasoline</option>
                <option value="DIESEL">Diesel</option>
                <option value="ELECTRIC">Electric</option>
                <option value="HYBRID">Hybrid</option>
                <option value="PLUG_IN_HYBRID">Plug-in Hybrid</option>
                <option value="HYDROGEN">Hydrogen</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Body Type</label>
              <select
                name="bodyType"
                value={formData.bodyType}
                onChange={handleChange}
              >
                <option value="SEDAN">Sedan</option>
                <option value="SUV">SUV</option>
                <option value="HATCHBACK">Hatchback</option>
                <option value="COUPE">Coupe</option>
                <option value="CONVERTIBLE">Convertible</option>
                <option value="WAGON">Wagon</option>
                <option value="VAN">Van</option>
                <option value="TRUCK">Truck</option>
                <option value="CROSSOVER">Crossover</option>
              </select>
            </div>

            <div className="form-group">
              <label>Condition *</label>
              <select
                name="condition"
                value={formData.condition}
                onChange={handleChange}
                required
              >
                <option value="NEW">New</option>
                <option value="USED_LIKE_NEW">Used - Like New</option>
                <option value="USED_EXCELLENT">Used - Excellent</option>
                <option value="USED_GOOD">Used - Good</option>
                <option value="USED_FAIR">Used - Fair</option>
                <option value="NEEDS_REPAIR">Needs Repair</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Mileage (km)</label>
              <input
                type="number"
                name="mileage"
                value={formData.mileage}
                onChange={handleChange}
                min="0"
                placeholder="e.g., 15000"
              />
            </div>

            <div className="form-group">
              <label>Price (€) *</label>
              <input
                type="number"
                name="price"
                value={formData.price}
                onChange={handleChange}
                required
                min="0"
                step="0.01"
                placeholder="e.g., 42500.00"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Location</label>
            <input
              type="text"
              name="location"
              value={formData.location}
              onChange={handleChange}
              placeholder="e.g., Munich, Germany"
            />
          </div>

          <div className="form-group">
            <label>Description</label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              rows="3"
              maxLength="500"
              placeholder="Brief description of the car..."
            />
          </div>

          <div className="form-group checkbox-group">
            <label>
              <input
                type="checkbox"
                name="isAvailable"
                checked={formData.isAvailable}
                onChange={handleChange}
              />
              <span>Available for Sale</span>
            </label>
          </div>

          <div className="form-actions">
            <button type="button" className="btn btn-cancel" onClick={onCancel}>
              Cancel
            </button>
            <button type="submit" className="btn btn-submit">
              {car ? 'Update Car' : 'Add Car'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CarForm;

