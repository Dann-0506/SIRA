import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useInvalidateDashboard } from '@/hooks/useInvalidateDashboard'
import { Pencil, Trash2, KeyRound, ToggleLeft, ToggleRight, UserPlus, Loader2 } from 'lucide-react'
import axios from 'axios'
import {
  getAlumnos, createAlumno, updateAlumno,
  toggleAlumnoEstado, resetAlumnoPassword, deleteAlumno,
} from '@/api/alumnos'
import { getCarrerasActivas } from '@/api/carreras'
import type { AlumnoResponse, CarreraResponse } from '@/types'
import { DataTable } from '@/components/shared/DataTable'
import { FormModal } from '@/components/shared/FormModal'
import { ConfirmDialog } from '@/components/shared/ConfirmDialog'
import { PageHeader } from '@/components/shared/PageHeader'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { ErrorAlert } from '@/components/shared/ErrorAlert'

const emptyForm = {
  nombre: '', apellidoPaterno: '', apellidoMaterno: '',
  email: '', numControl: '',
  curp: '', fechaNacimiento: '', carreraId: '' as string,
}

export default function Alumnos() {
  const qc = useQueryClient()
  const invalidateDashboard = useInvalidateDashboard()
  const [modalOpen, setModalOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<AlumnoResponse | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')

  const [deleteTarget, setDeleteTarget] = useState<AlumnoResponse | null>(null)
  const [resetTarget, setResetTarget] = useState<AlumnoResponse | null>(null)
  const [toggleError, setToggleError] = useState('')
  const [togglingId, setTogglingId] = useState<number | null>(null)

  const { data: alumnos = [], isLoading } = useQuery({
    queryKey: ['alumnos'],
    queryFn: getAlumnos,
  })

  const { data: carreras = [] } = useQuery<CarreraResponse[]>({
    queryKey: ['carreras-activas'],
    queryFn: getCarrerasActivas,
  })

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['alumnos'] })
    invalidateDashboard()
  }

  const createMut = useMutation({
    mutationFn: createAlumno,
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al crear alumno.' : 'Error inesperado.'),
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Parameters<typeof updateAlumno>[1] }) => updateAlumno(id, data),
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al actualizar.' : 'Error inesperado.'),
  })

  const toggleMut = useMutation({
    mutationFn: ({ id, activo }: { id: number; activo: boolean }) => toggleAlumnoEstado(id, activo),
    onMutate: async ({ id, activo }) => {
      setTogglingId(id)
      setToggleError('')
      await qc.cancelQueries({ queryKey: ['alumnos'] })
      const previous = qc.getQueryData<AlumnoResponse[]>(['alumnos'])
      qc.setQueryData<AlumnoResponse[]>(['alumnos'], old => old?.map(a => a.id === id ? { ...a, activo } : a) ?? [])
      return { previous }
    },
    onError: (_err, _vars, context) => {
      qc.setQueryData(['alumnos'], context?.previous)
      setToggleError('No se pudo cambiar el estado. Intenta de nuevo.')
    },
    onSettled: () => {
      setTogglingId(null)
      qc.invalidateQueries({ queryKey: ['alumnos'] })
      invalidateDashboard()
    },
  })

  const resetMut = useMutation({
    mutationFn: (id: number) => resetAlumnoPassword(id),
    onSuccess: () => setResetTarget(null),
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => deleteAlumno(id),
    onSuccess: () => { invalidate(); setDeleteTarget(null) },
  })

  const openCreate = () => { setEditTarget(null); setForm(emptyForm); setFormError(''); setModalOpen(true) }
  const openEdit = (a: AlumnoResponse) => {
    setEditTarget(a)
    setForm({
      nombre: a.nombre,
      apellidoPaterno: a.apellidoPaterno,
      apellidoMaterno: a.apellidoMaterno ?? '',
      email: a.email ?? '',
      numControl: a.numControl,
      curp: a.curp ?? '',
      fechaNacimiento: a.fechaNacimiento ?? '',
      carreraId: a.carreraId ? String(a.carreraId) : '',
    })
    setFormError('')
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditTarget(null); setForm(emptyForm); setFormError('') }

  const handleSubmit = () => {
    if (!form.nombre.trim()) { setFormError('El nombre es requerido.'); return }
    if (!form.apellidoPaterno.trim()) { setFormError('El apellido paterno es requerido.'); return }
    if (!form.email.trim()) { setFormError('El correo electrónico es requerido.'); return }
    if (!form.numControl.trim()) { setFormError('El número de control es requerido.'); return }
    const data = {
      nombre: form.nombre.trim(),
      apellidoPaterno: form.apellidoPaterno.trim(),
      apellidoMaterno: form.apellidoMaterno.trim() || undefined,
      email: form.email.trim(),
      numControl: form.numControl.trim(),
      curp: form.curp.trim() || undefined,
      fechaNacimiento: form.fechaNacimiento || undefined,
      carreraId: form.carreraId ? Number(form.carreraId) : undefined,
    }
    if (editTarget) updateMut.mutate({ id: editTarget.id, data })
    else createMut.mutate(data)
  }

  const isPending = createMut.isPending || updateMut.isPending

  return (
    <div>
      <PageHeader
        title="Alumnos"
        description="Gestión de alumnos registrados en el sistema."
        action={
          <button onClick={openCreate} className="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium transition-colors">
            <UserPlus className="h-4 w-4" />
            Nuevo alumno
          </button>
        }
      />

      {toggleError && (
        <div className="mb-4">
          <ErrorAlert message={toggleError} onClose={() => setToggleError('')} />
        </div>
      )}

      <DataTable<AlumnoResponse>
        data={alumnos}
        isLoading={isLoading}
        keyExtractor={(a) => a.id}
        searchable
        searchKeys={['nombre', 'numControl', 'email']}
        searchPlaceholder="Buscar por nombre, núm. de control o correo..."
        emptyMessage="No hay alumnos registrados."
        columns={[
          { header: 'Núm. de control', accessor: 'numControl' },
          { header: 'Nombre', accessor: (a) => `${a.apellidoPaterno} ${a.apellidoMaterno ?? ''} ${a.nombre}`.trim() },
          { header: 'Carrera', accessor: (a) => a.carreraNombre ?? '—' },
          { header: 'Correo', accessor: (a) => a.email ?? '—' },
          {
            header: 'Estado',
            accessor: (a) => <StatusBadge estado={a.activo ? 'ACTIVO' : 'INACTIVO'} label={a.activo ? 'Activo' : 'Inactivo'} />,
          },
        ]}
        rowActions={(a) => (
          <div className="flex items-center justify-end gap-1">
            <button
              onClick={() => openEdit(a)}
              title="Editar"
              className="p-1.5 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
            >
              <Pencil className="h-4 w-4" />
            </button>
            <button
              onClick={() => toggleMut.mutate({ id: a.id, activo: !a.activo })}
              disabled={togglingId === a.id}
              title={a.activo ? 'Desactivar' : 'Activar'}
              className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-amber-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {togglingId === a.id
                ? <Loader2 className="h-4 w-4 animate-spin" />
                : a.activo ? <ToggleRight className="h-4 w-4" /> : <ToggleLeft className="h-4 w-4" />}
            </button>
            <button
              onClick={() => setResetTarget(a)}
              title="Restablecer contraseña"
              className="p-1.5 rounded-lg text-slate-400 hover:text-violet-600 hover:bg-violet-50 transition-colors"
            >
              <KeyRound className="h-4 w-4" />
            </button>
            <button
              onClick={() => setDeleteTarget(a)}
              title="Eliminar"
              className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          </div>
        )}
      />

      {/* Create / Edit Modal */}
      <FormModal
        open={modalOpen}
        title={editTarget ? 'Editar alumno' : 'Nuevo alumno'}
        subtitle={editTarget ? `Editando: ${`${editTarget.apellidoPaterno} ${editTarget.apellidoMaterno ?? ''} ${editTarget.nombre}`.trim()}` : 'Completa los datos del nuevo alumno.'}
        onClose={closeModal}
        onSubmit={handleSubmit}
        loading={isPending}
        submitLabel={editTarget ? 'Guardar cambios' : 'Crear alumno'}
      >
        <div className="space-y-4">
          {formError && <ErrorAlert message={formError} onClose={() => setFormError('')} />}

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Apellido paterno <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={form.apellidoPaterno}
                onChange={(e) => setForm((p) => ({ ...p, apellidoPaterno: e.target.value }))}
                placeholder="Ej. Pérez"
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Apellido materno
              </label>
              <input
                type="text"
                value={form.apellidoMaterno}
                onChange={(e) => setForm((p) => ({ ...p, apellidoMaterno: e.target.value }))}
                placeholder="Ej. García"
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Nombre(s) <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={form.nombre}
              onChange={(e) => setForm((p) => ({ ...p, nombre: e.target.value }))}
              placeholder="Ej. Juan"
              className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
            />
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Correo electrónico <span className="text-red-500">*</span>
              </label>
              <input
                type="email"
                required
                value={form.email}
                onChange={(e) => setForm((p) => ({ ...p, email: e.target.value }))}
                placeholder="alumno@escuela.edu"
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">
                Número de control <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                value={form.numControl}
                onChange={(e) => setForm((p) => ({ ...p, numControl: e.target.value }))}
                placeholder="Ej. 21310001"
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">CURP</label>
              <input
                type="text"
                value={form.curp}
                onChange={(e) => setForm((p) => ({ ...p, curp: e.target.value.toUpperCase() }))}
                placeholder="Ej. PEGJ010101HMCRZN00"
                maxLength={18}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Fecha de nacimiento</label>
              <input
                type="date"
                value={form.fechaNacimiento}
                onChange={(e) => setForm((p) => ({ ...p, fechaNacimiento: e.target.value }))}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">Carrera</label>
            <select
              value={form.carreraId}
              onChange={(e) => setForm((p) => ({ ...p, carreraId: e.target.value }))}
              className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
            >
              <option value="">Sin asignar</option>
              {carreras.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre} ({c.clave})</option>
              ))}
            </select>
          </div>
        </div>
      </FormModal>

      {/* Delete Confirm */}
      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar alumno"
        description={`¿Estás seguro de que deseas eliminar a "${deleteTarget ? `${deleteTarget.apellidoPaterno} ${deleteTarget.apellidoMaterno ?? ''} ${deleteTarget.nombre}`.trim() : ''}"? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        variant="destructive"
        loading={deleteMut.isPending}
        onConfirm={() => deleteTarget && deleteMut.mutate(deleteTarget.id)}
        onCancel={() => setDeleteTarget(null)}
      />

      {/* Reset Password Confirm */}
      <ConfirmDialog
        open={!!resetTarget}
        title="Restablecer contraseña"
        description={`Se restablecerá la contraseña de "${resetTarget ? `${resetTarget.apellidoPaterno} ${resetTarget.apellidoMaterno ?? ''} ${resetTarget.nombre}`.trim() : ''}" a su número de control. ¿Continuar?`}
        confirmLabel="Restablecer"
        variant="warning"
        loading={resetMut.isPending}
        onConfirm={() => resetTarget && resetMut.mutate(resetTarget.id)}
        onCancel={() => setResetTarget(null)}
      />
    </div>
  )
}
