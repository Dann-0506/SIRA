import client from './client'
import type { AlumnoResponse } from '@/types'

const BASE = '/admin/alumnos'

export const getAlumnos = () => client.get<AlumnoResponse[]>(BASE).then(r => r.data)
export const createAlumno = (data: {
  nombre: string; apellidoPaterno: string; apellidoMaterno?: string
  email: string; numControl: string
  curp?: string; fechaNacimiento?: string; carreraId?: number
}) => client.post<AlumnoResponse>(BASE, data).then(r => r.data)
export const updateAlumno = (id: number, data: {
  nombre: string; apellidoPaterno: string; apellidoMaterno?: string
  email: string; numControl: string
  curp?: string; fechaNacimiento?: string; carreraId?: number
}) => client.put<AlumnoResponse>(`${BASE}/${id}`, data).then(r => r.data)
export const toggleAlumnoEstado = (id: number, activo: boolean) =>
  client.patch(`${BASE}/${id}/estado`, null, { params: { activo } })
export const resetAlumnoPassword = (id: number) => client.post(`${BASE}/${id}/reset-password`)
export const deleteAlumno = (id: number) => client.delete(`${BASE}/${id}`)
