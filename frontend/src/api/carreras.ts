import client from './client'
import type { CarreraResponse } from '@/types'

const BASE = '/admin/carreras'

export const getCarreras = () => client.get<CarreraResponse[]>(BASE).then(r => r.data)
export const getCarrerasActivas = () => client.get<CarreraResponse[]>(`${BASE}/activas`).then(r => r.data)
export const createCarrera = (data: { clave: string; nombre: string }) =>
  client.post<CarreraResponse>(BASE, data).then(r => r.data)
export const updateCarrera = (id: number, data: { clave: string; nombre: string }) =>
  client.put<CarreraResponse>(`${BASE}/${id}`, data).then(r => r.data)
export const toggleCarreraEstado = (id: number, activa: boolean) =>
  client.patch(`${BASE}/${id}/estado`, null, { params: { activa } })
export const deleteCarrera = (id: number) => client.delete(`${BASE}/${id}`)
