import http from '../http'

// Departments
export const getDepartments = (params) => http.get('/departments', { params })
export const createDepartment = (data) => http.post('/departments', data)
export const updateDepartment = (id, data) => http.put(`/departments/${id}`, data)
export const deleteDepartment = (id) => http.delete(`/departments/${id}`)

// Locations
export const getLocations = (params) => http.get('/locations', { params })
export const createLocation = (data) => http.post('/locations', data)
export const updateLocation = (id, data) => http.put(`/locations/${id}`, data)
export const deleteLocation = (id) => http.delete(`/locations/${id}`)

// Vendors
export const getVendors = (params) => http.get('/vendors', { params })
export const createVendor = (data) => http.post('/vendors', data)
export const updateVendor = (id, data) => http.put(`/vendors/${id}`, data)
export const deleteVendor = (id) => http.delete(`/vendors/${id}`)

// Categories
export const getCategories = (params) => http.get('/categories', { params })
export const createCategory = (data) => http.post('/categories', data)
export const updateCategory = (id, data) => http.put(`/categories/${id}`, data)
export const deleteCategory = (id) => http.delete(`/categories/${id}`)

// Users
export const getUsers = (params) => http.get('/users', { params })
export const createUser = (data) => http.post('/users', data)
export const updateUser = (id, data) => http.put(`/users/${id}`, data)
export const deleteUser = (id) => http.delete(`/users/${id}`)
