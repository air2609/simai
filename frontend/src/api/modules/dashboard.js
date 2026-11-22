import http from '../http'

export const getDashboardSummary = () => http.get('/dashboard/summary')
export const getLowStockItems = (params) => http.get('/dashboard/low-stock', { params })
