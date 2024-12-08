@file:Suppress("PackageDirectoryMismatch")

package org.gradle.kotlin.dsl

import enterprises.stardust.flow.models.LicenseData

fun license(id: String, distribution: String = "repo") =
    LicenseData(id, "https://spdx.org/licenses/$id.html", distribution)

val mit = license("MIT")
val isc = license("ISC")
val apache2 = license("Apache-2.0")
val bsd = license("BSD-3-Clause")