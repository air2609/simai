import http from '../http'

export const getStockOpnames = (params) => http.get('/stock-opnames', { params })
export const getStockOpname = (id) => http.get(`/stock-opnames/${id}`)
export const createStockOpname = (data) => http.post('/stock-opnames', data)
export const submitStockOpname = (id) => http.post(`/stock-opnames/${id}/submit`)
export const approveStockOpname = (id) => http.post(`/stock-opnames/${id}/approve`)
