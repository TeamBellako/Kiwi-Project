package com.bellako.kiwi.features.metrics

import android.app.usage.UsageStatsManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.bellako.kiwi.services.common.Logger
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object MetricsProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMetrics(context: Context, localDate: LocalDate) : Metrics? {
        val steps = getSteps(context)
        val screenTimeSeconds = getScreenTimeInSeconds(context, localDate)

        val metricsDTO = MetricsDTO(localDate.toString(), steps, screenTimeSeconds)
        return MetricsMapper.toDomain(metricsDTO).getOrNull()
    }

    private fun getSteps(context: Context): Int {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        var steps = -1
        val latch = CountDownLatch(1)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                steps = event?.values?.get(0)?.toInt() ?: -1
                sensorManager.unregisterListener(this)
                latch.countDown()
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (stepSensor != null) {
            sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
            latch.await(100, TimeUnit.MILLISECONDS)
            sensorManager.unregisterListener(listener)
        }

        return steps
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getScreenTimeInSeconds(context: Context, localDate: LocalDate): Int {
        val zoneId = ZoneId.systemDefault()

        val startOfDayMillis = localDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDayMillis = localDate
            .atTime(LocalTime.MAX)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()

        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDayMillis,
            endOfDayMillis
        )

        if (usageStatsList.isNullOrEmpty()) {
            Logger.warn("No usage stats available. Permission may not be granted")
            return -1
        }

        return ((usageStatsList.sumOf { it.totalTimeInForeground }) / 1000).toInt()
    }
}