import React from 'react';
import './CarCard.css';

const CarCard = ({ car, onEdit, onDelete }) => {
  const formatPrice = (price) => {
    return new Intl.NumberFormat('de-DE', {
      style: 'currency',
      currency: 'EUR'
    }).format(price);
  };

  const formatCondition = (condition) => {
    return condition.replace(/_/g, ' ');
  };

  return (
    <div className="car-card">
      <div className="car-card-header">
        <h3>{car.name}</h3>
        <span className={`status ${car.isAvailable ? 'available' : 'unavailable'}`}>
          {car.isAvailable ? '✓ Available' : '✗ Unavailable'}
        </span>
      </div>

      <div className="car-card-body">
        <div className="car-info-row">
          <span className="label">Brand:</span>
          <span className="value">{car.brand}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Model:</span>
          <span className="value">{car.model}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Year:</span>
          <span className="value">{car.year}</span>
        </div>
        <div className="car-info-row">
          <span className="label">VIN:</span>
          <span className="value vin">{car.vin}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Color:</span>
          <span className="value">{car.color}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Transmission:</span>
          <span className="value">{car.transmission?.replace(/_/g, ' ')}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Fuel Type:</span>
          <span className="value">{car.fuelType?.replace(/_/g, ' ')}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Body Type:</span>
          <span className="value">{car.bodyType}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Mileage:</span>
          <span className="value">{car.mileage?.toLocaleString()} km</span>
        </div>
        <div className="car-info-row">
          <span className="label">Condition:</span>
          <span className="value">{formatCondition(car.condition)}</span>
        </div>
        <div className="car-info-row">
          <span className="label">Location:</span>
          <span className="value">{car.location}</span>
        </div>
        {car.description && (
          <div className="car-description">
            <span className="label">Description:</span>
            <p>{car.description}</p>
          </div>
        )}
        <div className="car-price">
          <span className="price">{formatPrice(car.price)}</span>
        </div>
      </div>

      <div className="car-card-actions">
        <button className="btn btn-edit" onClick={() => onEdit(car)}>
          Edit
        </button>
        <button className="btn btn-delete" onClick={() => onDelete(car.id)}>
          Delete
        </button>
      </div>
    </div>
  );
};

export default CarCard;

