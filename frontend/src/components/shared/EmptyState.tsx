import { Inbox, LucideIcon } from 'lucide-react'
import { cn } from '@/lib/utils'

interface Props {
  title: string
  description?: string
  icon?: LucideIcon
  action?: React.ReactNode
  className?: string
}

export function EmptyState({ 
  title, 
  description, 
  icon: Icon = Inbox, 
  action,
  className 
}: Props) {
  return (
    <div className={cn("flex flex-col items-center justify-center py-16 px-6 text-center animate-in fade-in slide-in-from-bottom-4 duration-500", className)}>
      <div className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mb-6 border border-slate-100">
        <Icon className="h-10 w-10 text-slate-300" strokeWidth={1.5} />
      </div>
      <h3 className="text-xl font-bold text-slate-800 mb-2">{title}</h3>
      {description && (
        <p className="text-sm text-slate-400 max-w-xs mb-8 leading-relaxed font-medium">
          {description}
        </p>
      )}
      {action && (
        <div className="flex justify-center">
          {action}
        </div>
      )}
    </div>
  )
}
