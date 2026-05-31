import { CloudOff, RefreshCw } from 'lucide-react'
import { cn } from '@/lib/utils'

interface Props {
  title?: string
  message?: string
  onRetry?: () => void
  className?: string
}

export function DataError({ 
  title = "Error de conexión", 
  message = "No pudimos obtener la información del servidor. Por favor, verifica tu conexión e intenta de nuevo.", 
  onRetry,
  className
}: Props) {
  return (
    <div className={cn("flex flex-col items-center justify-center py-12 px-6 text-center animate-in fade-in zoom-in duration-300", className)}>
      <div className="w-16 h-16 bg-red-50 rounded-2xl flex items-center justify-center mb-4 border border-red-100">
        <CloudOff className="h-8 w-8 text-red-500" />
      </div>
      <h3 className="text-lg font-bold text-slate-900 mb-2">{title}</h3>
      <p className="text-sm text-slate-500 max-w-sm mb-6 leading-relaxed">
        {message}
      </p>
      {onRetry && (
        <button
          onClick={onRetry}
          className="flex items-center gap-2 px-5 py-2.5 bg-blue-600 hover:bg-blue-700 text-white rounded-xl text-sm font-semibold transition-all shadow-lg shadow-blue-200 active:scale-95"
        >
          <RefreshCw className="h-4 w-4" />
          Reintentar conexión
        </button>
      )}
    </div>
  )
}
