package com.kel5.magangyuk.util

object RelativeTime {

    fun format(timeMillis: Long, now: Long = System.currentTimeMillis()): String {
        val menit = (now - timeMillis) / 60_000
        val jam = menit / 60
        val hari = jam / 24
        return when {
            menit < 1 -> "Baru saja"
            menit < 60 -> "$menit menit lalu"
            jam < 24 -> "$jam jam lalu"
            hari < 7 -> "$hari hari lalu"
            else -> "${hari / 7} minggu lalu"
        }
    }
}