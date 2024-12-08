package enterprises.stardust.flow

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.util.*

val inherit = UUID.randomUUID().toString()

internal fun String?.orInherit(name: String, other: Any?): String? {
    if (this == inherit) {
        val should = other?.toString()
        if (should != null) {
            return should
        }
        throw GradleException("Value '$name' tried to inherit from a null value")
    }
    if (this == null) {
        val should = other?.toString()
        if (should != null) {
            return should
        }
        throw GradleException("Value '$name' was null")
    }
    return this
}