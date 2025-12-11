package com.example.cityguard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncidenciaAdapter(
    private val lista: MutableList<Incidencia>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<IncidenciaAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(incidencia: Incidencia)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icono: ImageView = view.findViewById(R.id.ivIcono)
        val direccion: TextView = view.findViewById(R.id.tvDireccion)
        val estado: TextView = view.findViewById(R.id.tvEstado)

        init {
            view.setOnClickListener {
                listener.onItemClick(lista[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val incidencia = lista[position]
        holder.icono.setImageResource(incidencia.icono)
        holder.direccion.text = incidencia.direccion
        holder.estado.text = incidencia.estado
    }

    fun actualizarLista(nuevaLista: List<Incidencia>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}
