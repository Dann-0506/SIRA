import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Pencil, Trash2, CheckCircle2, Loader2 } from 'lucide-react'
import axios from 'axios'
import { 
  getPeriodos, createPeriodo, updatePeriodo, 
  deletePeriodo, marcarPeriodoActual 
} from '@/api/periodos'
import type { PeriodoEscolarResponse } from '@/types'
import { PageHeader } from '@/components/shared/PageHeader'
import { DataTable } from '@/components/shared/DataTable'
import { FormModal } from '@/components/shared/FormModal'
import { ConfirmDialog } from '@/components/shared/ConfirmDialog'
import { ErrorAlert } from '@/components/shared/ErrorAlert'
import { DataError } from '@/components/shared/DataError'

const emptyForm = {
  nombrePeriodo: '',
  fechaInicioPeriodo: '',
  fechaFinPeriodo: '',
  calificacionMinimaAprobatoria: 70,
  calificacionMaximaPosible: 100,
  esPeriodoActual: false
}

export default function Periodos() {
  const qc = useQueryClient()
  const [modalOpen, setModalOpen] = useState(false)
  const [editTarget, setEditTarget] = useState<PeriodoEscolarResponse | null>(null)
  const [deleteTarget, setPeriodoDelete] = useState<PeriodoEscolarResponse | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [formError, setFormError] = useState('')

  const { data: periodos = [], isLoading, error, refetch } = useQuery({
    queryKey: ['periodos'],
    queryFn: getPeriodos
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['periodos'] })

  const createMut = useMutation({
    mutationFn: createPeriodo,
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al crear.' : 'Error inesperado.')
  })

  const updateMut = useMutation({
    mutationFn: (data: PeriodoEscolarResponse) => updatePeriodo(data.id, data),
    onSuccess: () => { invalidate(); closeModal() },
    onError: (err) => setFormError(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al actualizar.' : 'Error inesperado.')
  })

  const deleteMut = useMutation({
    mutationFn: (id: number) => deletePeriodo(id),
    onSuccess: () => { invalidate(); setPeriodoDelete(null) },
    onError: (err) => {
      setPeriodoDelete(null)
      alert(axios.isAxiosError(err) ? err.response?.data?.error ?? 'Error al eliminar.' : 'Error inesperado.')
    }
  })

  const actualMut = useMutation({
    mutationFn: marcarPeriodoActual,
    onSuccess: () => invalidate()
  })

  const openCreate = () => { setEditTarget(null); setForm(emptyForm); setFormError(''); setModalOpen(true) }
  const openEdit = (p: PeriodoEscolarResponse) => {
    setEditTarget(p)
    setForm({ ...p })
    setFormError('')
    setModalOpen(true)
  }
  const closeModal = () => { setModalOpen(false); setEditTarget(null); setForm(emptyForm); setFormError('') }

  const handleSubmit = () => {
    if (!form.nombrePeriodo.trim()) return setFormError('El nombre es requerido.')
    if (!form.fechaInicioPeriodo || !form.fechaFinPeriodo) return setFormError('Las fechas son requeridas.')
    
    if (editTarget) updateMut.mutate({ ...form, id: editTarget.id } as PeriodoEscolarResponse)
    else createMut.mutate(form)
  }

  if (error) return <DataError onRetry={() => refetch()} />

  return (
    <div>
      <PageHeader
        title="Periodos Escolares"
        description="Administra los ciclos académicos, fechas y criterios de evaluación global."
        action={
          <button onClick={openCreate} className="flex items-center gap-2 px-4 py-2 rounded-lg bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium transition-colors">
            <Plus className="h-4 w-4" />
            Nuevo periodo
          </button>
        }
      />

      <DataTable<PeriodoEscolarResponse>
        data={periodos}
        isLoading={isLoading}
        keyExtractor={(p) => p.id}
        emptyMessage="No hay periodos registrados."
        columns={[
          { header: 'Periodo', accessor: 'nombrePeriodo' },
          { header: 'Inicio', accessor: 'fechaInicioPeriodo' },
          { header: 'Fin', accessor: 'fechaFinPeriodo' },
          { header: 'Mínimo', accessor: 'calificacionMinimaAprobatoria' },
          { header: 'Máximo', accessor: 'calificacionMaximaPosible' },
          { 
            header: 'Estado', 
            accessor: (p) => p.esPeriodoActual ? (
              <span className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-100 text-emerald-700 border border-emerald-200">
                <CheckCircle2 className="h-3 w-3" /> ACTUAL
              </span>
            ) : (
              <span className="text-[10px] font-semibold text-slate-400">HISTÓRICO</span>
            )
          }
        ]}
        rowActions={(p) => (
          <div className="flex items-center justify-end gap-1">
            {!p.esPeriodoActual && (
              <button 
                onClick={() => actualMut.mutate(p.id)}
                disabled={actualMut.isPending}
                title="Marcar como actual"
                className="p-1.5 rounded-lg text-slate-400 hover:text-emerald-600 hover:bg-emerald-50 transition-colors"
              >
                {actualMut.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
              </button>
            )}
            <button onClick={() => openEdit(p)} title="Editar" className="p-1.5 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition-colors">
              <Pencil className="h-4 w-4" />
            </button>
            <button onClick={() => setPeriodoDelete(p)} title="Eliminar" className="p-1.5 rounded-lg text-slate-400 hover:text-red-600 hover:bg-red-50 transition-colors">
              <Trash2 className="h-4 w-4" />
            </button>
          </div>
        )}
      />

      <FormModal
        open={modalOpen}
        title={editTarget ? 'Editar periodo' : 'Nuevo periodo'}
        onClose={closeModal}
        onSubmit={handleSubmit}
        loading={createMut.isPending || updateMut.isPending}
      >
        <div className="space-y-4">
          {formError && <ErrorAlert message={formError} onClose={() => setFormError('')} />}
          
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">Nombre del periodo</label>
            <input 
              type="text" 
              value={form.nombrePeriodo} 
              onChange={e => setForm(p => ({ ...p, nombrePeriodo: e.target.value }))}
              placeholder="Ej. Enero - Junio 2026"
              className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-blue-500/30 outline-none transition"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Fecha inicio</label>
              <input 
                type="date" 
                value={form.fechaInicioPeriodo} 
                onChange={e => setForm(p => ({ ...p, fechaInicioPeriodo: e.target.value }))}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-blue-500/30 outline-none transition"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Fecha fin</label>
              <input 
                type="date" 
                value={form.fechaFinPeriodo} 
                onChange={e => setForm(p => ({ ...p, fechaFinPeriodo: e.target.value }))}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-blue-500/30 outline-none transition"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4 pt-2">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Mín. aprobatorio</label>
              <input 
                type="number" 
                value={form.calificacionMinimaAprobatoria} 
                onChange={e => setForm(p => ({ ...p, calificacionMinimaAprobatoria: Number(e.target.value) }))}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-blue-500/30 outline-none transition"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Calif. máxima</label>
              <input 
                type="number" 
                value={form.calificacionMaximaPosible} 
                onChange={e => setForm(p => ({ ...p, calificacionMaximaPosible: Number(e.target.value) }))}
                className="w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-blue-500/30 outline-none transition"
              />
            </div>
          </div>
        </div>
      </FormModal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar periodo"
        description={`¿Estás seguro de que deseas eliminar "${deleteTarget?.nombrePeriodo}"? Esta acción no se puede deshacer si existen grupos asociados.`}
        confirmLabel="Eliminar"
        variant="destructive"
        loading={deleteMut.isPending}
        onConfirm={() => deleteTarget && deleteMut.mutate(deleteTarget.id)}
        onCancel={() => setPeriodoDelete(null)}
      />
    </div>
  )
}
