package com.stop.util

import android.os.Build

fun isMoreThanOreo(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

fun isMoreThanOreoMr1(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1

fun isUnderOreo(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.O

fun isMoreThanOreoUnderRedVelVet(): Boolean =
    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) and (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R)

fun isMoreThanSnow(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun isMoreThanRedVelVet(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun isMoreThanQ(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q