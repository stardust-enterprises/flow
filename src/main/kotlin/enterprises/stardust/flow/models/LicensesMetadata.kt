package enterprises.stardust.flow.models

import enterprises.stardust.flow.Model
import org.gradle.api.Project

data class LicenseData(
    val name: String,
    val url: String,
    val distribution: String,
)

class LicensesMetadata : Model<Project> {
    val licenses = mutableListOf<LicenseData>()

    operator fun invoke(vararg licenses: String) = licenses.forEach { it.unaryPlus() }
    operator fun invoke(block: LicensesMetadata.() -> Unit) = block()

    fun String.unaryPlus() {
        licenses.add(LicenseData(this, "https://spdx.org/licenses/${this}.html", "repo"))
    }

    fun LicenseData.unaryPlus() {
        licenses.add(this)
    }

    operator fun plusAssign(license: String) = license.unaryPlus()
    operator fun plusAssign(license: LicenseData) = license.unaryPlus()

    override fun consume0(target: Project) {
    }
}