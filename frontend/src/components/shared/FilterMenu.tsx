import { useState, useRef, useEffect } from 'react'
import { Filter, X, ChevronDown } from 'lucide-react'
import { cn } from '@/lib/utils'

interface Props {
  children: React.ReactNode
  onClear: () => void
  activeCount: number
}

export function FilterMenu({ children, onClear, activeCount }: Props) {
  const [open, setOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  // Cerrar al hacer clic fuera
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }
    if (open) {
      document.addEventListener('mousedown', handleClickOutside)
    }
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [open])

  return (
    <div className="relative" ref={menuRef}>
      <button
        onClick={() => setOpen(!open)}
        className={cn(
          "flex items-center gap-2 px-3 py-2 text-sm font-medium rounded-lg border transition-all",
          open || activeCount > 0
            ? "bg-blue-50 border-blue-200 text-blue-700"
            : "bg-white border-slate-200 text-slate-600 hover:bg-slate-50 hover:border-slate-300"
        )}
      >
        <Filter className={cn("h-4 w-4", activeCount > 0 ? "fill-blue-700/10" : "")} />
        <span>Filtros</span>
        {activeCount > 0 && (
          <span className="flex items-center justify-center min-w-[18px] h-[18px] px-1 text-[10px] font-bold bg-blue-600 text-white rounded-full">
            {activeCount}
          </span>
        )}
        <ChevronDown className={cn("h-3.5 w-3.5 transition-transform", open ? "rotate-180" : "")} />
      </button>

      {open && (
        <div className="absolute left-0 mt-2 w-72 bg-white rounded-xl border border-slate-200 shadow-xl shadow-slate-200/50 z-50 overflow-hidden animate-in fade-in zoom-in duration-100">
          <div className="flex items-center justify-between px-4 py-3 border-b border-slate-100 bg-slate-50/50">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Opciones de filtrado</span>
            {activeCount > 0 && (
              <button
                onClick={(e) => { e.stopPropagation(); onClear(); }}
                className="text-[11px] font-semibold text-blue-600 hover:text-blue-700 transition-colors flex items-center gap-1"
              >
                <X className="h-3 w-3" />
                Limpiar
              </button>
            )}
          </div>
          
          <div className="p-4 space-y-5">
            {children}
          </div>
        </div>
      )}
    </div>
  )
}

interface SectionProps {
  label: string
  children: React.ReactNode
}

export function FilterSection({ label, children }: SectionProps) {
  return (
    <div className="space-y-2">
      <label className="text-[11px] font-semibold text-slate-400 uppercase tracking-wide">
        {label}
      </label>
      <div className="flex flex-col gap-1.5">
        {children}
      </div>
    </div>
  )
}
