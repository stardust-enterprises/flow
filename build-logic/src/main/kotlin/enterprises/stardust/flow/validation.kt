package enterprises.stardust.flow

import org.gradle.api.GradleException
import java.util.*

internal val unset = UUID.randomUUID().toString()

internal fun Model<*>.validate() {
    for (field in this::class.java.declaredFields) {
        field.isAccessible = true
        val value = field.get(this)
        if (value == unset) {
            throw GradleException("${this.javaClass.simpleName} field '${field.name}' is required")
        }
    }
}

internal fun <T> Model<T>.consume(target: T) {
    if (this is Activatable) {
        if (!this.activated) {
            return
        }
    }
    this.validate()
    this.consume0(target)
}