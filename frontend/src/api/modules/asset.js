import http from '../http'

export const getAssets = (params) => http.get('/assets', { params })
export const getAsset = (id) => http.get(`/assets/${id}`)
export const createAsset = (data) => http.post('/assets', data)
export const updateAsset = (id, data) => http.put(`/assets/${id}`, data)

export const getAssetMovements = (assetId, params) =>
  http.get(`/assets/${assetId}/movements`, { params })

export const moveAsset = (assetId, data) => http.post(`/assets/${assetId}/movements`, data)
