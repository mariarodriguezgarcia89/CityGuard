package com.example.cityguard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

object CanalNotificacion {
    private const val CHANNEL_ID = "cityguard_channel"
    private const val CHANNEL_NAME = "CityGuard"

    // Función SEGURA para crear canal
    fun crearCanalNotificaciones(context: Context) {
        // Solo para Android 8.0+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        try {
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importancia).apply {
                description = "Notificaciones de incidencias"
                // SIN vibración por ahora (más estable)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        } catch (e: Exception) {
            // Silenciar error - no es crítico
        }
    }

    // Mantener esta función para usos generales
    fun mostrarNotificacionSimple(
        context: Context,
        titulo: String,
        mensaje: String,
        targetActivity: Class<*> = MainActivity::class.java  // Parámetro opcional
    ) {
        Log.d(TAG, "mostrarNotificacionSimple llamado: $titulo")

        try {
            // 1. Verificar si tenemos canal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    crearCanalNotificaciones(context)
                }
            }

            // 2. ID único
            val notificationId = System.currentTimeMillis().toInt()

            // 3. Intent para abrir la actividad especificada
            val intent = Intent(context, targetActivity).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 4. Icono
            val smallIcon = android.R.drawable.ic_dialog_info

            // 5. Construir notificación
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            // 6. Mostrar
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())

            Log.d(TAG, "✅ Notificación simple mostrada")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error mostrando notificación: ${e.message}", e)
            Toast.makeText(context, "🔔 $mensaje", Toast.LENGTH_LONG).show()
        }
    }
    // Función para mostrar notificación que redirige a DetalleIncidenciaActivity
    fun mostrarNotificacionResuelta(
        context: Context,
        titulo: String,
        mensaje: String,
        incidencia: Incidencia  // Añadir parámetro de incidencia
    ) {
        Log.d(TAG, "mostrarNotificacionResuelta llamado para incidencia ID: ${incidencia.id}")

        try {
            // 1. Verificar si tenemos canal (crearlo si no existe)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                    Log.d(TAG, "Canal no existe, creando...")
                    crearCanalNotificaciones(context)
                }
            }

            // 2. ID único (usar el ID de la incidencia para consistencia)
            val notificationId = incidencia.id

            // 3. Intent para abrir DetalleIncidenciaActivity con la incidencia
            val intent = Intent(context, DetalleIncidenciaActivity::class.java).apply {
                putExtra("incidencia", incidencia)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 4. Icono
            val smallIcon = android.R.drawable.ic_dialog_info

            // 5. Construir notificación
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(smallIcon)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)

            // Añadir acción adicional para ver detalles
            val verDetallesIntent = Intent(context, DetalleIncidenciaActivity::class.java).apply {
                putExtra("incidencia", incidencia)
                putExtra("from_notification", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            val verDetallesPendingIntent = PendingIntent.getActivity(
                context,
                notificationId + 1, // ID diferente
                verDetallesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            builder.addAction(
                android.R.drawable.ic_menu_info_details,
                "Ver Detalles",
                verDetallesPendingIntent
            )

            // Solo vibrar si es Android < 8.0
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(longArrayOf(0, 500))
            }

            // 6. Mostrar
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())

            Log.d(TAG, "✅ Notificación mostrada para incidencia ID: ${incidencia.id}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error mostrando notificación: ${e.message}", e)
            // Si falla, mostrar Toast
            Toast.makeText(
                context,
                "🔔 $mensaje",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}