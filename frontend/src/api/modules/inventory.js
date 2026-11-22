import http from '../http'

export const getItems = (params) => http.get('/items', { params })
export const createItem = (data) => http.post('/items', data)
export const updateItem = (id, data) => http.put(`/items/${id}`, data)

export const getStockMovements = (params) => http.get('/stock-movements', { params })
export const createStockMovement = (data) => http.post('/stock-movements', data)

export const getBalances = (params) => http.get('/inventory-balances', { params })
