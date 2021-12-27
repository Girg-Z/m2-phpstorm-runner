package com.github.girgz.m2phpstormrunner.util

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonStringLiteral

private const val REQUIRE = "require"
private const val PACKAGE_MAGENTO_COMMUNITY = "magento/product-community-edition"

class ComposerJson constructor(private val json: JsonObject) {
    fun getProjectVersion(): String? {
        val property = this.json.findProperty("version")
        val value = property?.value
        return if (value != null && JsonStringLiteral::class.isInstance(value)) {
            (value as JsonStringLiteral).text
        } else null
    }

    fun getPackageVersion(packageName: String, section: String = REQUIRE): String? {
        val sectionProperty = this.json.findProperty(section)?.value;
        if (sectionProperty is JsonObject) {
            val packageProperty = sectionProperty.findProperty(packageName)
            val value = packageProperty?.value
            return if (value != null && JsonStringLiteral::class.isInstance(value)) {
                (value as JsonStringLiteral).text
            } else null
        } else {
            return null
        }
    }

    fun getMagentoVersion(): String? {
        val projectVersion: String? = getProjectVersion()
        val packageVersion: String? = getPackageVersion(PACKAGE_MAGENTO_COMMUNITY)
        if (projectVersion == null || packageVersion == null) {
            return projectVersion ?: packageVersion
        } else {
            if (compareVersion(projectVersion, packageVersion)){
                return projectVersion;
            }
            return packageVersion;
        }
    }

    /**
     * @return true if version1 >= version 2
     */
    private fun compareVersion(version1: String, version2: String): Boolean {
        if (version1 == version2) {
            return true;
        }

        val regex: Regex = "\\d".toRegex()
        val version1Parts = regex.findAll(version1).map { it.value }.toList()
        val version2Parts = regex.findAll(version2).map { it.value }.toList()

        for (i in 0..3) {
            val v1 = version1Parts.getOrNull(i)?.toInt()
            val v2 = version2Parts.getOrNull(i)?.toInt()
            v1 ?: return false
            v2 ?: return true

            if (v1 > v2) {
                return true
            }
        }
        return false
    }
}