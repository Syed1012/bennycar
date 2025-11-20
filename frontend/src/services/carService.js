import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/cars';

const carService = {
  // Get all cars
  getAllCars: async () => {
    const response = await axios.get(API_BASE_URL);
    return response.data;
  },

  // Get car by ID
  getCarById: async (id) => {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // Create a new car
  createCar: async (car) => {
    const response = await axios.post(API_BASE_URL, car);
    return response.data;
  },

  // Update a car
  updateCar: async (id, car) => {
    const response = await axios.put(`${API_BASE_URL}/${id}`, car);
    return response.data;
  },

  // Delete a car
  deleteCar: async (id) => {
    await axios.delete(`${API_BASE_URL}/${id}`);
  },

  // Get available cars
  getAvailableCars: async () => {
    const response = await axios.get(`${API_BASE_URL}/available`);
    return response.data;
  },

  // Get cars by brand
  getCarsByBrand: async (brand) => {
    const response = await axios.get(`${API_BASE_URL}/brand/${brand}`);
    return response.data;
  }
};

export default carService;

