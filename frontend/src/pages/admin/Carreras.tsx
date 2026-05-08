import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Pencil, Trash2, ToggleLeft, ToggleRight, Plus, Loader2 } from 'lucide-react'
import axios from 'axios'
import {
  getCarreras, createCarrera, updateCarrera,
  toggleCarreraEstado, deleteCarrera,
} from '@/api/carreras'
import type { CarreraResponse } from '@/types'
import { DataTable } from '@/components/shared/DataTable'
import { FormModal } from '@/components/shared/FormModal'
import { ConfirmDialog } from '@/components/shared/ConfirmDialog'
import { PageHeader } from '@/components/shared/PageHeader'
import { StatusBadge } from '@/components/shared/StatusBadge'
import { ErrorAlert } from '@/components/shared/ErrorAlert'

const emptyForm = { clave: '', nombre: '' }

export default function Carreras() {
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<CarreraResponse | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')
  const [deleteTarget, setDeleteTarget] = useState<CarreraResponse | null>(null)
  const [toggleError, setToggleError] = useState('')
  const [togglingId, setTogglingId] = useState<number | null>(null)

  const { data: carreras = [], isLoading } = useQuery({
    queryKey: ['carreras'],
    queryFn: getCarreras,
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['carreras'] })

  const createMut = useMutation({
    mutationFn: createCarrera,
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al crear carrera.' : 'Error inesperado.'),
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: typeof emptyForm }) => updateCarrera(id, data),
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al actualizar.' : 'Error inesperado.'),
  })

  const toggleMut = useMutation({
    mutationFn: ({ id, activa }: { id: number; activa: boolean }) => toggleCarreraEstado(id, activa),
    onMutate: async ({ id, activa }) => {
      setTogglingId(id)
      setToggleError('')
      await qc.cancelQueries({ queryKey: ['carreras'] })
      const previous = qc.getQueryData<CarreraResponse[]>(['carreras'])
      qc.setQueryData<CarreraResponse[]>(['carreras'], old => old?.map(c => c.id === id ? { ...c, activa } : c) ?? [])
      return { previous }
    },
    onError: (_err, _vars, context) => {
      qc.setQueryData(['carreras'], context?.previous)
      setToggleError('No se pudo cambiar el estado. Intenta de nuevo.')
    },
    onSettled: () => {
      setTogglingId(null)
      qc.invalidateQueries({ queryKey: ['carreras'] })
      qc.invalidateQueries({ queryKey: ['carreras-activas'] })
    },
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => deleteCarrera(id),
    onSuccess: () => { invalidate(); setDeleteTarget(null) },
    onError: (err) => {
      setDeleteTarget(null)
      setToggleError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al eliminar.' : 'Error inesperado.')
    },
  })

  const openCreate = () => { setEditTarget(null); setForm(emptyForm); setFormError(''); setModalOpen(true) }
  const openEdit = (c: CarreraResponse) => {
    setEditTarget(c)
    setForm({ clave: c.clave, nombre: c.nombre })
    setFormError('')
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditTarget(null); setForm(emptyForm); setFormError('') }

  const handleSubmit = () => {
    if (!form.clave.trim()) { setFormError('La clave es requerida.'); return }
    if (!form.nombre.trim()) { setFormError('El nombre es requerido.'); return }
    const data = { clave: form.clave.trim(), nombre: form.nombre.trim() }
    if (editTarget) updateMut.mutate({ id: editTarget.id, data })
    else createMut.mutate(data)
  }

  const isPending = createMut.isPending || updateMut.isPending

  return (
    <div>
      <PageHeader
        title="Carreras"
        description="Catálogo de programas académicos disponibles en la institución."
        action={
          <button onClick={openCreate} className="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium transition-colors">
            <Plus className="h-4 w-4" />
            Nueva carrera
          </button>
        }
      />

      {toggleError && (
        <div className="mb-4">
          <ErrorAlert message={toggleError} onClose={() => setToggleError('')} />
        </div>
      )}

      <DataTable<CarreraResponse>
        data={carreras}
        isLoading={isLoading}
        keyExtractor={(c) => c.id}
        searchable
        searchKeys={['clave', 'nombre']}
        searchPlaceholder="Buscar por clave o nombre..."
        emptyMessage="No hay carreras registradas."
        columns={[
          { header: 'Clave', accessor: 'clave' },
          { header: 'Nombre', accessor: 'nombre' },
          {
            header: 'Estado',
            accessor: (c) => <StatusBadge estado={c.activa ? 'ACTIVO' : 'INACTIVO'} label={c.activa ? 'Activa' : 'Inactiva'} />,
          },
        ]}
        rowActions={(c) => (
          <div className="flex items-center justify-end gap-1">
            <button
              onClick={() => openEdit(c)}
              title="Editar"
              className="p-1.5 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-colors"
            >
              <Pencil className="h-4 w-4" />
            </button>
            <button
              onClick={() => toggleMut.mutate({ id: c.id, activa: !c.activa })}
              disabled={togglingId === c.id}
              title={c.activa ? 'Desactivar' : 'Activar'}
              className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-amber-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {togglingId === c.id
                ? <Loader2 className="h-4 w-4 animate-spin" />
                : c.activa ? <ToggleRight className="h-4 w-4" /> : <ToggleLeft className="h-4 w-4" />}
            </button>
            <button
              onClick={() => setDeleteTarget(c)}
              title="Eliminar"
              className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors"
            >
              <Trash2 className="h-4 w-4" />
            </button>
          </div>
        )}
      />

      <FormModal
        open={modalOpen}
        title={editTarget ? 'Editar carrera' : 'Nueva carrera'}
        subtitle={editTarget ? `Editando: ${editTarget.nombre}` : 'Completa los datos de la nueva carrera.'}
        onClose={closeModal}
        onSubmit={handleSubmit}
        loading={isPending}
        submitLabel={editTarget ? 'Guardar cambios' : 'Crear carrera'}
      >
        <div className="space-y-4">
          {formError && <ErrorAlert message={formError} onClose={() => setFormError('')} />}

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Clave <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={form.clave}
              onChange={(e) => setForm((p) => ({ ...p, clave: e.target.value.toUpperCase() }))}
              placeholder="Ej. ISC"
              className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">
              Nombre <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              value={form.nombre}
              onChange={(e) => setForm((p) => ({ ...p, nombre: e.target.value }))}
              placeholder="Ej. Ingeniería en Sistemas Computacionales"
              className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm text-slate-800 focus:outline-none focus:ring-2 focus:ring-blue-500/30 focus:border-blue-500 transition"
            />
          </div>
        </div>
      </FormModal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar carrera"
        description={`¿Estás seguro de que deseas eliminar "${deleteTarget?.nombre}"? Si hay alumnos asignados a esta carrera, usa "Desactivar" en su lugar.`}
        confirmLabel="Eliminar"
        variant="destructive"
        loading={deleteMut.isPending}
        onConfirm={() => deleteTarget && deleteMut.mutate(deleteTarget.id)}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
