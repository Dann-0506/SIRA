import client from './client'
import type { PeriodoEscolarResponse, DashboardResponse } from '@/types'

const BASE_URL = '/admin/periodos'

export const getPeriodos = () => client.get<PeriodoEscolarResponse[]>(BASE_URL).then(r => r.data)
export const getPeriodo = (id: number) => client.get<PeriodoEscolarResponse>(`${BASE_URL}/${id}`).then(r => r.data)
export const getPeriodoVigente = () => client.get<PeriodoEscolarResponse>(`${BASE_URL}/vigente`).then(r => r.data)

export const createPeriodo = (data: Partial<PeriodoEscolarResponse>) => client.post<PeriodoEscolarResponse>(BASE_URL, data).then(r => r.data)
export const updatePeriodo = (id: number, data: Partial<PeriodoEscolarResponse>) => client.put<PeriodoEscolarResponse>(`${BASE_URL}/${id}`, data).then(r => r.data)
export const deletePeriodo = (id: number) => client.delete(`${BASE_URL}/${id}`)

export const marcarPeriodoActual = (id: number) => client.post(`${BASE_URL}/${id}/actual`)

// Dashboard
export const getDashboard = () => client.get<DashboardResponse>('/admin/dashboard').then(r => r.data)

// Para compatibilidad con dashboard legado
export const getConfiguracionLegacy = () => client.get(`${BASE_URL}/configuracion-legacy`).then(r => r.data)
